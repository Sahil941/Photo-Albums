package controller;

import model.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;


/**
 * This class represents all the functionality an album can have.
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class AlbumController extends ControllerBase implements ControllerSwitcher, EventHandler<MouseEvent>, ChangeListener<Tab> {
   
    String style_white = "-fx-border-color: white;\n" + "-fx-border-style: solid;\n" + "-fx-border-width: 3;\n";
    
    String style_black = "-fx-border-color: black;\n" + "-fx-border-style: solid;\n" + "-fx-border-width: 3;\n";

    /**
     * Tab for album and photo
     */
    @FXML
    TabPane tab;

    /**
     * Tile for photo thumbnail
     */
    @FXML
    TilePane tile;

    /**
     * ListView of tags
     */
    @FXML ListView<Tag> listTag;

    /**
     * Pagination to navigate through photos
     */
    @FXML Pagination pagination;

    /**
     * Textfield to add tag name
     */
	@FXML TextField tagName;

    /**
     * Textfield to add tag value
     */
    @FXML TextField tagValue;


    /**
     * List of nodes for photo thumbnails
     */
    ObservableList<Node> list;

    /**
     * ContextMenu for photo: add copy move delete
     */
    final ContextMenu cm = new ContextMenu();

    /**
     * Menu to help copy photo to other albums
     */
    Menu cmCopy = new Menu("Copy to");

    /**
     * Menu to help move photo to other albums
     */
    Menu cmMove = new Menu("Move to");
    
    
    /**
     * ContextMenu for tile: add only
     */
	final ContextMenu cm_tile = new ContextMenu();


    /**
     * Initial with album to be shown
     */
    @Override
	public void initBeforeShow() {
    	tab.getSelectionModel().select(0);
    	
    	User user = getModel().getCurrUser();
    	Album album = user.getCurrAlbum();
    	//ObservableList<Album> albums =user.getListAlbums();
    	
    	list.clear();
    	
    	List<Photo> photoList = album.getPhotoList();
        for (Photo onePhoto : photoList) {
            
            BorderPane viewWrapper = onePhoto.getThumbnailView(this, style_white);
            
            list.add(viewWrapper);
        }
    	
    	int index = album.getCurrPhotoIndex();
        setupCurrentPhoto(index, true);
	}


    /**
     * constructor of controller
     */
    public AlbumController() {
    	AlbumController controller = this;
    	
    	{	MenuItem cmItemAdd = new MenuItem("Add");
	    	cmItemAdd.setOnAction(e -> {
                User user = getModel().getCurrUser();
                Album album = user.getCurrAlbum();
                
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Open Photo File");
                File file = chooser.showOpenDialog(new Stage());
                
                if (file!=null) {
                    Photo photo = Photo.createPhoto(file.getAbsolutePath(), file);
                    
                    int index = album.addToAlbum(null, photo);
                    
                    BorderPane viewWrapper = null;
                    if (photo != null) {
                        viewWrapper = photo.getThumbnailView(controller, style_white);
                    }
                    
                    list.add(index, viewWrapper);
                    
                    setupCurrentPhoto(index, true);
                }
            });
	    	cm_tile.getItems().add(cmItemAdd);
    	}
    	
    	MenuItem cmItemAdd = new MenuItem("Add");
    	cmItemAdd.setOnAction(e -> {
            Photo onePhoto = (Photo)cm.getUserData();
            
            User user = getModel().getCurrUser();
            Album album = user.getCurrAlbum();
            
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open File");
            File file = chooser.showOpenDialog(new Stage());
            
            if (file!=null) {
                Photo photo = Photo.createPhoto(file.getAbsolutePath(), file);
                
                int oldcurrIndex = album.getCurrPhotoIndex();
                
                int index = album.addToAlbum(onePhoto, photo);
                
				BorderPane viewWrapper = null;
				if (photo != null) {
					viewWrapper = photo.getThumbnailView(controller, style_white);
				}
				
                list.add(index, viewWrapper);
                
                if (index<=oldcurrIndex) {
                	BorderPane vw = (BorderPane)list.get(oldcurrIndex+1);
                    vw.setStyle(style_white);
                }
                
                setupCurrentPhoto(index, true);
            }
        });
    	cm.getItems().add(cmItemAdd);
    	
    	cm.getItems().add(cmCopy);
    	
    	cm.getItems().add(cmMove);
    	
    	MenuItem cmItemDelete = new MenuItem("Delete");
    	cmItemDelete.setOnAction(e -> {
            Photo onePhoto = (Photo)cm.getUserData();
            
            User user = getModel().getCurrUser();
            Album album = user.getCurrAlbum();
            
            int index = album.deleteFromAlbum(onePhoto);
            list.remove(index);
            
            int indexCurr = album.getCurrPhotoIndex();
            
            setupCurrentPhoto(indexCurr, true);
        });
    	cm.getItems().add(cmItemDelete);
	}


    /**
     * Initialize album controller
     */
    @FXML
    public void initialize() {
    	list = tile.getChildren();
    	
    	tile.setOnMouseClicked(this);
    	
        tab.getSelectionModel().selectedItemProperty().addListener(this);
        tab.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        
        pagination.setPageFactory(pageIndex -> {
            User user = getModel().getCurrUser();
            Album album = user.getCurrAlbum();
            
            if (album.getSize() > 0) {
                AlbumController.this.setupCurrentPhoto(pageIndex, false);
                
                Photo currPhoto = album.getCurrPhoto();
                
                return currPhoto.getImageNode(arg0 -> tab.getSelectionModel().select(0));
            }
            else {
                return null;
            }
        });
    }

    
    /**
     * @param photo Input photo
     */
    public void setupCurrentPhoto(Photo photo) {
    	User user = getModel().getCurrUser();
    	Album album = user.getCurrAlbum();
    	ObservableList<Album> albums =user.getAlbumList();
    	
    	int index = album.getPhotoIndex(photo);
    	
    	setupCurrentPhoto(index, true);
    }

    /**
     * @param newIndex Index for current photo in the album
     * @param isFromAlbum if true initialize pagination too.
     */
    public void setupCurrentPhoto(int newIndex, boolean isFromAlbum) {
    	User user = getModel().getCurrUser();
    	Album album = user.getCurrAlbum();
    	ObservableList<Album> albums =user.getAlbumList();
    	
		int oldIndex = album.getCurrPhotoIndex();
    	
		newIndex = album.setCurrPhotoIndex(newIndex);
		Photo newPhoto = album.getCurrPhoto();
		
    	if (newIndex > -1) {
	    	if (list.size()>0) {
	    		if (oldIndex==-1) {
			        BorderPane currViewWrapper = (BorderPane)list.get(newIndex);
			        
			        currViewWrapper.setStyle(style_black);
	    		}
	    		else if (oldIndex!=newIndex) {
			        BorderPane oldViewWrapper = (BorderPane)list.get(oldIndex);
			        BorderPane currViewWrapper = (BorderPane)list.get(newIndex);
			      
			        oldViewWrapper.setStyle(style_white);
			        currViewWrapper.setStyle(style_black);
		    	}
		    	else {
			        BorderPane currViewWrapper = (BorderPane)list.get(newIndex);
			        
			        currViewWrapper.setStyle(style_black);
		    	}
	    	}
	    	
	        listTag.setItems(newPhoto.getTags());
	    	
	        if (newPhoto.getTags().size() > 0) {
	        	listTag.getSelectionModel().select(0);
	        }
	        
	        if (isFromAlbum) {
	        	pagination.setPageCount(album.getSize());
	        	pagination.setCurrentPageIndex(newIndex);
	        }
    	}
    }


    /**
     * @param event Event for mouse clicks
     */
    @Override
	public void handle(MouseEvent event) {
		Object obj = event.getSource();
		if (obj instanceof TilePane) {
	        if (event.getButton() == MouseButton.SECONDARY) {
	        	cm_tile.show(tile, event.getScreenX(), event.getScreenY());
	        }
	        
	        event.consume();
		}
		else if (obj instanceof ImageView) {
			ImageView view = (ImageView)obj;
			
	        if (event.getButton().equals(MouseButton.PRIMARY)) {
	            if (event.getClickCount() == 1) {
	            	Object obj1 = view.getUserData();
	            	Photo onePhoto = (Photo)obj1;
	            	
	            	setupCurrentPhoto(onePhoto);
	            }
	            else /*if ( event.getClickCount() == 2)*/ {
	            	Object obj1 = view.getUserData();
	            	Photo onePhoto = (Photo)obj1;
	            	
	            	setupCurrentPhoto(onePhoto);
	            	
	            	tab.getSelectionModel().select(1);
	            }
	        }
	        else if (event.getButton() == MouseButton.SECONDARY) {
            	Object obj1 = view.getUserData();
            	Photo onePhoto = (Photo)obj1;
            	
            	PhotoModel model = getModel();
            	User user = model.getCurrUser();
            	ObservableList<Album> albums =user.getAlbumList();
            	Album album = user.getCurrAlbum();
            	
                List<Album> albumList = Helper.filter(albums, album, (t,u)->t!=u&&(!t.getAlbumName().equalsIgnoreCase(Album.ALBUM_NAME_SEARCH_BY_DATE) && !t.getAlbumName().equalsIgnoreCase(Album.ALBUM_NAME_SEARCH_BY_TAG)));
                
            	cmCopy.getItems().clear();
            	for (Album a : albumList) {
                	MenuItem cmItemAlbum = new MenuItem(a.getAlbumName());
                	cmItemAlbum.setOnAction(e -> {
                        MenuItem mi =  (MenuItem)e.getSource();
                        
                        User user1 = getModel().getCurrUser();
                        
                        Photo onePhoto1 = (Photo)cm.getUserData();
                        String albumTarget = mi.getText();
                        
                        user1.copyPhoto(onePhoto1, albumTarget);
                    });
                	cmCopy.getItems().add(cmItemAlbum);
            	}
            	
            	cmMove.getItems().clear();
            	for (Album a : albumList) {
                	MenuItem cmItemAlbum = new MenuItem(a.getAlbumName());
                	cmItemAlbum.setOnAction(e -> {
                        MenuItem mi =  (MenuItem)e.getSource();
                        
                        Photo onePhoto12 = (Photo)cm.getUserData();
                        String albumTarget = mi.getText();
                        
                        User user12 = getModel().getCurrUser();
                        Album album1 = user12.getCurrAlbum();
                        
                        int index = album1.getPhotoIndex(onePhoto12);
                        
                        list.remove(index);
                        
                        user12.movePhoto(onePhoto12, albumTarget);
                        
                        int iCurr = album1.getCurrPhotoIndex();
                        setupCurrentPhoto(iCurr, true);
                    });
                	cmMove.getItems().add(cmItemAlbum);
            	}
            	
	        	cm.setUserData(onePhoto);
	        	cm.show(view, event.getScreenX(), event.getScreenY());
	        }
	        
	        event.consume();
		}
	}

    /**
     * Logs off the album scene go to login
     */
    public void doLogoff() {
	    goToLoginFromAlbum();
	}

    /**
     * Exits album
     */
    public void doExit() {
		Platform.exit();
	}


    /**
     * Event handle of tab switching
     */
	@Override
	public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
		if (arg2.getText().equals("Photo")) {
	    	User user = getModel().getCurrUser();
	    	Album album = user.getCurrAlbum();
        	Photo currPhoto = album.getCurrPhoto();
			
        	if (currPhoto!=null) {
        		pagination.setVisible(true);
        		
	        	int index = album.getPhotoIndex(currPhoto);
	        	
				pagination.setPageCount(album.getSize());
				pagination.setCurrentPageIndex(index);
				
        	}
        	else {
        		pagination.setVisible(false);
        	}
		}
		else {

		}
	}


    /**
     * Deletes tag
     */
    public void doDeleteTag() {
    	User user = getModel().getCurrUser();
    	Album album = user.getCurrAlbum();
    	
    	Photo currPhoto = album.getCurrPhoto();
    	
    	int index = listTag.getSelectionModel().getSelectedIndex();
    	
    	currPhoto.deleteTag(index);
    	
    	listTag.refresh();
	}

    /**
     * Adds tag
     */
    public void doAddTag() {
    	String name = tagName.getText().trim();
    	String value = tagValue.getText().trim();
    	
    	if (name.length()>0 && value.length()>0) {
	    	User user = getModel().getCurrUser();
	    	Album album = user.getCurrAlbum();
	    	
	    	Photo currPhoto = album.getCurrPhoto();
	    	boolean ret = currPhoto.addTag(name, value);
	    	
			if (ret) {
                listTag.refresh();
                
                tagName.setText("");
                tagValue.setText("");
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Not complete");
                alert.setContentText("Duplicate Name and Value Pair. Try again.");
                alert.showAndWait();
            }
    	}
    	else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Not complete");
            alert.setContentText("Name and Value Fields are empty. Enter again");
            alert.showAndWait();
    	}
    	
    }


    /**
     * Go to list of albums
     */
    public void gotoAlbumList() {
    	goToUserFromAlbum();
    }


    /**
     * Shows Photos help
     */
    public void doHelp() {
		Help(PHOTO_HELP_FXML);
	}
}