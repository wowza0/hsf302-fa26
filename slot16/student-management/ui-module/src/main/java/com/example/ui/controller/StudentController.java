package com.example.ui.controller;

import com.example.data.exception.StudentNotFoundException;
import com.example.data.exception.ValidationException;
import com.example.data.pojo.Student;
import com.example.data.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class StudentController {

    @FXML private TableView<Student> tableView;
    @FXML private TextField searchField, idField, nameField, emailField, ageField;
    @FXML private Button btnUpdate, btnDelete;
    @FXML private Label messageLabel, statusLabel;

    private final StudentService studentService;
    private final ObservableList<Student> data = FXCollections.observableArrayList();

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @FXML
    public void initialize() {
        tableView.setItems(data);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            fillForm(newVal);
        });

        btnUpdate.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        btnDelete.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        loadData();
    }

    private void loadData() {
        try {
            var list = studentService.findAll();
            data.setAll(list);
            statusLabel.setText("Loaded " + list.size() + " students");
            messageLabel.setText("");
        } catch (Exception e) {
            showError("Cannot load data: " + e.getMessage());
            statusLabel.setText("Database error");
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String kw = searchField.getText();
            var list = studentService.search(kw);
            data.setAll(list);
            statusLabel.setText("Found " + list.size() + " students");
            messageLabel.setText("");
        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadData();
    }

    @FXML
    private void handleAdd() {
        try {
            Student s = readForm();
            Student created = studentService.create(s);
            clearForm();
            loadData();
            showInfo("Added: " + created.getName());
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        try {
            Student selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            Student edit = readForm();
            edit.setId(selected.getId());
            Student updated = studentService.update(edit);
            clearForm();
            loadData();
            showInfo("Updated: " + updated.getName());
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Student selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText(null);
            alert.setContentText("Delete student \"" + selected.getName() + "\"?");
            var result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                studentService.delete(selected.getId());
                clearForm();
                loadData();
                showInfo("Deleted: " + selected.getName());
            }
        } catch (ValidationException | StudentNotFoundException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
        messageLabel.setText("");
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        ageField.clear();
        tableView.getSelectionModel().clearSelection();
        nameField.requestFocus();
    }

    private Student readForm() {
        String name = nameField.getText();
        String email = emailField.getText();
        String ageStr = ageField.getText();

        if (ageStr == null || ageStr.trim().isEmpty()) {
            throw new ValidationException("Age is required");
        }
        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a number");
        }
        return new Student(name, email, age);
    }

    private void fillForm(Student s) {
        if (s == null) {
            idField.clear();
            nameField.clear();
            emailField.clear();
            ageField.clear();
        } else {
            idField.setText(s.getId() == null ? "" : String.valueOf(s.getId()));
            nameField.setText(s.getName() == null ? "" : s.getName());
            emailField.setText(s.getEmail() == null ? "" : s.getEmail());
            ageField.setText(s.getAge() == null ? "" : String.valueOf(s.getAge()));
        }
    }

    private void showInfo(String msg) {
        messageLabel.setStyle("-fx-text-fill: #27ae60;");
        messageLabel.setText(msg);
    }

    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");
        messageLabel.setText(msg);
    }
}
