/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.dragonzone.util.FileUtil;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Andy
 */
public class Mp3InfoTest {

    public Mp3InfoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSearchFiles() throws Exception {
        List<File> resultFileList = new ArrayList<>();
        FileUtil.searchFiles(new File("C:\\Users\\Andy\\Desktop\\temp"), "arab", resultFileList, true);
        
        for (File file : resultFileList) {
            System.out.println("Found: " + file.getAbsolutePath());
        }
    }

//    @Test
    public void testMp3Files() throws Exception {
        List<File> listFiles = FileUtil.getFiles(new File("C:\\Users\\Andy\\Desktop\\temp\\MP3s"));
        for (File file : listFiles) {
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType != null && 
                    (mimeType.endsWith("mpeg") || mimeType.endsWith("mp3"))) {
                displayMp3FileInfo(file.getAbsolutePath());
            }
        }
    }

    public void displayMp3FileInfo(String filePath) throws Exception {
        Mp3File mp3file = new Mp3File(filePath);
        System.out.println("File: " + filePath);
        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            System.out.println("ID3v1");
            System.out.println("Title: " + id3v1Tag.getTitle());
//            System.out.println("Track: " + id3v1Tag.getTrack());
//            System.out.println("Artist: " + id3v1Tag.getArtist());
//            System.out.println("Album: " + id3v1Tag.getAlbum());
//            System.out.println("Year: " + id3v1Tag.getYear());
//            System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
//            System.out.println("Comment: " + id3v1Tag.getComment());
        } else if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            System.out.println("ID3v2");
            System.out.println("Title: " + id3v2Tag.getTitle());
//            System.out.println("Track: " + id3v2Tag.getTrack());
//            System.out.println("Artist: " + id3v2Tag.getArtist());
//            System.out.println("Album: " + id3v2Tag.getAlbum());
//            System.out.println("Year: " + id3v2Tag.getYear());
//            System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
//            System.out.println("Comment: " + id3v2Tag.getComment());
//            System.out.println("Composer: " + id3v2Tag.getComposer());
//            System.out.println("Publisher: " + id3v2Tag.getPublisher());
//            System.out.println("Original artist: " + id3v2Tag.getOriginalArtist());
//            System.out.println("Album artist: " + id3v2Tag.getAlbumArtist());
//            System.out.println("Copyright: " + id3v2Tag.getCopyright());
//            System.out.println("URL: " + id3v2Tag.getUrl());
//            System.out.println("Encoder: " + id3v2Tag.getEncoder());
            byte[] albumImageData = id3v2Tag.getAlbumImage();
            if (albumImageData != null) {
                System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
                System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
            }
        }
    }
}
