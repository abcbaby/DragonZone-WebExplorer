package com.dragonzone.jsf;

import com.dragonzone.service.FileDirectoryService;
import com.dragonzone.util.FileUtil;
import com.dragonzone.jsf.util.MessageUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

@ManagedBean
@RequestScoped
public class ExplorerControlBean extends BaseControlBean {

    private static final int MINIMUM_SEARCH_LENGTH = 2;
    @ManagedProperty("#{applicationConstants}")
    private ApplicationConstants applicationConstants;
    @ManagedProperty("#{explorerBean}")
    private ExplorerBean explorerBean;
    @ManagedProperty("#{fileDirectoryService}")
    private FileDirectoryService fileDirectoryService;
    private final SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.FORMAT_TIMESTAMP);

    @Override
    public void preLoadPage() {
        explorerBean.setDefaultPathList(fileDirectoryService.getDefaultPathList());
        List<File> pathList = explorerBean.getDefaultPathList();
        
        explorerBean.setSelectedDrive(pathList.get(0).getAbsolutePath());
        explorerBean.setRootNode(
                fileDirectoryService.createFolder(explorerBean.getSelectedDrive(),
                        getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN)));
        explorerBean.setSelectedNode(explorerBean.getRootNode());
        fileDirectoryService.loadSubDirectories(explorerBean.getSelectedNode(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
        loadFileList();
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile tempFile = event.getFile();
        File newFile = new File((File) explorerBean.getSelectedNode().getData(), tempFile.getFileName());
        if (newFile.exists()) {
            MessageUtil.addErrorMessageByString(event.getFile().getFileName() + " already exists.");
        } else {
            try (FileOutputStream fos = new FileOutputStream(newFile);) {
                fos.write(tempFile.getContents());
                nodeSelect(explorerBean.getSelectedNode()); // refresh list w/ new item
                MessageUtil.addInfoMessageByString(event.getFile().getFileName() + " is uploaded.");
            } catch (IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        "Error trying to upload file: " + newFile.getAbsolutePath(), e);
            }
        }
    }

    public void searchFiles() throws Exception {
        if (StringUtils.strip(explorerBean.getSearchFile()).length() < MINIMUM_SEARCH_LENGTH) {
            MessageUtil.addErrorMessageByString("You need a mininum of " + MINIMUM_SEARCH_LENGTH + " characters to search!");
        } else {
            explorerBean.setSearch(true);
            explorerBean.setFileList(new ArrayList<File>()); // init list
            FileUtil.searchFiles((File) explorerBean.getSelectedNode().getData(),
                    explorerBean.getSearchFile(),
                    explorerBean.getFileList(),
                    explorerBean.isSearchMp3Meta());
        }
    }

    public void deleteSelectedFile() {
        File deleteFile = explorerBean.getSelectedFile();
        if (deleteFile.exists()) {
            if (deleteFile.delete()) {
                loadFileList();
                MessageUtil.addInfoMessageByString(deleteFile.getName() + " deleted.");
            } else {
                MessageUtil.addErrorMessageByString("Cannot delete file: " + deleteFile.getName() + ".");
            }
        }
    }

    public void deleteFolder() {
        File folder = explorerBean.getSelectedFile();
        TreeNode currentNode = fileDirectoryService.getNode(explorerBean.getRootNode(), folder);
        if (folder.exists() && folder.isDirectory()) {
            if (folder.listFiles().length == 0) {
                if (folder.delete()) {
                    // refresh folder
                    TreeNode parentNode = currentNode.getParent();
                    parentNode.getChildren().remove(currentNode);
                    nodeSelect(parentNode);
                    MessageUtil.addInfoMessageByString(folder.getName() + " deleted.");
                } else {
                    MessageUtil.addErrorMessageByString("Cannot delete folder: " + folder.getName() + ".");
                }
            } else {
                MessageUtil.addErrorMessageByString("Cannot delete folder: " + folder.getName() + ", delete all children first.");
            }
        }
    }

    public void createFolder() {
        File folder = explorerBean.getSelectedFile();
        TreeNode currentNode = fileDirectoryService.getNode(explorerBean.getRootNode(), folder);
        File newFolder = new File(folder, explorerBean.getNewFilename());
        boolean createFolder = true;
        if (newFolder.exists()) {
            if (newFolder.isDirectory()) {
                MessageUtil.addErrorMessageByString("Folder already exists!");
                createFolder = false;
            }
        }
        if (createFolder) {
            if (newFolder.mkdir()) {
                // refresh folder
                nodeSelect(currentNode);

                MessageUtil.addInfoMessageByString(newFolder.getName() + " created.");
            } else {
                MessageUtil.addErrorMessageByString("Error creating folder: " + folder.getName() + ".");
            }
        }
    }

    public void renameFolder() {
        File folder = explorerBean.getSelectedFile();
        TreeNode currentNode = fileDirectoryService.getNode(explorerBean.getRootNode(), folder);
        File newFolder = new File(folder.getParent(), explorerBean.getNewFilename());
        boolean renameFolder = true;
        if (newFolder.exists()) {
            if (newFolder.isDirectory()) {
                MessageUtil.addErrorMessageByString("Folder already exists!");
                renameFolder = false;
            }
        }
        if (renameFolder) {
            if (folder.renameTo(newFolder)) {
                // refresh folder
                TreeNode parentNode = currentNode.getParent();
                TreeNode renameTreeNode = new DefaultTreeNode(newFolder, parentNode);
                renameTreeNode.setExpanded(true);
                parentNode.getChildren().remove(currentNode);
                nodeSelect(renameTreeNode);
                MessageUtil.addInfoMessageByString(newFolder.getName() + " renamed.");
            } else {
                MessageUtil.addErrorMessageByString("Error renaming folder: " + folder.getName() + ".");
            }
        }
    }

    public String getDisplayFolderFullPath() {
        String fullPath = "";
        if (explorerBean.getSelectedNode() != null) {
            File dir = (File) explorerBean.getSelectedNode().getData();
            fullPath = dir.getAbsolutePath();
        }
        return fullPath;
    }

    public String getDisplayFileInfo(File file) {
        return applicationConstants.abbreviateFileName(file.getName())
                + " - " + sdf.format(explorerBean.getDate(file.lastModified()));
    }

    public void viewFolder(String path) {
        explorerBean.setSearch(false);
        File folder = new File(path);
        explorerBean.setSelectedDrive(fileDirectoryService.getParentFolders(folder).get(0).getAbsolutePath());
        TreeNode pathTreeNode = fileDirectoryService.createAllFoldersFor(path,
                getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
        explorerBean.setSelectedNode(pathTreeNode);
        explorerBean.setRootNode(fileDirectoryService.getRootNode(pathTreeNode));
        loadFileList();
    }

    private void nodeSelect(TreeNode node) {
        explorerBean.setSearch(false);
        explorerBean.setSelectedNode(node);
        fileDirectoryService.loadSubDirectories(node, getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
        loadFileList();
    }

    public void onNodeSelect(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        nodeSelect(node);
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        TreeNode node = event.getTreeNode();
        TreeNode tn = fileDirectoryService.getNode(explorerBean.getRootNode(), node);
        if (tn != null) {
            tn.setExpanded(false);
            explorerBean.setSelectedNode(tn);
        }
    }

    public void onChangePath() {
        explorerBean.setSearch(false);
        if (explorerBean.getSelectedDrive() != null) {
            explorerBean.setRootNode(fileDirectoryService.createFolder(explorerBean.getSelectedDrive(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN)));
            explorerBean.setSelectedNode(explorerBean.getRootNode());
            fileDirectoryService.loadSubDirectories(explorerBean.getSelectedNode(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
            loadFileList();
        }
    }

    public void createSubFolder() {
        File dir = (File) explorerBean.getSelectedNode().getData();
        modifySubFolder(dir);
    }

    public void modifySubFolder(File dir) {
        explorerBean.setSelectedFile(dir);
    }

    public void renameFile() {
        File folder = (File) explorerBean.getSelectedNode().getData();
        File oldFile = explorerBean.getSelectedFile();
        File newFile = new File(folder, explorerBean.getNewFilename());
        if (newFile.exists()) {
            MessageUtil.addErrorMessageByString("Cannot rename, file already exists!");
        } else {
            oldFile.renameTo(newFile);
            loadFileList();
            MessageUtil.addInfoMessageByString("Rename was successful.");
        }
    }

    private void loadFileList() {
        List<File> listFiles = new ArrayList<>();
        if (explorerBean.getSelectedNode() != null) {
            listFiles = FileUtil.getFiles((File) explorerBean.getSelectedNode().getData());
        }
        explorerBean.setFileList(listFiles);
    }

    public void checkZipFileTotalSize() throws IOException {
        if (FileUtil.validZipSize(Arrays.asList(explorerBean.getSelectedFiles()))) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.addCallbackParam("validZipFileSizeLimit", true);
        } else {
            MessageUtil.addErrorMessageByString("Zip file(s) exceeded size limit: "
                    + FileUtil.humanReadableByteCount(FileUtil.ZIP_FILE_SIZE_LIMIT, false));
        }
    }

    public void checkZipFolderTotalSize() throws IOException {
        File dir = explorerBean.getSelectedFile();
        if (dir.isDirectory()) {
            List<File> zipFileList = new ArrayList<>();
            FileUtil.getFilesAndSubFolderFiles(zipFileList, dir);
            if (FileUtil.validZipSize(zipFileList)) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.addCallbackParam("validZipFolderSizeLimit", true);
            } else {
                MessageUtil.addErrorMessageByString("Zip folder, '" + dir.getName() + "', exceeded size limit: "
                        + FileUtil.humanReadableByteCount(FileUtil.ZIP_FILE_SIZE_LIMIT, false));
            }
        }
    }

    public void zipFiles() throws IOException {
        if (explorerBean.getSelectedFiles() != null
                && explorerBean.getSelectedFiles().length > 0) {
            try {
                File zipFile = FileUtil.zipFiles(Arrays.asList(explorerBean.getSelectedFiles()), null);
                explorerBean.setSelectedFile(zipFile);
                downloadFile();
            } catch (IOException e) {
                MessageUtil.addErrorMessageByString(e.getMessage());
            }
        }
    }

    public void zipFolder(File dir) throws IOException {
        if (dir.isDirectory()) {
            List<File> zipFileList = new ArrayList<>();
            FileUtil.getFilesAndSubFolderFiles(zipFileList, dir);
            try {
                File zipFile = FileUtil.zipFiles(zipFileList, dir.getAbsolutePath());
                explorerBean.setSelectedFile(zipFile);
                downloadFile();
            } catch (IOException e) {
                MessageUtil.addErrorMessageByString(e.getMessage());
            }
        }
    }

    public void downloadFile() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        String contentType = Files.probeContentType(explorerBean.getSelectedFile().toPath());
        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        ec.setResponseContentLength(Long.valueOf(explorerBean.getSelectedFile().length()).intValue()); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition",
                "attachment; filename=\"" + explorerBean.getSelectedFile().getName() + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

        OutputStream output = ec.getResponseOutputStream();
        try (FileInputStream fis = new FileInputStream(explorerBean.getSelectedFile())) {
            output.write(IOUtils.toByteArray(fis));
        }

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }

    public String getReadableFileSize(File file) {
        return FileUtil.humanReadableByteCount(file.length(), true);
    }

    public void loadViewDataPage() {
        String url = "/viewData.jsf?fileId=" + encryptAndEncode(explorerBean.getSelectedFile().getAbsolutePath());

        redirectPage(url);
    }

    public void loadRename(File file) {
        modifySubFolder(file);
        explorerBean.setNewFilename(file.getName());
    }

    public void setFileDirectoryService(FileDirectoryService fileDirectoryService) {
        this.fileDirectoryService = fileDirectoryService;
    }

    public void setExplorerBean(ExplorerBean explorerBean) {
        this.explorerBean = explorerBean;
    }

    /**
     * @param applicationConstants the applicationConstants to set
     */
    public void setApplicationConstants(ApplicationConstants applicationConstants) {
        this.applicationConstants = applicationConstants;
    }
}
