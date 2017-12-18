package controller;

import model.PhotoModel;
import model.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Helps functionality for admin account.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class AdminController extends ControllerBase implements ControllerSwitcher, ChangeListener<User> {
    @FXML
    ListView<User> listView;

    @FXML
    TextField name;

    /**
     * Clears the name field
     */
    public void initBeforeShow() {
        this.name.clear();
	}

    /**
     * Initializes the admin scene
     */
    public void initialize() {
    	PhotoModel photoModel = getModel();
    	
        this.listView.setItems(photoModel.getUserList());
        this.listView.getSelectionModel().selectedItemProperty().addListener(this);
        
        if (photoModel.getUserList().size() > 0) {
            this.listView.getSelectionModel().select(0);
        }
    }
	
	 /**
     * Adds a user
     */
    public void Add() {
    	String username = this.name.getText().trim();
    	
    	PhotoModel photoModel = getModel();
        
		if (username.isEmpty()){
			Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Missing Username");
            alert.setContentText("Username cannot be empty.");
            alert.showAndWait();
		}
		else{
			User user = photoModel.getUser(username);
        	
			if(user != null){
				Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Name should be unique.");
                alert.setContentText("User already exists.");
                alert.showAndWait();
			}
			else{
				photoModel.addUser(username);
                this.name.clear();
			}
		}
    }

    /**
     * Deletes a user
     */
    public void Delete() {
    	PhotoModel photoModel = getModel();
    	int index = this.listView.getSelectionModel().getSelectedIndex();
    	
    	if (index >= 0) {
    		photoModel.deleteUser(index);
    		this.listView.refresh();
    	}
    }

    /**
     * Goes to albums list for admin
     */
    public void goToAlbumList() {
        goToUserFromAdmin();
    }

    /**
     * Logs off admin
     */
    public void Logoff() {
	    gotoLoginFromAdmin();
	}

    /**
     * Exits admin
     */
    public void Exit() {
		Platform.exit();
	}

	@Override
	public void changed(ObservableValue<? extends User> arg0, User arg1, User arg2) {}
}