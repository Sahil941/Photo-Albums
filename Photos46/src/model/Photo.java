package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

/**
 * Represents a photo object.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class Photo implements Serializable {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 6278229967228693482L;

	/**
     * Format of thumbnail
     */
    private static final String THUMBNAIL_IMAGE_FORMAT = "png";
    
    /**
     * Width of thumbnail
     */
    private static final int THUMBNAIL_WIDTH = 120;
    
    /**
     * Height of thumbnail
     */
    private static final int THUMBNAIL_HEIGHT = 120;
    
    /**
     * Path to store thumbnails
     */
    private static final String	THUMBNAIL_PATH = "thumbnails";
    
    /**
     * File name of the photo
     */
    private String fileName;

    /**
     * Thumbnail name
     */
    private String thumbnail;

    /**
     * Caption that a photo has
     */
    private String caption;

    /**
     * Date the photo was taken
     */
    private long photoDate;

    /**
     * List of tags the photo contains
     */
    private ObservableList<Tag> tagList;

    /**
     * List of tags the photo contains (for serialization)
     */
    private ArrayList<Tag> keepTagList;
    
    /**
     * Create thumbnail folder
     */
    static{
        new File(THUMBNAIL_PATH).mkdir();
    }

    /**
     * @param thumbInput Input thumbname
     * @return Thumbnail file name
     */
    public static String getThumbnailName(String thumbInput) {
        return THUMBNAIL_PATH + "/" + thumbInput + "." + THUMBNAIL_IMAGE_FORMAT;
    }

    /**
     * @param fileName File name
     * @param file Photo file
     * @return New photo created
     */
    public static Photo createPhoto(String fileName, File file) {
        if (file == null) {
            file = new File(fileName);
        }
        
        long lastModified = file.lastModified();
        String fn = file.getName();
        int index = fn.indexOf('.');
        
        if (index > 0) {
            fn = fn.substring(0, index);
        }
        
        String thumbnail = String.valueOf(UUID.randomUUID());
        boolean result = createThumbNail(fileName, getThumbnailName(thumbnail), THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, THUMBNAIL_IMAGE_FORMAT);
        
        if (result){
            return new Photo(fileName, thumbnail, fn, lastModified);
        }
        
        return null;
    }

    /**
     * @param photoName File name of origin photo
     * @param thumbnailName File name of thumbnail
     * @param width Width of thumbnail
     * @param height Height of thumbnail
     * @param thumbnailFormat Format of thumbnail
     * @return True if thumbnail is created
     */
    private static boolean createThumbNail(String photoName, String thumbnailName, int width, int height, String thumbnailFormat) {
        boolean result = true;
        Image img = null;
        
        try {
            img = new Image(new FileInputStream(photoName), width, height, true, true);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        if (img != null) {
            BufferedImage bfrImage = SwingFXUtils.fromFXImage(img, null);
            
            try {
                FileOutputStream outputStream = new FileOutputStream(thumbnailName);
                ImageIO.write(bfrImage, thumbnailFormat, outputStream);
                outputStream.close();
            }
            catch (IOException e) {
                result = false;
                e.printStackTrace();
            }
        }
        else {
            result = false;
        }
        
        return result;
    }

    /**
     * @param stored Clean up before store to file or after retrieved from file
     */
    public void cleanUp(boolean stored) {
        if (!stored) {
        	this.tagList = FXCollections.observableList(this.keepTagList);
            this.keepTagList = null;
        }
        else {
        	this.keepTagList = new ArrayList<>(this.tagList);
            this.tagList = null;
        }
    }

    /**
     * @param fileName File name of photo
     * @param thumbnail Thumbnail created from photo
     * @param caption Caption of photo
     * @param photoDate Date of photo
     */
    private Photo(String fileName, String thumbnail, String caption, long photoDate) {
        this.fileName = fileName;
        this.thumbnail = thumbnail;
        this.caption = caption;
        this.photoDate = photoDate;
        this.tagList = FXCollections.observableArrayList();
        this.keepTagList = null;
    }

    /**
     * @return List of tags
     */
    public ObservableList<Tag> getTags() {
        return this.tagList;
    }

    /**
     * @param p Clone a photo, including thumbnail file
     */
    public Photo(Photo p) {
        this.fileName = p.fileName;
        this.thumbnail = String.valueOf(UUID.randomUUID());
        this.caption = p.caption;
        this.photoDate = p.photoDate;
        this.tagList = FXCollections.observableArrayList();
        
        for (Tag t: p.tagList) {
            this.tagList.add(new Tag(t));
        }
        
        try {
            Files.copy(new File(p.getThumbnailName()).toPath(), new File(getThumbnailName()).toPath());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return File name of thumbnail
     */
    private String getThumbnailName() {
        return getThumbnailName(this.thumbnail);
    }

    /**
     * @return File name of photo
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * @param fileName File name of photo
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Caption of photo
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * @param caption Caption of photo
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @return Date of photo taken
     */
    public long getPhotoDate() {
        return this.photoDate;
    }

    /**
     * @return Localtime of photo taken
     */
    public String getPhotoDateString() {
        return epochToLocalTime(this.photoDate);
    }

    /**
     * @param tagName Tag name
     * @param tagVal Tag value
     * @return True if tag is added
     */
    public boolean addTag(String tagName, String tagVal) {
        Tag t = new Tag(tagName, tagVal);
        
        return Helper.addOrGetList(this.tagList, t);
    }

    public Tag deleteTag(String tagName, String tagVal) {
        Tag t = new Tag(tagName, tagVal);
        
        return Helper.delete(this.tagList, t, (tt,kk)->tt.compareTo(kk)==0);
    }

    /**
     * @param x Index of tag in list to delete
     */
    public void deleteTag(int x) {
        if (x >= 0 && x < this.tagList.size()) {
            this.tagList.remove(x);
        }
    }

    /**
     * @param mouseHandler Handler for mouse events
     * @param style Style of Image View
     * @return Thumbnail Image View
     */
    public BorderPane getThumbnailView(EventHandler<MouseEvent> mouseHandler, String style) {
        Image img = new Image("File:" + getThumbnailName(), 0, 0, false, false);
        ImageView imgView = new ImageView(img);
        
        imgView.setFitWidth(THUMBNAIL_WIDTH);
        imgView.setFitHeight(THUMBNAIL_HEIGHT);
        imgView.setOnMouseClicked(mouseHandler);
        imgView.setUserData(this);
        
        Photo p = this;
        TextField txtF = new TextField(getCaption());
        txtF.setPrefWidth(THUMBNAIL_WIDTH);
        
        txtF.setOnAction(e -> {
            TextField textField = (TextField) e.getSource();
            String tmp = textField.getText().trim();
            if (tmp.length()==0) {
                textField.setText(p.getCaption());
            }
            else {
                p.setCaption(tmp);
            }
        });
        
        VBox vBox = new VBox(4);
        vBox.getChildren().addAll(imgView, txtF, new Label(getPhotoDateString()));
        BorderPane finalView = new BorderPane(vBox);
        finalView.setStyle(style);
        
        return finalView;
    }

    /**
     * @param mouseHandler Handler for mouse events
     * @return Node for Image to render
     */
    public Node getImageNode(EventHandler<MouseEvent> mouseHandler) {
        ImageView img;
        Image imgN = null;
        
        try {
            imgN = new Image(new FileInputStream(getFileName()));
        }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        img = new ImageView();
        img.setFitWidth(650);
        img.setFitHeight(450);
        img.setPreserveRatio(true);
        img.setSmooth(true);
        img.setImage(imgN);
        img.setOnMouseClicked(mouseHandler);
        
        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(new Label("Caption: " + getCaption() + ". This photo was taken at " + getPhotoDateString()), img);
        
        return vBox;
    }

    /**
     * @param dates Range of dates
     * @return True if within range of dates
     */
    public boolean isWithinRange(TimeRestriction dates) {
        ZoneId zID = ZoneId.systemDefault();
        LocalDate startDate = dates.getStartDate();
        LocalDate endDate = dates.getEndDate().plusDays(1);
        
        long start = startDate.atStartOfDay(zID).toEpochSecond();
        long end = endDate.atStartOfDay(zID).toEpochSecond();
        
        Date photoDate = new Date(this.photoDate);
        LocalDateTime date = photoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long check = date.atZone(zID).toEpochSecond();
        
        return (check >= start && check < end);
    }

    /**
     * @param time Input time
     * @return Last modified time converted to String
     */
    public static String epochToLocalTime(long time) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        DateTimeFormatter frmtr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dateTime.format(frmtr);
    }
}