package com.example.ui.controller;

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

    private final StudentService studentService;   // INTERFACE, không phải StudentServiceImpl
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

    @FXML private void handleAdd()     { }
    @FXML private void handleUpdate()  { }
    @FXML private void handleDelete()  { }
    @FXML private void handleClear()   { }

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
