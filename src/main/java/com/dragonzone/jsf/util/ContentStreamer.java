package com.dragonzone.jsf.util;

import com.dragonzone.security.AESEncryption;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import javax.faces.bean.ApplicationScoped;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@ApplicationScoped
/**
 * Should read this blog using p:graphicImage and f:param, it's a bit tricky
 * workaround:
 * http://stackoverflow.com/questions/10073905/display-database-blob-images-in-pgraphicimage-inside-uirepeat/10161878#10161878
 */
public class ContentStreamer {
    final static Logger logger = LoggerFactory.getLogger(ContentStreamer.class);

    private static final int DEFAULT_FILE_SIZE_LIMIT = 1024 * 1024 * 50; // 50MB

    public StreamedContent getStreamedContent() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String fileId = context.getExternalContext().getRequestParameterMap().get("fileId");
            File file = new File(decrypt(fileId, "FILEID_KEY")); // fileId was encrypted to handle file with special char in filename
            MediaFileUtil mediaFileUtil = new MediaFileUtil();
            if (mediaFileUtil.isImage(file) || mediaFileUtil.isMedia(file)) {
                String mimeType = Files.probeContentType(file.toPath());
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                return new DefaultStreamedContent(bis, mimeType);
            } else {
                return getDefaultStreamedContent();
            }
        }
    }

    public StreamedContent getStreamedContentPreview() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Return a real StreamedContent with the image bytes.
            String fileId = context.getExternalContext().getRequestParameterMap().get("fileId");
            File file = new File(decrypt(fileId, "FILEID_KEY")); // fileId was encrypted to handle file with special char in filename
            MediaFileUtil mediaFileUtil = new MediaFileUtil();
            if (mediaFileUtil.isImage(file) || mediaFileUtil.isMedia(file)) {
                if (file.length() < DEFAULT_FILE_SIZE_LIMIT) {
                    try {
                        String mimeType = Files.probeContentType(file.toPath());
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                        return new DefaultStreamedContent(bis, mimeType);
                    } catch (Exception e) {
                        logger.error("Error trying to load file: " + file.getAbsolutePath(), e);
                        return new DefaultStreamedContent();
                    }
                } else {
                    return getDefaultStreamedContent();
                }
            } else {
                return getDefaultStreamedContent();
            }
        }
    }

    private StreamedContent getDefaultStreamedContent() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return new DefaultStreamedContent(servletContext.getResourceAsStream("/images/file.png"));
    }

    
    public String decrypt(String message, String key) {
        String decryptedMesg = null;
        try {
            decryptedMesg = AESEncryption.decrypt(message, key);
        } catch (Throwable e) {
            logger.error("Error decrypting message: " + message + " with key: " + key, e);
        }

        return decryptedMesg;
    }
    
}
