package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;
import com.google.cloud.storage.Blob;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class AccessFBView {

    @FXML private TextField nameField;
    @FXML private TextField majorField;
    @FXML private TextField ageField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextArea outputField;
    @FXML private Button writeButton;
    @FXML private Button readButton;

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return this.listOfUsers;
    }

    public void initialize() {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }

    @FXML
    private void addRecord(ActionEvent event) {
        this.addData();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        this.readFirebase();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    public void addData() {
        try {
            DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());
            Map<String, Object> data = new HashMap<>();
            data.put("Name", nameField.getText());
            data.put("Major", majorField.getText());
            data.put("Age", Integer.parseInt(ageField.getText()));
            docRef.set(data);
            outputField.setText("✅ Data added.");
        } catch (Exception e) {
            outputField.setText("❌ Error: " + e.getMessage());
        }
    }

    public boolean readFirebase() {
        this.key = false;
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.size() > 0) {
                outputField.setText("📋 Records:\n");

                for (QueryDocumentSnapshot doc : documents) {
                    String entry = doc.get("Name") + ", Major: " + doc.get("Major") + ", Age: " + doc.get("Age") + "\n";
                    outputField.appendText(entry);
                    person = new Person(doc.get("Name").toString(), doc.get("Major").toString(), Integer.parseInt(doc.get("Age").toString()));
                    listOfUsers.add(person);
                }
            } else {
                outputField.setText("No data found.");
            }
            key = true;
        } catch (Exception e) {
            outputField.setText("❌ Failed to read: " + e.getMessage());
        }

        return key;
    }

    @FXML
    public void registerUserFromInput() {
        try {
            String email = emailField.getText();
            String password = passwordField.getText();

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = App.fauth.createUser(request);
            outputField.setText("✅ Registered: " + userRecord.getEmail());

        } catch (FirebaseAuthException e) {
            outputField.setText("❌ Registration failed: " + e.getMessage());
        }
    }

    @FXML
    public void loginUser() {
        outputField.setText("⚠️ Login not supported by Admin SDK. Use WebView or REST for real auth.");
    }

    @FXML
    public void uploadProfilePicture() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );

            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                String fileName = "profile_pictures/" + file.getName();
                FileInputStream stream = new FileInputStream(file);

                Blob blob = StorageClient.getInstance().bucket().create(fileName, stream, "image/jpeg");

                outputField.setText("✅ Uploaded: " + blob.getName() + "\nURL: " + blob.getMediaLink());
            } else {
                outputField.setText("⚠️ Upload canceled.");
            }
        } catch (Exception e) {
            outputField.setText("❌ Upload failed: " + e.getMessage());
        }
    }
}
