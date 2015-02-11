package com.dragonzone.jsf;

import com.dragonzone.jsf.util.MediaFileUtil;
import com.dragonzone.jsf.util.MessageUtil;
import com.dragonzone.service.FileDirectoryService;
import com.dragonzone.util.FileUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resnbl.android.swfview.SWFInfo;

@ManagedBean
@RequestScoped
public class ViewDataControlBean extends ExplorerControlBean {

    final static Logger logger = LoggerFactory.getLogger(ViewDataControlBean.class);

    private static final int SLIDE_SHOW_INTERVAL_IN_SEC = 5;
    @ManagedProperty("#{fileDirectoryService}")
    private FileDirectoryService fileDirectoryService;
    @ManagedProperty("#{mediaFileUtil}")
    private MediaFileUtil mediaFileUtil;
    @ManagedProperty("#{viewDataBean}")
    private ViewDataBean viewDataBean;
    @ManagedProperty(value = "#{param.fileId}")
    private String fileId;

    @Override
    public void preLoadPage() {
        viewDataBean.setLastLoaded(new Date());
        if (fileId != null) {
            fileId = decrypt(fileId);
            viewDataBean.setSelectedFile(new File(fileId));
            viewDataBean.setFileList(getPlayableFileList());

            if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
                setAutoImageDimensions(viewDataBean.getSelectedFile());
            } else if (mediaFileUtil.isMedia(viewDataBean.getSelectedFile())) {
                if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                    viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
                }
            }
        }
    }

    public SWFInfo getSwfInfo(File file) {
        return SWFInfo.getInfo(file);
    }

    private List<File> getPlayableFileList() {
        List<File> playableFileList = new ArrayList<>();
        List<File> fileList = FileUtil.getFiles(viewDataBean.getSelectedFile().getParentFile());
        for (File file : fileList) {
            if (mediaFileUtil.isImage(file)
                    || mediaFileUtil.isMedia(file)) {
                playableFileList.add(file);
            }
        }
        return playableFileList;
    }

    private void setAutoImageDimensions(File file) {
        setImageDimensions(file, viewDataBean.getRelativeToActualSize());
    }

    public void zoomImage() {
        fileId = viewDataBean.getSelectedFile().getAbsolutePath();
        setImageDimensions(viewDataBean.getSelectedFile(), viewDataBean.getRelativeToActualSize());
    }

    private void setImageDimensions(File file, int relativeToActualSize) {
        try {
            BufferedImage image = ImageIO.read(file);
            int fileWidth;
            int fileHeight;
            if (relativeToActualSize == 0) {
                fileWidth = image.getWidth();
                fileHeight = image.getHeight();
            } else {
                fileWidth = relativeToActualSize > 0
                        ? (int) (image.getWidth() * (Math.abs((relativeToActualSize + 100) / 100f)))
                        : getRelativeSize(image.getWidth(), relativeToActualSize);
                fileHeight = relativeToActualSize > 0
                        ? (int) (image.getHeight() * (Math.abs((relativeToActualSize + 100) / 100f)))
                        : getRelativeSize(image.getHeight(), relativeToActualSize);
            }
            viewDataBean.setFileActualWidth(image.getWidth());
            viewDataBean.setFileActualHeight(image.getHeight());
            viewDataBean.setFileWidth(fileWidth);
            viewDataBean.setFileHeight(fileHeight);
        } catch (IOException ex) {
            logger.error("Error trying to read image file: " + viewDataBean.getSelectedFile().getAbsolutePath(), ex);
        }
    }

    private int getRelativeSize(int actualSize, int inputSize) {
        int step = Integer.parseInt(MessageUtil.getResourceBundleValue("image.zoom.step", null));
        int stepTotal = Integer.parseInt(MessageUtil.getResourceBundleValue("image.zoom.step.total", null));
        int zoomMin = Integer.parseInt(MessageUtil.getResourceBundleValue("image.zoom.min", null));
        int perZoom = actualSize / stepTotal;
        int relSize = (stepTotal - (Math.abs(inputSize == zoomMin ? (zoomMin + 3) : inputSize) / step)) * perZoom;
        return relSize;
    }

    public void onRowSelect(SelectEvent event) {
        playFile(viewDataBean.getSelectedFile());
    }

    public void removeFile(File file) {
        viewDataBean.getFileList().remove(file);
    }

    public long getRemainingPollInterval() {
        if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
            Date now = new Date();
            long passInSec = (now.getTime() - viewDataBean.getLastLoaded().getTime()) / 1000;
            long fileInSec = viewDataBean.getSelectedMp3Meta().getLengthInMilliseconds() / 1000;
            long remaining = fileInSec - passInSec;
            return remaining > 5 ? remaining : 5; // don't make below 5, otherwise, page keeps on polling/refreshing
        } else if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
            return SLIDE_SHOW_INTERVAL_IN_SEC;
        } else {
            return 60 * 60 * 4; // poll 4 hour later
        }
    }

    public void repeat() {
        if (viewDataBean.isRepeat()) {
            viewDataBean.setPolling(true);
            viewDataBean.setShuffle(false);
        }
    }

    public void shuffle() {
        if (viewDataBean.isShuffle()) {
            viewDataBean.setPolling(true);
            viewDataBean.setRepeat(false);
        }
    }

    public void polling() {
        if (!viewDataBean.isPolling()) {
            viewDataBean.setRepeat(false);
            viewDataBean.setShuffle(false);
        }
    }

    public void pickNext() {
        playNextFile();
    }

    public void playFile(File file) {
        fileId = file.getAbsolutePath();
        if (mediaFileUtil.isImage(file)) {
            setAutoImageDimensions(file);
        } else if (mediaFileUtil.isMedia(file)) {
            if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
            }
        }
        viewDataBean.setLastLoaded(new Date());
    }

    public void playFirstFile() {
        List<File> fileList = viewDataBean.getFileList();
        fileId = fileList.get(0).getAbsolutePath();
        viewDataBean.setSelectedFile(new File(fileId));
        if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
            setAutoImageDimensions(viewDataBean.getSelectedFile());
        } else if (mediaFileUtil.isMedia(viewDataBean.getSelectedFile())) {
            if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
            }
        } else {
            playNextFile(); // first file might not be readable, so call playNextFile()
        }
        viewDataBean.setLastLoaded(new Date());
    }

    public void playLastFile() {
        List<File> fileList = viewDataBean.getFileList();
        fileId = fileList.get(fileList.size() - 1).getAbsolutePath();
        viewDataBean.setSelectedFile(new File(fileId));
        if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
            setAutoImageDimensions(viewDataBean.getSelectedFile());
        } else if (mediaFileUtil.isMedia(viewDataBean.getSelectedFile())) {
            if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
            }
        } else {
            playPreviousFile(); // last file might not be readable, so call playPreviousFile()
        }
        viewDataBean.setLastLoaded(new Date());
    }

    public void playNextFile() {
        if (viewDataBean.isRepeat()) {
            playFile(viewDataBean.getSelectedFile());
        } else if (viewDataBean.isShuffle()) {
            playRandomFile();
        } else {
            List<File> fileList = viewDataBean.getFileList();
            int curIndex = fileList.indexOf(viewDataBean.getSelectedFile());
            fileId = fileList.get((curIndex == fileList.size() - 1)
                    ? 0
                    : (curIndex + 1)).getAbsolutePath();
            viewDataBean.setSelectedFile(new File(fileId));
            if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
                setAutoImageDimensions(viewDataBean.getSelectedFile());
            } else if (mediaFileUtil.isMedia(viewDataBean.getSelectedFile())) {
                if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                    viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
                }
            } else {
                playNextFile();
            }
        }
        viewDataBean.setLastLoaded(new Date());
    }

    public void playPreviousFile() {
        if (viewDataBean.isShuffle()) {
            playRandomFile();
        } else {
            List<File> fileList = viewDataBean.getFileList();
            int curIndex = fileList.indexOf(viewDataBean.getSelectedFile());
            fileId = fileList.get(curIndex == 0
                    ? (fileList.size() - 1)
                    : (curIndex - 1)).getAbsolutePath();
            viewDataBean.setSelectedFile(new File(fileId));
            if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
                setAutoImageDimensions(viewDataBean.getSelectedFile());
            } else if (mediaFileUtil.isMedia(viewDataBean.getSelectedFile())) {
                if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                    viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
                }
            } else {
                playPreviousFile();
            }
        }
        viewDataBean.setLastLoaded(new Date());
    }

    public void playRandomFile() {
        File nextFile = nextRandomFile();

        // get next file that is not same as current one
        while ((nextFile).equals(viewDataBean.getSelectedFile())) {
            nextFile = nextRandomFile();
        }
        fileId = nextFile.getAbsolutePath();
        viewDataBean.setSelectedFile(nextFile);

        if (mediaFileUtil.isImage(viewDataBean.getSelectedFile())) {
            setAutoImageDimensions(viewDataBean.getSelectedFile());
        } else if (mediaFileUtil.isMedia(viewDataBean.getSelectedFile())) {
            if (mediaFileUtil.isMp3(viewDataBean.getSelectedFile())) {
                viewDataBean.setSelectedMp3Meta(getMp3Meta(viewDataBean.getSelectedFile()));
            }
        } else {
            playRandomFile();
        }
        viewDataBean.setLastLoaded(new Date());
    }

    private File nextRandomFile() {
        List<File> fileList = viewDataBean.getFileList();
        int index = randomInt(0, fileList.size() - 1);
        return fileList.get(index);
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    public void downloadFile() throws IOException {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        String contentType = Files.probeContentType(viewDataBean.getSelectedFile().toPath());
        ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the buffer beforehand. We want to get rid of them, else it may collide.
        ec.setResponseContentType(contentType); // Check http://www.iana.org/assignments/media-types for all types. Use if necessary ExternalContext#getMimeType() for auto-detection based on filename.
        ec.setResponseContentLength(Long.valueOf(viewDataBean.getSelectedFile().length()).intValue()); // Set it with the file size. This header is optional. It will work if it's omitted, but the download progress will be unknown.
        ec.setResponseHeader("Content-Disposition",
                "attachment; filename=\"" + viewDataBean.getSelectedFile().getName() + "\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.

        OutputStream output = ec.getResponseOutputStream();
        try (FileInputStream fis = new FileInputStream(viewDataBean.getSelectedFile())) {
            IOUtils.copy(fis, output);
        }

        fc.responseComplete(); // Important! Otherwise JSF will attempt to render the response which obviously will fail since it's already written with a file and closed.
    }

    /**
     * @return the fileId
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * @param viewDataBean the viewDataBean to set
     */
    public void setViewDataBean(ViewDataBean viewDataBean) {
        this.viewDataBean = viewDataBean;
    }

    /**
     * @param fileDirectoryService the fileDirectoryService to set
     */
    public void setFileDirectoryService(FileDirectoryService fileDirectoryService) {
        this.fileDirectoryService = fileDirectoryService;
    }

    /**
     * @param mediaFileUtil the mediaFileUtil to set
     */
    public void setMediaFileUtil(MediaFileUtil mediaFileUtil) {
        this.mediaFileUtil = mediaFileUtil;
    }
}
