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

/**
 * Controller FXML da tela principal.
 *
 * <p>Responsabilidades desta classe:</p>
 * <ul>
 *   <li>Receber eventos de interação do usuário (cliques, ações)</li>
 *   <li>Delegar toda a lógica de negócio ao {@link FileCopyService}</li>
 *   <li>Atualizar os componentes de UI com base nos resultados recebidos</li>
 * </ul>
 */
public class CopyController {

    // ── Injeção de componentes via @FXML ─────────────────────────────────────
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

    // ── Variáveis para movimentação da janela ─────────────────────────────────
    private double xOffset = 0;
    private double yOffset = 0;

    // ── Dependências (backend) ────────────────────────────────────────────────
    private final FileCopyService fileCopyService = new FileCopyService();

    /**
     * Inicializa o controller e configura listeners e comportamentos visuais.
     */
    @FXML
    public void initialize() {
        // Define o tema escuro por padrão
        rootPane.getStyleClass().add("theme-dark");
        themeBtn.setText("☀");

        // Configura arrastar a janela sem bordas através da barra de título
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleBar.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Configura efeitos visuais de foco nos wrappers dos inputs
        setupFocusEffects(sourceField, sourceWrapper);
        setupFocusEffects(destField, destWrapper);
    }

    /**
     * Adiciona listeners para aplicar a borda/sombra colorida de foco no container do input.
     */
    private void setupFocusEffects(TextField field, HBox wrapper) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                wrapper.getStyleClass().add("input-wrapper-focused");
            } else {
                wrapper.getStyleClass().remove("input-wrapper-focused");
            }
        });

        // Quando o wrapper for clicado, dá foco ao campo de texto interno
        wrapper.setOnMouseClicked(event -> {
            field.requestFocus();
        });
    }

    // ── Handlers da Barra de Título Customizada ──────────────────────────────

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
        // A janela é de tamanho fixo, não faz nada
    }

    @FXML
    private void onClose() {
        Platform.exit();
    }

    // ── Handlers de eventos de navegação de pastas ───────────────────────────

    @FXML
    private void onBrowseSource() {
        browseDirectory("Selecionar diretório de origem", sourceField);
    }

    @FXML
    private void onBrowseDest() {
        browseDirectory("Selecionar diretório de destino", destField);
    }

    /**
     * Inicia o processo de cópia de forma assíncrona ao clicar no botão principal.
     */
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

    // ── Métodos utilitários privados ──────────────────────────────────────────

    /**
     * Exibe um {@link DirectoryChooser} nativo e popula o campo alvo com o caminho selecionado.
     */
    private void browseDirectory(String title, TextField target) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);

        Stage owner = (Stage) target.getScene().getWindow();
        File selected = chooser.showDialog(owner);

        if (selected != null) {
            target.setText(selected.getAbsolutePath());
        }
    }

    /**
     * Atualiza a UI com base no resultado retornado pelo {@link FileCopyService}.
     */
    private void handleResult(CopyResult result) {
        setRunning(false);
        setStatus(result.message(), !result.success());
        if (result.success()) {
            statusLabel.getStyleClass().removeAll("status-error");
            statusLabel.getStyleClass().add("status-success");
        }
    }

    /**
     * Alterna o estado da UI entre "processando" e "disponível".
     */
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

    /**
     * Define o texto e o estilo visual do label de status.
     */
    private void setStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        if (isError) {
            statusLabel.getStyleClass().add("status-error");
        }
    }
}
