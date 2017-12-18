package controller;

import model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;

/**
 * Controls the login page.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class LoginController extends ControllerBase implements ControllerSwitcher {
    @FXML TextField userID;
    
    /**
     * Initializes login stage
     */
    public void initBeforeShow() {
		getModel().setCurrUser(null);
		this.userID.clear();
	}
	
    /**
     * If enter key is pressed, login
     */
    public void initLogin(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
        	Login();
        }
	}
	
    /**
     * Performs login
     */
    public void Login() {
    	String userID = this.userID.getText().trim();
    	User user = getModel().getUser(userID);
    
		if (user == null){
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Not registered User");
			alert.setContentText("Username is not found");
			alert.showAndWait();
    		this.userID.setText("");
		}
		else{
			getModel().setCurrUser(user);
			
        	if (userID.equalsIgnoreCase("admin")) {
            	goToAdminFromLogin();
        	}
        	else {
            	goToUserFromLogin();
        	}
		}
    }

    /**
     * Exits program
     */
    public void Exit() {
		Platform.exit();
	}
}