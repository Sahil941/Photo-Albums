package controller;

import model.Album;
import model.Photo;
import model.PhotoModel;
import model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is base of all controllers.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class ControllerBase {
    /**
     * Help pop-up for admin
     */
    public static final String ADMIN_HELP_FXML 	= "/view/AdminHelp.fxml";

    /**
     * Help pop-up for photos app
     */
    public static final String PHOTO_HELP_FXML  = "/view/PhotoHelp.fxml";

    /**
     * File for storing data
     */
	public final static String DATA_FILE_PATH 	= "photos46.dat";

    /**
     * Login scene
     */
    protected static Scene sceneLogin = null;

    /**
     * Admin scene
     */
    public static Scene sceneAdmin = null;

    /**
     * User scene
     */
    public static Scene sceneUser = null;

    /**
     * Album scene
     */
    public static Scene sceneAlbum = null;

    /**
     * Controller for login
     */
    public static ControllerSwitcher controllerLogin = null;

    /**
     * Controller for admin
     */
    public static ControllerSwitcher controllerAdmin = null;

    /**
     * Controller for user
     */
    public static ControllerSwitcher controllerUser = null;

    /**
     * Controller for album
     */
    public static ControllerSwitcher controllerAlbum = null;

    /**
     * Login stage
     */
    public static Stage loginStage = null;

    /**
     * Album stage
     */
    public static Stage albumStage = null;

    /**
     * @param fml Filename for help pop-up
     */
    public static void Help(String fml) {
    	Parent root;
    	
		try {
			root = FXMLLoader.load(ControllerBase.class.getResource(fml));
	    	Stage window = new Stage();
	    	window.initModality(Modality.APPLICATION_MODAL);

			Scene scene = new Scene(root);
			window.setScene(scene);
			window.setTitle("Help");
			window.setResizable(false);
			window.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * @param primaryStage Primary stage of application
     * @throws Exception In case stage does not start
     */
    public static void start(Stage primaryStage) throws Exception {
		{	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ControllerBase.class.getResource("/view/Login.fxml"));
			Parent root = loader.load();
			sceneLogin = new Scene(root);
			controllerLogin = loader.getController();
		}

		{	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ControllerBase.class.getResource("/view/Admin.fxml"));
			Parent root = loader.load();
			sceneAdmin = new Scene(root);
			controllerAdmin = loader.getController();
		}

		{	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ControllerBase.class.getResource("/view/User.fxml"));
			Parent root = loader.load();
			sceneUser = new Scene(root);
			controllerUser = loader.getController();
		}

		{	FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ControllerBase.class.getResource("/view/Album.fxml"));
			Parent root = loader.load();
			sceneAlbum = new Scene(root);
			controllerAlbum = loader.getController();
		}

		primaryStage.setTitle("Not yet");
		primaryStage.setResizable(false);
		loginStage = primaryStage;
		albumStage = new Stage();
		primaryStage.setTitle("Not yet");
		albumStage.setResizable(false);

		goToLogin();
	}

    /**
     * Model for photos
     */
    private static PhotoModel model;

    /**
     * @return Model. Retrieve model from file. If could not create new model
     */
    public static PhotoModel getModel() {
        if (model == null) {
            try {
                FileInputStream fileIn = new FileInputStream(DATA_FILE_PATH);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                model = (PhotoModel)in.readObject();
                model.cleanUp(false);
                in.close();
                fileIn.close();
            }
            catch(IOException | ClassNotFoundException i) {
                model = null;
            }

            if (model == null) {
                model = new PhotoModel();
                model.addUser("admin");
                model.addUser("stock");
                model.setCurrUser(model.getUser("stock"));

                User user = model.getCurrUser();

                Album album1 = user.addAlbum("Cars");
                album1.addToAlbum(Photo.createPhoto("stockphotos/stockcar1.jpg", null));
                album1.addToAlbum(Photo.createPhoto("stockphotos/stockcar2.jpg", null));
                album1.addToAlbum(Photo.createPhoto("stockphotos/stockcar3.jpg", null));
                album1.addToAlbum(Photo.createPhoto("stockphotos/stockcar4.jpg", null));
                album1.addToAlbum(Photo.createPhoto("stockphotos/stockcar5.jpg", null));

                Album album2 = user.addAlbum("Airplanes");
                album2.addToAlbum(Photo.createPhoto("stockphotos/stockap1.jpg", null));
                album2.addToAlbum(Photo.createPhoto("stockphotos/stockap2.jpg", null));
                album2.addToAlbum(Photo.createPhoto("stockphotos/stockap3.jpg", null));
                album2.addToAlbum(Photo.createPhoto("stockphotos/stockap4.jpg", null));
                album2.addToAlbum(Photo.createPhoto("stockphotos/stockap5.jpg", null));
            }
        }

        return model;
    }

    /**
     * Helps store the PhotoModel to file
     */
    public static void storeModelToFile() {
		 if (model != null) {
	        model.cleanUp(true);

	         try {
	            FileOutputStream fileOut = new FileOutputStream(DATA_FILE_PATH);
	            ObjectOutputStream out = new ObjectOutputStream(fileOut);
	            out.writeObject(model);
	            out.close();
	            fileOut.close();
	         }
	         catch (IOException i) {
	             i.printStackTrace();
	         }
		 }
	 }

    /**
     * Goes to the album stage for users
     */
    public void goToAlbumFromUser() {
        albumStage.setScene(sceneAlbum);
        controllerAlbum.initBeforeShow();
        albumStage.setTitle("Album " + model.getCurrUser().getCurrAlbum().getAlbumName());
        albumStage.show();
    }

    /**
     * Goes to the admin stage for admin
     */
    public static void goToAdminFromLogin() {
		loginStage.setScene(sceneAdmin);
		controllerAdmin.initBeforeShow();
		loginStage.setTitle("Welcome to the Admin Tool!");
		loginStage.show();
    }

    /**
     * Goes to login stage from admin
     */
    public static void gotoLoginFromAdmin() {
    	goToLogin();
    }

    /**
     * Goes to login stage from albums
     */
    public static void goToLoginFromAlbum() {
    	albumStage.hide();
    	goToLogin();
    }

    /**
     * Goes to login stage from user
     */
    public static void goToLoginFromUser() {
    	albumStage.hide();
    	goToLogin();
    }

    /**
     * Goes to the login stage
     */
    private static void goToLogin() {
    	loginStage.setScene(sceneLogin);
		controllerLogin.initBeforeShow();
		loginStage.setTitle("Welcome To Photos App!");
		loginStage.show();
    }

    /**
     * If user logs in, goes to user
     */
    public static void goToUserFromLogin() {
		loginStage.hide();
		goToUser();
    }

    /**
     * Given admin, go to user stage
     */
    public static void goToUserFromAdmin() {
		loginStage.hide();
		goToUser();
    }

    /**
     * Go to user stage
     */
    public static void goToUserFromAlbum() {
		goToUser();
	}

    /**
     * Go to user stage
     */
    private static void goToUser() {
		albumStage.setScene(sceneUser);
		controllerUser.initBeforeShow();
		albumStage.setTitle("Welcome " + model.getCurrUser().getUsername() + "!");
		albumStage.show();
	}
}