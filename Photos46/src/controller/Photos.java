package controller;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This class is used to launch the application and store data upon stop.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class Photos extends Application {
	/**
	 * @param primaryStage Primary stage of application
	 * @throws Exception in case stage does not start
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		ControllerBase.start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Store data to file upon program stop
	 */
	@Override
	public void stop(){
        ControllerBase.storeModelToFile();
	}
}