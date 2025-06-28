package com.example.csc325_firebase_webview_auth.view;

import javafx.animation.PauseTransition;
import javafx.fxml.Initializable;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashScreenController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Wait 3 seconds, then switch to main screen
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                App.setRoot("/files/AccessFBView.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        delay.play();
    }
}
