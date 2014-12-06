package com.dragonzone.service;

import com.dragonzone.jsf.BaseBean;
import com.dragonzone.spring.AppProperties;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@ApplicationScoped
public class FileDirectoryService {

    public static final String DEFAULT_ROOT_PATH = "C:\\";
    @ManagedProperty("#{appProperties}")
    private AppProperties appProperties;

    public List<File> getDefaultPathList() {
        List<File> fileList = new ArrayList<>();
        for (String strPath : appProperties.getDefaultPathList()) {
            File file = new File(strPath);
            if (file.exists()) {
                fileList.add(file);
            } else {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, 
                        "Path does not exists: {0}", file.getAbsolutePath());
            }
        }
        if (fileList.isEmpty()) {
        	String tempDirName = System.getenv("TEMP");
        	File tempDir = new File(tempDirName);
        	fileList.add(tempDir);
        	Logger.getLogger(getClass().getName()).log(Level.WARNING, "Defaulting to sharing " + tempDirName);
        }
        return fileList;
    }
    
    public boolean isDefaultPath(File file) {
        boolean hasDefaultPath = false;
        List<File> defaultPathList = getDefaultPathList();
        for (File defaultPath : defaultPathList) {
            if (defaultPath.equals(file)) {
                hasDefaultPath = true;
                break;
            }
        }
        return hasDefaultPath;
    }

    public List<File> getParentFolders(File file) {
        File cloneFile = file.getAbsoluteFile();
        List<File> dirList = new ArrayList<>();

        if (file.isDirectory()) {
            dirList.add(file);
        }
        File dir;
        while ((dir = cloneFile.getParentFile()) != null) {
            dirList.add(0, dir.getAbsoluteFile());
            cloneFile = dir;
            if (isDefaultPath(dir)) {
                break;
            }
        }
        return dirList;
    }

    public TreeNode createAllFoldersFor(String path, boolean viewHiddenFiles) {
        TreeNode treeNode = null;
        File folder = new File(path);

        List<File> dirList = getParentFolders(folder);
        if (dirList != null) {
            for (int i = 0; i < dirList.size(); i++) {
                File dir = dirList.get(i);
                if (i == 0) {
                    treeNode = new DefaultTreeNode(dir, null);
                } else {
                    treeNode = getNode(treeNode, dir);
                }
                loadSubDirectories(treeNode, viewHiddenFiles);
            }
        }

        return treeNode;
    }

    public TreeNode createFolder(String rootPath, boolean viewHiddenFiles) {
        File rootFile = new File(rootPath == null ? DEFAULT_ROOT_PATH : rootPath);
        TreeNode rootNode = new DefaultTreeNode(rootFile, null);

        loadSubDirectories(rootNode, viewHiddenFiles);

        return rootNode;
    }

    public void loadSubDirectories(TreeNode parentNode, boolean viewHiddenFiles) {
        if (parentNode.isLeaf()) {
            File parentDir = (File) parentNode.getData();
            File[] files = parentDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        boolean canView = true;
                        if (file.isHidden() && !viewHiddenFiles) {
                            canView = false;
                        }
                        if (canView) {
                            TreeNode node = new DefaultTreeNode(file, parentNode);
                            node.setExpanded(true);
                        }
                    }
                }
            }
        }
    }

    public TreeNode getRootNode(TreeNode treeNode) {
        TreeNode rootNode = null;
        while (treeNode != null && treeNode.getParent() != null) {
            rootNode = treeNode.getParent();
            treeNode = treeNode.getParent();
        }
        return rootNode;
    }

    public TreeNode getNode(TreeNode rootNode, TreeNode searchNode) {
        TreeNode foundTreeNode = null;
        if (searchNode.equals(rootNode)) {
            foundTreeNode = rootNode;
        } else {
            List<TreeNode> treeNodeList = rootNode.getChildren();
            for (TreeNode tn : treeNodeList) {
                if (searchNode.equals(tn)) {
                    foundTreeNode = tn;
                    break;
                } else {
                    List<TreeNode> treeNodeChildrenList = tn.getChildren();
                    for (TreeNode child : treeNodeChildrenList) {
                        foundTreeNode = getNode(child, searchNode);
                        if (foundTreeNode != null) {
                            break;
                        }
                    }
                }
            }
        }
        return foundTreeNode;
    }

    public TreeNode getNode(TreeNode rootNode, File file) {
        TreeNode foundTreeNode = null;
        File rootFile = (File) rootNode.getData();
        if (file.equals(rootFile)) {
            foundTreeNode = rootNode;
        } else {
            List<TreeNode> treeNodeList = rootNode.getChildren();
            for (TreeNode tn : treeNodeList) {
                File tnFile = (File) tn.getData();
                if (file.equals(tnFile)) {
                    foundTreeNode = tn;
                    break;
                } else {
                    List<TreeNode> treeNodeChildrenList = tn.getChildren();
                    for (TreeNode child : treeNodeChildrenList) {
                        foundTreeNode = getNode(child, file);
                        if (foundTreeNode != null) {
                            break;
                        }
                    }
                }
            }
        }
        return foundTreeNode;
    }

    /**
     * @param appProperties the appProperties to set
     */
    public void setAppProperties(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
}
