package model;

import java.util.ArrayList;
import java.io.Serializable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;

/**
 * Represents the data for this app.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class PhotoModel implements Serializable {    
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 6868170481828007340L;

	/**
     * List of users
     */
    private ObservableList<User> userList;

    /**
     * used when store model to file as ObservableList could not be Serialized
     */
    private ArrayList<User> keepUserList;

    /**
     * Current user
     */
    private User currUser;

    /**
     * @param stored Clean up before store to file or after retrieve from file
     */
    public void cleanUp(boolean stored) {
        if (!stored) {
        	this.userList = FXCollections.observableList(this.keepUserList);
            this.keepUserList = null;

            for (User user : this.userList) {
                user.cleanUp(false);
            }
        }
        else {
            this.keepUserList = new ArrayList<>(this.userList);
            this.userList = null;

            for (User user : this.keepUserList) {
                user.cleanUp(true);
            }
        }
    }

    /**
     * Initializes fields
     */
    public PhotoModel() {
        this.userList = FXCollections.observableArrayList();
        this.keepUserList = null;
        this.currUser = null;
    }

    /**
     * @return List of users
     */
    public ObservableList<User> getUserList() {
        return this.userList;
    }

    /**
     * @return Current user
     */
    public User getCurrUser() {
        return this.currUser;
    }

    /**
     * @param currUser Current user
     */
    public void setCurrUser(User currUser) {
        this.currUser = currUser;
    }

    /**
     * @param username Username
     */
    public void addUser(String username) {
        User user = new User(username);

        Helper.addOrGetList(this.userList, user);
    }

    public User deleteUser(String username) {
        if (username.equalsIgnoreCase(User.ADMIN)) {
        	return null;
        }
        else {
        	return Helper.delete(this.userList, username, (t,k)->t.getUsername().equalsIgnoreCase(k));
        }
    }

    /**
     * @param x Index of user in the list to delete
     */
    public void deleteUser(int x) {
        if ((x >= 0) && (x < this.userList.size())) {
            if (this.userList.get(x).getUsername().equalsIgnoreCase(User.ADMIN)) {
            	Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Deleting Admin.");
                alert.setContentText("Admin cannot be deleted.");
                alert.showAndWait();
            }
            else {
            	this.userList.remove(x);
            }
        }
    }

    /**
     * @param username Username
     * @return User
     */
    public User getUser(String username) {
        return this.userList.stream().filter(user->user.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }
}