package com.dragonzone.jsf;

import com.dragonzone.jsf.util.MessageUtil;
import com.dragonzone.service.FileDirectoryService;
import com.dragonzone.util.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.apache.commons.io.FileUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@RequestScoped
public class MoveControlBean extends BaseControlBean {
    final static Logger logger = LoggerFactory.getLogger(MoveControlBean.class);

    @ManagedProperty("#{applicationConstants}")
    private ApplicationConstants applicationConstants;
    @ManagedProperty("#{moveBean}")
    private MoveBean moveBean;
    @ManagedProperty("#{fileDirectoryService}")
    private FileDirectoryService fileDirectoryService;
    @ManagedProperty("#{explorerBean}")
    private ExplorerBean explorerBean;

    @Override
    public void preLoadPage() {
        moveBean.setDefaultPathList(fileDirectoryService.getDefaultPathList());
        List<File> pathList = moveBean.getDefaultPathList();

        moveBean.setSelectedDrive(pathList.get(0).getAbsolutePath());
        moveBean.setRootNode(
                fileDirectoryService.createFolder(moveBean.getSelectedDrive(),
                        getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN)));
        moveBean.setSelectedNode(moveBean.getRootNode());
        fileDirectoryService.loadSubDirectories(moveBean.getSelectedNode(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
    }

    public void move() {
        if (moveBean.getSelectedNode() == null) {
            MessageUtil.addErrorMessageByString("A folder must be selected to move!");
        } else {
            File fileFrom = explorerBean.getSelectedFile();
            boolean isFromDir = fileFrom.isDirectory();
            File dirTo = (File) moveBean.getSelectedNode().getData();

            try {
                FileUtils.moveToDirectory(fileFrom, dirTo, true);

                // refresh folder
                reloadMainExplorer(explorerBean.getSelectedNode(), isFromDir);

                RequestContext context = RequestContext.getCurrentInstance();
                context.addCallbackParam("validMove", true);

                MessageUtil.addInfoMessageByString(fileFrom.getAbsolutePath()
                        + " moved to " + dirTo.getAbsolutePath() + " was successful.");
            } catch (Exception e) {
                MessageUtil.addErrorMessageByString("Folder cannot be moved!");
                logger.error("Error trying to move from " + fileFrom.getAbsolutePath()
                        + " to " + dirTo.getAbsolutePath(), e);
            }
        }
    }

    private void reloadMainExplorer(TreeNode node, boolean dir) {
        if (dir) {
            if (explorerBean.getSelectedDrive() != null) {
                explorerBean.setRootNode(fileDirectoryService.createFolder(explorerBean.getSelectedDrive(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN)));
                explorerBean.setSelectedNode(explorerBean.getRootNode());
            }
        } else {
            explorerBean.setSelectedNode(node);
        }
        explorerBean.setSearch(false);
        fileDirectoryService.loadSubDirectories(node, getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
        loadFileList();
    }

    private void loadFileList() {
        List<File> listFiles = new ArrayList<>();
        if (explorerBean.getSelectedNode() != null) {
            listFiles = FileUtil.getFiles((File) explorerBean.getSelectedNode().getData());
        }
        explorerBean.setFileList(listFiles);
    }

    private void nodeSelect(TreeNode node) {
        moveBean.setSelectedNode(node);
        fileDirectoryService.loadSubDirectories(node, getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
    }

    public void onNodeSelect(NodeSelectEvent event) {
        TreeNode node = event.getTreeNode();
        nodeSelect(node);
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        TreeNode node = event.getTreeNode();
        TreeNode tn = fileDirectoryService.getNode(moveBean.getRootNode(), node);
        if (tn != null) {
            tn.setExpanded(false);
            moveBean.setSelectedNode(tn);
        }
    }

    public void onChangePath() {
        if (moveBean.getSelectedDrive() != null) {
            moveBean.setRootNode(fileDirectoryService.createFolder(moveBean.getSelectedDrive(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN)));
            moveBean.setSelectedNode(moveBean.getRootNode());
            fileDirectoryService.loadSubDirectories(moveBean.getSelectedNode(), getSecurityBean().hasRole(ApplicationConstants.ROLE_ADMIN));
        }
    }

    public void setFileDirectoryService(FileDirectoryService fileDirectoryService) {
        this.fileDirectoryService = fileDirectoryService;
    }

    public void setMoveBean(MoveBean moveBean) {
        this.moveBean = moveBean;
    }

    /**
     * @param applicationConstants the applicationConstants to set
     */
    public void setApplicationConstants(ApplicationConstants applicationConstants) {
        this.applicationConstants = applicationConstants;
    }

    /**
     * @param explorerBean the explorerBean to set
     */
    public void setExplorerBean(ExplorerBean explorerBean) {
        this.explorerBean = explorerBean;
    }
}
