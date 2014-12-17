package com.dragonzone.mp3;

import com.dragonzone.jsf.util.MediaFileUtil;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import org.slf4j.LoggerFactory;

public class Mp3Util {
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(Mp3Util.class);

    private Mp3Util() {
    }

    public static Mp3Meta getMp3Meta(String filePath) {
        Mp3Meta mp3Meta = new Mp3Meta(filePath);
        MediaFileUtil mediaFileUtil = new MediaFileUtil();
        if (mediaFileUtil.isMp3(new File(filePath))) {
            Mp3File mp3File;
            try {
                mp3File = new Mp3File(filePath);
                mp3Meta.setLengthInMilliseconds(mp3File.getLengthInMilliseconds());
                if (mp3File.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                    mp3Meta.setId3v1Tag(true);
                    mp3Meta.setTitle(id3v1Tag.getTitle());
                    mp3Meta.setTrack(id3v1Tag.getTrack());
                    mp3Meta.setArtist(id3v1Tag.getArtist());
                    mp3Meta.setAlbum(id3v1Tag.getAlbum());
                    mp3Meta.setYear(id3v1Tag.getYear());
                    mp3Meta.setGenre(id3v1Tag.getGenreDescription());
                    mp3Meta.setComment(id3v1Tag.getComment());
                } else if (mp3File.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                    mp3Meta.setId3v2Tag(true);
                    mp3Meta.setTitle(id3v2Tag.getTitle());
                    mp3Meta.setTrack(id3v2Tag.getTrack());
                    mp3Meta.setArtist(id3v2Tag.getArtist());
                    mp3Meta.setAlbum(id3v2Tag.getAlbum());
                    mp3Meta.setYear(id3v2Tag.getYear());
                    mp3Meta.setGenre(id3v2Tag.getGenreDescription());
                    mp3Meta.setComment(id3v2Tag.getComment());
                    mp3Meta.setComposer(id3v2Tag.getComposer());
                    mp3Meta.setPublisher(id3v2Tag.getPublisher());
                    mp3Meta.setOriginalArist(id3v2Tag.getOriginalArtist());
                    mp3Meta.setAlbumArtist(id3v2Tag.getAlbumArtist());
                    mp3Meta.setCopyright(id3v2Tag.getCopyright());
                    mp3Meta.setUrl(id3v2Tag.getUrl());
                    mp3Meta.setEncoder(id3v2Tag.getEncoder());
                }
            } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
                logger.error("Error trying to get Mp3 meta for file: " + filePath);
            }
        }

        return mp3Meta;
    }
}
