package copy.controller;

import copy.model.CopyResult;
import copy.service.FileCopyService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class CopyController {

    @FXML private StackPane   rootPane;
    @FXML private HBox        titleBar;
    @FXML private Button      themeBtn;
    
    @FXML private HBox        sourceWrapper;
    @FXML private TextField   sourceField;
    @FXML private HBox        destWrapper;
    @FXML private TextField   destField;
    
    @FXML private Button      copyButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label       statusLabel;

    private double xOffset = 0;
    private double yOffset = 0;

    private final FileCopyService fileCopyService = new FileCopyService();

    @FXML
    public void initialize() {
        rootPane.getStyleClass().add("theme-dark");
        themeBtn.setText("☀");

        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        setupFocusEffects(sourceField, sourceWrapper);
        setupFocusEffects(destField, destWrapper);
    }

    private void setupFocusEffects(TextField field, HBox wrapper) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                wrapper.getStyleClass().add("input-wrapper-focused");
            } else {
                wrapper.getStyleClass().remove("input-wrapper-focused");
            }
        });

        wrapper.setOnMouseClicked(event -> {
            field.requestFocus();
        });
    }

    @FXML
    private void onToggleTheme() {
        if (rootPane.getStyleClass().contains("theme-light")) {
            rootPane.getStyleClass().remove("theme-light");
            rootPane.getStyleClass().add("theme-dark");
            themeBtn.setText("☀");
        } else {
            rootPane.getStyleClass().remove("theme-dark");
            rootPane.getStyleClass().add("theme-light");
            themeBtn.setText("☾");
        }
    }

    @FXML
    private void onMinimize() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void onMaximizeDummy() {
    }

    @FXML
    private void onClose() {
        Platform.exit();
    }

    @FXML
    private void onBrowseSource() {
        browseDirectory("Selecionar diretório de origem", sourceField);
    }

    @FXML
    private void onBrowseDest() {
        browseDirectory("Selecionar diretório de destino", destField);
    }

    @FXML
    private void onStartCopy() {
        String sourcePath = sourceField.getText().trim();
        String destPath   = destField.getText().trim();

        if (sourcePath.isEmpty() || destPath.isEmpty()) {
            setStatus("Por favor, selecione os diretórios de origem e destino.", true);
            return;
        }

        Task<CopyResult> task = fileCopyService.createCopyTask(sourcePath, destPath);

        task.setOnRunning(e  -> setRunning(true));
        task.setOnSucceeded(e -> handleResult(task.getValue()));
        task.setOnFailed(e    -> {
            Throwable ex = task.getException();
            setRunning(false);
            setStatus("Erro inesperado: " + (ex != null ? ex.getMessage() : "desconhecido"), true);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void browseDirectory(String title, TextField target) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);

        Stage owner = (Stage) target.getScene().getWindow();
        File selected = chooser.showDialog(owner);

        if (selected != null) {
            target.setText(selected.getAbsolutePath());
        }
    }

    private void handleResult(CopyResult result) {
        setRunning(false);
        setStatus(result.message(), !result.success());
        if (result.success()) {
            statusLabel.getStyleClass().removeAll("status-error");
            statusLabel.getStyleClass().add("status-success");
        }
    }

    private void setRunning(boolean running) {
        copyButton.setDisable(running);
        sourceWrapper.setDisable(running);
        destWrapper.setDisable(running);
        progressBar.setVisible(running);
        progressBar.setManaged(running);
        if (running) {
            setStatus("Copiando arquivos, aguarde...", false);
        }
    }

    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        if (isError) {
            statusLabel.getStyleClass().add("status-error");
        }
    }
}
