package com.fu.demologin;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class LoginController {

    // ===== FXML controls =====
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberCheckBox;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Hyperlink registerLink;
    @FXML private Label errorLabel; // add this to your FXML

    // ===== Config =====
    private static final int MAX_ATTEMPTS = 3;
    private static final int LOCK_SECONDS = 30;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String PREF_USERNAME = "remembered_username";

    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
    private final AuthService authService = new AuthService();
    private int failedAttempts = 0;

    // ===== Lifecycle =====
    @FXML
    public void initialize() {
        hideError();

        // Pre-fill the username if "Remember me" was used before
        String saved = prefs.get(PREF_USERNAME, "");
        if (!saved.isEmpty()) {
            usernameField.setText(saved);
            rememberCheckBox.setSelected(true);
            passwordField.requestFocus();
        }

        // Clear the error as soon as the user starts typing again
        usernameField.textProperty().addListener((o, a, b) -> hideError());
        passwordField.textProperty().addListener((o, a, b) -> hideError());
    }

    // ===== Handlers =====
    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // 1. Validate input
        String validationError = validate(username, password);
        if (validationError != null) {
            showError(validationError);
            return;
        }

        // 2. Authenticate
        if (!authService.authenticate(username, password)) {
            failedAttempts++;
            passwordField.clear();

            int remaining = MAX_ATTEMPTS - failedAttempts;
            if (remaining <= 0) {
                lockLogin();
            } else {
                showError("Invalid username or password. " + remaining + " attempt(s) left.");
            }
            return;
        }

        // 3. Success
        failedAttempts = 0;
        saveRememberMe(username);
        goToHome(username);
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        System.out.println("Go to Forgot Password screen");
    }

    @FXML
    void handleRegister(ActionEvent event) {
        System.out.println("Go to Register screen");
    }

    // ===== Helpers =====
    private String validate(String username, String password) {
        if (username.isEmpty() && password.isEmpty()) {
            return "Please enter your username and password.";
        }
        if (username.isEmpty()) {
            return "Username cannot be empty.";
        }
        if (password.isEmpty()) {
            return "Password cannot be empty.";
        }
        if (username.length() < MIN_USERNAME_LENGTH) {
            return "Username must be at least " + MIN_USERNAME_LENGTH + " characters.";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.";
        }
        return null;
    }

    /** Disable the form for LOCK_SECONDS after too many failed attempts. */
    private void lockLogin() {
        setFormDisabled(true);
        showError("Too many failed attempts. Try again in " + LOCK_SECONDS + " seconds.");

        PauseTransition unlock = new PauseTransition(Duration.seconds(LOCK_SECONDS));
        unlock.setOnFinished(e -> {
            failedAttempts = 0;
            setFormDisabled(false);
            hideError();
        });
        unlock.play();
    }

    private void setFormDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
        loginButton.setDisable(disabled);
    }

    /** Only the username is remembered, never the password. */
    private void saveRememberMe(String username) {
        if (rememberCheckBox.isSelected()) {
            prefs.put(PREF_USERNAME, username);
        } else {
            prefs.remove(PREF_USERNAME);
        }
    }

    private void goToHome(String username) {
        // Change "home-view.fxml" to your real next screen
        URL fxml = getClass().getResource("home-view.fxml");
        if (fxml == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login successful");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + username + "!");
            alert.showAndWait();
            return;
        }

        try {
            Parent root = new FXMLLoader(fxml).load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
        } catch (IOException e) {
            showError("Could not open the home screen: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    // ===== Demo authentication service (in-memory) =====
    // Replace with a database / REST call and BCrypt (or Argon2) in a real app.
    private static class AuthService {

        private record Credential(byte[] salt, byte[] hash) {}

        private final Map<String, Credential> users = new HashMap<>();
        private final SecureRandom random = new SecureRandom();

        AuthService() {
            register("admin", "admin123");
            register("student", "student123");
        }

        private void register(String username, String password) {
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            users.put(username.toLowerCase(), new Credential(salt, hash(password, salt)));
        }

        boolean authenticate(String username, String password) {
            Credential cred = users.get(username.toLowerCase());
            if (cred == null) {
                // Hash anyway so timing doesn't reveal whether the user exists
                hash(password, new byte[16]);
                return false;
            }
            // Constant-time comparison
            return MessageDigest.isEqual(cred.hash(), hash(password, cred.salt()));
        }

        private byte[] hash(String password, byte[] salt) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt);
                return md.digest(password.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        }
    }
}