package com.dragonzone.jsf.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import javax.faces.bean.ApplicationScoped;

import javax.faces.bean.ManagedBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@ApplicationScoped
public class MediaFileUtil implements Serializable {
    final static Logger logger = LoggerFactory.getLogger(MediaFileUtil.class);

    private static final long serialVersionUID = -1683775099172487865L;
    private static final String REGEX_PATTERN_MP3 = "^.*(mp3|mpeg)$";
    private static final String REGEX_PATTERN_SWF = "^.*(swf)";
    private static final String MEDIA_PLAYER_WIDOWS = "windows";
    private static final String MEDIA_PLAYER_QUICKTIME = "quicktime";
    private static final String MEDIA_PLAYER_FLASH = "flash";
    private static final String MEDIA_PLAYER_REAL = "real";
    private static final String MEDIA_PLAYER_PDF = "pdf";
    private static final String REGEX_PATTERN_IMAGE = "^.*(png|jpg|gif|bmp|jpeg|ico)$";
    // primefaces docs say quicktime support 'bmp', but putting as image instead
    private static final String REGEX_PATTERN_MEDIA = "^.*(asx|asf|avi|wma|wmv|aif|aiff|aac|au|gsm|mov|mid|midi|mpg|mpeg|mp4|m4a|psd|qt|qtif|qif|qti|snd|tif|tiff|wav|3g2|3pg|3gp|3gpp|flv|mp3|swf|ra|ram|rm|rpm|rv|smi|smil|pdf)$";
    private static final String REGEX_PATTERN_PLAYER_WINDOWS = "^.*(asx|asf|avi|wma|wmv)$";
    private static final String REGEX_PATTERN_PLAYER_QUICKTIME = "^.*(aif|aiff|aac|au|gsm|mov|mid|midi|mpg|mpeg|mp4|m4a|psd|qt|qtif|qif|qti|snd|tif|tiff|wav|3g2|3pg|3gp|3gpp)$";
    private static final String REGEX_PATTERN_PLAYER_FLASH = "^.*(flv|mp3|swf)$";
    private static final String REGEX_PATTERN_PLAYER_REAL = "^.*(ra|ram|rm|rpm|rv|smi|smil)$";
    private static final String REGEX_PATTERN_PLAYER_PDF = "^.*(pdf)$";

    public boolean isMp3(File file) {
        boolean image = false;
        if (file != null) {
            String mimeType = getMimeType(file);
            image = mimeType.matches(REGEX_PATTERN_MP3);
        }
        return image;
    }

    public boolean isSwf(File file) {
        boolean image = false;
        if (file != null) {
            String mimeType = getMimeType(file);
            image = mimeType.matches(REGEX_PATTERN_SWF);
        }
        return image;
    }

    public boolean isImagePath(String filePath) {
        File file = new File(filePath);
        return isImage(file);
    }

    public boolean isImage(File file) {
        boolean image = false;
        if (file != null) {
            String mimeType = getMimeType(file);
            image = mimeType.matches(REGEX_PATTERN_IMAGE);
        }
        return image;
    }

    public boolean isMedia(File file) {
        boolean media = false;
        if (file != null) {
            String mimeType = getMimeType(file);
            media = mimeType.matches(REGEX_PATTERN_MEDIA);
        }
        return media;
    }

    public boolean isMediaPlayerPath(String playerName, String filePath) {
        File file = new File(filePath);
        return isMediaPlayer(playerName, file);
    }

    public String getMediaPlayer(File file) {
        String mediaPlayer = null;
        if (file != null) {
            String mimeType = getMimeType(file);

            if (mimeType.matches(REGEX_PATTERN_PLAYER_WINDOWS)) {
                mediaPlayer = MEDIA_PLAYER_WIDOWS;
            } else if (mimeType.matches(REGEX_PATTERN_PLAYER_QUICKTIME)) {
                mediaPlayer = MEDIA_PLAYER_QUICKTIME;
            } else if (mimeType.matches(REGEX_PATTERN_PLAYER_FLASH)) {
                mediaPlayer = MEDIA_PLAYER_FLASH;
            } else if (mimeType.matches(REGEX_PATTERN_PLAYER_REAL)) {
                mediaPlayer = MEDIA_PLAYER_REAL;
            } else if (mimeType.matches(REGEX_PATTERN_PLAYER_PDF)) {
                mediaPlayer = MEDIA_PLAYER_PDF;
            }
        }
        return mediaPlayer;
    }

    public boolean isMediaPlayer(String playerName, File file) {
        boolean media = false;
        if (file != null) {
            String mimeType = getMimeType(file);

            switch (playerName) {
                case MEDIA_PLAYER_WIDOWS:
                    media = mimeType.matches(REGEX_PATTERN_PLAYER_WINDOWS);
                    break;
                case MEDIA_PLAYER_QUICKTIME:
                    media = mimeType.matches(REGEX_PATTERN_PLAYER_QUICKTIME);
                    break;
                case MEDIA_PLAYER_FLASH:
                    media = mimeType.matches(REGEX_PATTERN_PLAYER_FLASH);
                    break;
                case MEDIA_PLAYER_REAL:
                    media = mimeType.matches(REGEX_PATTERN_PLAYER_REAL);
                    break;
                case MEDIA_PLAYER_PDF:
                    media = mimeType.matches(REGEX_PATTERN_PLAYER_PDF);
                    break;
                default:
                    break;
            }
        }
        return media;
    }

    private String getMimeType(File file) {
        String mimeType = "";
        if (file != null) {
            try {
                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    String fileExt = file.getName().contains(".")
                            ? file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase()
                            : "";
                    if (fileExt.matches(REGEX_PATTERN_MEDIA)) {
                        mimeType = fileExt;
                    }
                } else {
                    mimeType = contentType.toLowerCase();
                }
            } catch (IOException e) {
                logger.error("Error trying to load file: " + file.getAbsolutePath(), e);
            }
        }
        return mimeType;
    }
}
