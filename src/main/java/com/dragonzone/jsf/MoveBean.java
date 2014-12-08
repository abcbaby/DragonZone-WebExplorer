package com.dragonzone.jsf;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class MoveBean implements Serializable {
    private static final long serialVersionUID = 553241222920856765L;

    private TreeNode rootNode;
    private TreeNode selectedNode;
    private String selectedDrive;
    private File selectedFile;
    private List<File> defaultPathList;

    public List<File> getDrives() {
        return Arrays.asList(File.listRoots());
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

}
