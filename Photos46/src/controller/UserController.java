package controller;

import model.*;
import java.time.LocalDate;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Helps controls all user functionality.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class UserController extends ControllerBase implements ControllerSwitcher, EventHandler<MouseEvent>, ChangeListener<Album> {
    
    @FXML TableView<Album> table;
    @FXML TableColumn colalbumName;
	@FXML TableColumn colphotoCount;
	@FXML TableColumn colstartTime;
	@FXML TableColumn colendTime;
	@FXML ListView<Tag> listTag;
    @FXML TextField tagName;
    @FXML TextField tagValue;
    @FXML TextField newAlbumName;
    @FXML DatePicker startDate;
    @FXML DatePicker endDate;

    /**
     * Initializes for the current user before shown
     */
    public void initBeforeShow() {
    	User user = getModel().getCurrUser();
    	ObservableList<Album> albumList = user.getAlbumList();

    	listTag.setItems(user.getTags().getTags());
        if (user.getTags().getTags().size() > 0) {
        	listTag.getSelectionModel().select(0);
        }

    	for (Album a : albumList) {
    		a.setCounterDateTime();
    	}

    	table.setItems(albumList);

        if (user.getAlbumList().size() > 0) {
        	table.getSelectionModel().select(0);
        }

    	table.refresh();
	}

    /**
     * Initializes controller
     */
    @FXML
    public void initialize() {
    	table.setEditable(true);
    	colalbumName.setCellFactory(TextFieldTableCell.<Album>forTableColumn());
    	colalbumName.setOnEditCommit(
            new EventHandler<CellEditEvent<Album, String>>() {
                @Override
                public void handle(CellEditEvent<Album, String> t) {
            		String newAlbumName = t.getNewValue().trim();
            		if (newAlbumName.length()>0) {
            			User user = getModel().getCurrUser();
            			int i = table.getSelectionModel().getSelectedIndex();
            			user.updateAlbumName(i, newAlbumName);
            		}
        			table.refresh();
                }
            }
        );
    	table.setRowFactory(tableView -> {
            final TableRow<Album> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem removeMenuItem = new MenuItem("Remove");
            removeMenuItem.setOnAction(event -> table.getItems().remove(row.getItem()));
            contextMenu.getItems().add(removeMenuItem);
			
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                    .then((ContextMenu)null)
                    .otherwise(contextMenu)
            );
            return row ;
        });
    	
    	endDate.setValue(LocalDate.now());
    	startDate.setValue(endDate.getValue().minusDays(30));
    	table.getSelectionModel().selectedItemProperty().addListener(this);
    	table.setOnMouseClicked(event -> {
			if (!event.getButton().equals(MouseButton.PRIMARY) || event.getClickCount() != 1) {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    goToAlbumFromUser();
                }
			}
		});
    	
    }

	@Override
	public void handle(MouseEvent arg0) {
		
	}
	
    /**
     * Logs off user
     */
    public void Logoff() {
	    goToLoginFromUser();
	}
    
    /**
     * Exits user
     */
    public void Exit() {
		Platform.exit();
	}
    
    /**
     * Adds one tag (for tags searching)
     */
    public void AddUserTag() {
		String tName = tagName.getText().trim();
		String tVal = tagValue.getText().trim();
        User user = getModel().getCurrUser();
        
		if (!(tName.length()>0 && tVal.length()>0)){
			Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Not complete");
            alert.setContentText("Name and Value Fields are empty. Enter again");
            alert.showAndWait();
		}
		else{
			boolean result = user.getTags().addTag(tName, tVal);

			if (!result){
				Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Not complete");
                alert.setContentText("Duplicate Name and Value Pair. Try again.");
                alert.showAndWait();
			}
			else{
				listTag.refresh();
                tagName.setText("");
                tagValue.setText("");
			}
		}
	}

    /**
     * Deletes one tag used for tag searching
     */
    public void DeleteUserTag() {
		User user = getModel().getCurrUser();
		int index = listTag.getSelectionModel().getSelectedIndex();
		user.deleteTagRestrictionTag(index);
		listTag.refresh();
	}

    /**
     * Searches photos given date range
     */
    public void dateSearch() {
		User user = getModel().getCurrUser();
		
		if(!(startDate.getValue() == null || endDate.getValue() == null)){
			user.getDates().setStartDate(startDate.getValue());
			user.getDates().setEndDate(endDate.getValue());
			List<Photo> list = user.dateSearch();
			String message;
			
			if(list.size() <= 0){
				message = "No photo matched";
			    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES/*, ButtonType.NO, ButtonType.CANCEL*/);
			    alert.showAndWait();
			}
			else{
				Album newOne = new Album(Album.ALBUM_NAME_SEARCH_BY_DATE, list);
				newOne.setCounterDateTime();
				user.addAlbum(newOne);
				message = "Album '" + Album.ALBUM_NAME_SEARCH_BY_DATE + "' is created for search results. It will be replaced in next search. To keep it, simply change its name.";
			    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES/*, ButtonType.NO, ButtonType.CANCEL*/);
			    alert.showAndWait();
	    		user.setCurrAlbum(newOne);
	    		goToAlbumFromUser();
			}
		}
		else{
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please set search condition (dates) first.", ButtonType.YES/*, ButtonType.NO, ButtonType.CANCEL*/);
		    alert.showAndWait();
		}
	}

    /**
     * Searches through tags
     */
    public void tagSearch() {
		User user = getModel().getCurrUser();
		TagRestriction tagRestriction = user.getTags();
		
		if (!(tagRestriction.getTags().size() == 0)){
			List<Photo> list = user.tagSearch();
			String message;
			
			if (list.size() <= 0){
				message = "No photo matched";
			    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES/*, ButtonType.NO, ButtonType.CANCEL*/);
			    alert.showAndWait();
			}
			else{
				Album newOne = new Album(Album.ALBUM_NAME_SEARCH_BY_TAG, list);
				newOne.setCounterDateTime();
				user.addAlbum(newOne);
				message = "Album '" + Album.ALBUM_NAME_SEARCH_BY_TAG + "' is created for search results. It will be replaced in next search. To keep it, simply change its name.";
			    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES/*, ButtonType.NO, ButtonType.CANCEL*/);
			    alert.showAndWait();
	    		user.setCurrAlbum(newOne);
	    		goToAlbumFromUser();
			}
		}
		else{
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please set search condition (tags) first.", ButtonType.YES/*, ButtonType.NO, ButtonType.CANCEL*/);
		    alert.showAndWait();
		}
	}

    /**
     * Adds a new album
     */
    public void AddNewAlbum() {
		User user = getModel().getCurrUser();
		String album_name = newAlbumName.getText().trim();
		
		if (album_name.length() > 0) {
			user.addAlbum(album_name);
	    	table.refresh();
			newAlbumName.setText("");
		}
	}
    
    /**
     * Event handler: set current album when selection in table view changed
     */
    @Override
	public void changed(ObservableValue<? extends Album> observable, Album oldVal, Album newVal) {
    	PhotoModel model = getModel();
    	User user = model.getCurrUser();
    	
    	if (newVal != null) {
    		user.setCurrAlbum(newVal);
    	}
	}
}