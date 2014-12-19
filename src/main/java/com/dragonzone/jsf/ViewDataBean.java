package com.dragonzone.jsf;

import com.dragonzone.mp3.Mp3Meta;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ViewDataBean implements Serializable {
    private static final long serialVersionUID = 914970822058171434L;
    private List<File> fileList;
    private File selectedFile;
    private Mp3Meta selectedMp3Meta;
    private int fileWidth;
    private int fileHeight;
    private int fileActualWidth;
    private int fileActualHeight;
    private boolean shuffle;
    private boolean repeat;
    private boolean polling;
    private Date lastLoaded;
    private int relativeToActualSize;

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
     * @return the selectedMp3Meta
     */
    public Mp3Meta getSelectedMp3Meta() {
        return selectedMp3Meta;
    }

    /**
     * @param selectedMp3Meta the selectedMp3Meta to set
     */
    public void setSelectedMp3Meta(Mp3Meta selectedMp3Meta) {
        this.selectedMp3Meta = selectedMp3Meta;
    }
    
    /**
     * @return the fileWidth
     */
    public int getFileWidth() {
        return fileWidth;
    }

    /**
     * @param fileWidth the fileWidth to set
     */
    public void setFileWidth(int fileWidth) {
        this.fileWidth = fileWidth;
    }

    /**
     * @return the fileHeight
     */
    public int getFileHeight() {
        return fileHeight;
    }

    /**
     * @param fileHeight the fileHeight to set
     */
    public void setFileHeight(int fileHeight) {
        this.fileHeight = fileHeight;
    }

    /**
     * @return the fileActualWidth
     */
    public int getFileActualWidth() {
        return fileActualWidth;
    }

    /**
     * @param fileActualWidth the fileActualWidth to set
     */
    public void setFileActualWidth(int fileActualWidth) {
        this.fileActualWidth = fileActualWidth;
    }

    /**
     * @return the fileActualHeight
     */
    public int getFileActualHeight() {
        return fileActualHeight;
    }

    /**
     * @param fileActualHeight the fileActualHeight to set
     */
    public void setFileActualHeight(int fileActualHeight) {
        this.fileActualHeight = fileActualHeight;
    }

    /**
     * @return the shuffle
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * @param shuffle the shuffle to set
     */
    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
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
     * @return the polling
     */
    public boolean isPolling() {
        return polling;
    }

    /**
     * @param polling the polling to set
     */
    public void setPolling(boolean polling) {
        this.polling = polling;
    }

    /**
     * @return the lastLoaded
     */
    public Date getLastLoaded() {
        return lastLoaded;
    }

    /**
     * @param lastLoaded the lastLoaded to set
     */
    public void setLastLoaded(Date lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    /**
     * @return the repeat
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    /**
     * @return the relativeToActualSize
     */
    public int getRelativeToActualSize() {
        return relativeToActualSize;
    }

    /**
     * @param relativeToActualSize the relativeToActualSize to set
     */
    public void setRelativeToActualSize(int relativeToActualSize) {
        this.relativeToActualSize = relativeToActualSize;
    }
}
