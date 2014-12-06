package com.dragonzone.util;

import com.dragonzone.jsf.util.MediaFileUtil;
import com.dragonzone.mp3.Mp3Meta;
import com.dragonzone.mp3.Mp3Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.StringUtils;

public class FileUtil {

    public static final long ZIP_FILE_SIZE_LIMIT = 1048576000; // 1GB

    private FileUtil() {
    }

    public static String humanReadableByteCount(long bytes, boolean size) {
        int unit = size ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (size ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (size ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean validZipSize(List<File> fileList) {
        long totalSize = 0;
        for (File file : fileList) {
            totalSize += file.length();
        }
        return totalSize < ZIP_FILE_SIZE_LIMIT;
    }

    public static File zipFiles(List<File> fileList, String omitBasePath) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if (!tmpDir.exists()) { // make sure tmpDir exists
            tmpDir.mkdir();
        }

        File zipTempFile = null;
        try {
            zipTempFile = File.createTempFile("webEx", ".zip");
            byte[] buffer = new byte[1024];

            FileOutputStream fout = new FileOutputStream(zipTempFile);

            try (ZipOutputStream zout = new ZipOutputStream(fout)) {
                for (File file : fileList) {
                    //create object of FileInputStream for source file
                    try (final FileInputStream fin = new FileInputStream(file)) {
                        String zipFilePath = omitBasePath == null
                                ? file.getName()
                                : file.getAbsolutePath().substring(omitBasePath.length());
                        zout.putNextEntry(new ZipEntry(zipFilePath));
                        int length;
                        while ((length = fin.read(buffer)) > 0) {
                            zout.write(buffer, 0, length);
                        }
                        zout.closeEntry();
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, "Error trying to zip file", ex);
        }

        return zipTempFile;
    }

    public static File getDrive(File file) {
        File drive = null;
        for (File dr : File.listRoots()) {
            if (file.getAbsolutePath().toLowerCase().startsWith(dr.getAbsolutePath().toLowerCase())) {
                drive = dr;
                break;
            }
        }
        return drive;
    }

    public static List<File> getFiles(File parentDir) {
        List<File> fileList = new ArrayList<>();
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    public static void getFilesAndSubFolderFiles(List<File> fileList, File rootDir) {
        if (fileList != null) {
            File[] files = rootDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file);
                    } else if (file.isDirectory()) {
                        getFilesAndSubFolderFiles(fileList, file);
                    }
                }
            }
        }
    }

    public static void searchFiles(File rootDir, String searchStr, List<File> resultFileList, boolean searchMp3Meta) {
        MediaFileUtil mediaFileUtil = new MediaFileUtil();
        searchStr = searchStr.toLowerCase();
        File[] files = rootDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if (matchTerms(file.getName(), searchStr)) {
                    resultFileList.add(file);
                }
                searchFiles(file, searchStr, resultFileList, searchMp3Meta);
            } else {
                // sometimes the term is in the dir not file name, so concat them
                if (matchTerms(file.getParentFile().getName() + "/" + file.getName(), searchStr)) { 
                    resultFileList.add(file);
                } else {
                    if (searchMp3Meta && mediaFileUtil.isMp3(file)) {
                        Mp3Meta mp3Meta = Mp3Util.getMp3Meta(file.getAbsolutePath());
                        if ((mp3Meta.getTitle() != null && matchTerms(mp3Meta.getTitle(), searchStr))
                                || (mp3Meta.getArtist() != null && matchTerms(mp3Meta.getArtist(), searchStr))
//                                || (mp3Meta.getAlbum() != null && matchTerms(mp3Meta.getAlbum(), searchStr))
//                                || (mp3Meta.getAlbumArtist() != null && matchTerms(mp3Meta.getAlbumArtist(), searchStr))
//                                || (mp3Meta.getComment() != null && matchTerms(mp3Meta.getComment(), searchStr))
                                ) {
                            resultFileList.add(file);
                        }
                    }
                }
            }
        }
    }

    private static boolean matchTerms(String input, String searchStr) {
        String[] searchVals = StringUtils.split(searchStr.toLowerCase());
        StringBuilder sbPattern = new StringBuilder("^");
        for (String str : searchVals) {
            sbPattern.append("(.*");
            sbPattern.append(str);
            sbPattern.append(")");
        }
        sbPattern.append(".+$");
        
        return Pattern.matches(sbPattern.toString(), input.toLowerCase());
    }
}
