package com.dragonzone.mp3;

import java.io.Serializable;

public class Mp3Meta implements Serializable {

    private static final long serialVersionUID = -2104467868842632839L;
    private String fileAbsolutePath;
    private boolean id3v1Tag;
    private boolean id3v2Tag;
    private long lengthInMilliseconds;
    private String title;
    private String track;
    private String artist;
    private String album;
    private String year;
    private String genre;
    private String comment;
    private String composer;
    private String publisher;
    private String originalArist;
    private String albumArtist;
    private String copyright;
    private String url;
    private String encoder;

    public Mp3Meta(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the track
     */
    public String getTrack() {
        return track;
    }

    /**
     * @param track the track to set
     */
    public void setTrack(String track) {
        this.track = track;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * @param album the album to set
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the composer
     */
    public String getComposer() {
        return composer;
    }

    /**
     * @param composer the composer to set
     */
    public void setComposer(String composer) {
        this.composer = composer;
    }

    /**
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * @return the originalArist
     */
    public String getOriginalArist() {
        return originalArist;
    }

    /**
     * @param originalArist the originalArist to set
     */
    public void setOriginalArist(String originalArist) {
        this.originalArist = originalArist;
    }

    /**
     * @return the albumArtist
     */
    public String getAlbumArtist() {
        return albumArtist;
    }

    /**
     * @param albumArtist the albumArtist to set
     */
    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    /**
     * @return the copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * @param copyright the copyright to set
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * @return the encoder
     */
    public String getEncoder() {
        return encoder;
    }

    /**
     * @param encoder the encoder to set
     */
    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }

    /**
     * @return the id3v1Tag
     */
    public boolean isId3v1Tag() {
        return id3v1Tag;
    }

    /**
     * @param id3v1Tag the id3v1Tag to set
     */
    public void setId3v1Tag(boolean id3v1Tag) {
        this.id3v1Tag = id3v1Tag;
    }

    /**
     * @return the id3v2Tag
     */
    public boolean isId3v2Tag() {
        return id3v2Tag;
    }

    /**
     * @param id3v2Tag the id3v2Tag to set
     */
    public void setId3v2Tag(boolean id3v2Tag) {
        this.id3v2Tag = id3v2Tag;
    }

    /**
     * @return the fileAbsolutePath
     */
    public String getFileAbsolutePath() {
        return fileAbsolutePath;
    }

    /**
     * @param fileAbsolutePath the fileAbsolutePath to set
     */
    public void setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the lengthInMilliseconds
     */
    public long getLengthInMilliseconds() {
        return lengthInMilliseconds;
    }

    /**
     * @param lengthInMilliseconds the lengthInMilliseconds to set
     */
    public void setLengthInMilliseconds(long lengthInMilliseconds) {
        this.lengthInMilliseconds = lengthInMilliseconds;
    }

}
