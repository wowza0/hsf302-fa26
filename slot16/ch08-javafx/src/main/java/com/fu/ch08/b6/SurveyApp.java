package com.fu.ch08.b6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SurveyApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SurveyApp.class.getResource("survey-view.fxml"));
        stage.setScene(new Scene(loader.load(), 760, 720));
        stage.setTitle("B6 - Khảo sát môn học HSF302");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
