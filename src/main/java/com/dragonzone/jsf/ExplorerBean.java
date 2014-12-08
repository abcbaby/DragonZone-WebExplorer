package com.dragonzone.jsf;

import com.dragonzone.jsf.util.MediaFileUtil;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class ExplorerBean implements Serializable {

    private static final long serialVersionUID = -3980094244475127787L;

    @ManagedProperty("#{mediaFileUtil}")
    private MediaFileUtil mediaFileUtil;
    private TreeNode rootNode;
    private TreeNode selectedNode;
    private String selectedDrive;
    private File selectedFile;
    private String newFilename;
    private List<File> fileList;
    private File[] selectedFiles;
    private List<File> defaultPathList;
    private String searchFile;
    private boolean search;
    private boolean searchMp3Meta;

    public List<File> getDrives() {
        return Arrays.asList(File.listRoots());
    }

    public List<File> getImageFiles() {
        List<File> removeFiles = new ArrayList<>();
        List<File> listFiles = new ArrayList<>(getFileList());
        for (File file : listFiles) {
            if (!mediaFileUtil.isImage(file)) {
                removeFiles.add(file);
            }
        }

        listFiles.removeAll(removeFiles);

        return listFiles;
    }

    public List<File> getMediaFiles() {
        List<File> removeFiles = new ArrayList<>();
        List<File> listFiles = new ArrayList<>(getFileList());
        for (File file : listFiles) {
            if (!mediaFileUtil.isMedia(file)) {
                removeFiles.add(file);
            }
        }

        listFiles.removeAll(removeFiles);

        return listFiles;
    }

    public List<File> getUnknownFiles() {
        List<File> removeFiles = new ArrayList<>();
        List<File> listFiles = new ArrayList<>(getFileList());
        for (File file : listFiles) {
            if (mediaFileUtil.isImage(file) || mediaFileUtil.isMedia(file)) {
                removeFiles.add(file);
            }
        }

        listFiles.removeAll(removeFiles);

        return listFiles;
    }

    public Date getDate(long timestamp) {
        return new Date(timestamp);
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public String getSelectedDrive() {
        return selectedDrive;
    }

    public void setSelectedDrive(String selectedDrive) {
        this.selectedDrive = selectedDrive;
    }

    /**
     * @return the selectedFile
     */
    public File getSelectedFile() {
        return selectedFile;
    }

    /**
     * @param selectedFile the selectedFile to set
     */
    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    /**
     * @return the newFilename
     */
    public String getNewFilename() {
        return newFilename;
    }

    /**
     * @param newFilename the newFilename to set
     */
    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    /**
     * @return the fileList
     */
    public List<File> getFileList() {
        return fileList;
    }

    /**
     * @param fileList the fileList to set
     */
    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    /**
     * @param mediaFileUtil the mediaFileUtil to set
     */
    public void setMediaFileUtil(MediaFileUtil mediaFileUtil) {
        this.mediaFileUtil = mediaFileUtil;
    }

    /**
     * @return the defaultPathList
     */
    public List<File> getDefaultPathList() {
        return defaultPathList;
    }

    /**
     * @param defaultPathList the defaultPathList to set
     */
    public void setDefaultPathList(List<File> defaultPathList) {
        this.defaultPathList = defaultPathList;
    }

    /**
     * @return the selectedFiles
     */
    public File[] getSelectedFiles() {
        return selectedFiles;
    }

    /**
     * @param selectedFiles the selectedFiles to set
     */
    public void setSelectedFiles(File[] selectedFiles) {
        this.selectedFiles = selectedFiles;
    }

    /**
     * @return the searchFile
     */
    public String getSearchFile() {
        return searchFile;
    }

    /**
     * @param searchFile the searchFile to set
     */
    public void setSearchFile(String searchFile) {
        this.searchFile = searchFile;
    }

    /**
     * @return the search
     */
    public boolean isSearch() {
        return search;
    }

    /**
     * @param search the search to set
     */
    public void setSearch(boolean search) {
        this.search = search;
    }

    /**
     * @return the searchMp3Meta
     */
    public boolean isSearchMp3Meta() {
        return searchMp3Meta;
    }

    /**
     * @param searchMp3Meta the searchMp3Meta to set
     */
    public void setSearchMp3Meta(boolean searchMp3Meta) {
        this.searchMp3Meta = searchMp3Meta;
    }

}
