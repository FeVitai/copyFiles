package copy.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Classe de bootstrap da aplicação JavaFX.
 *
 * <p>Responsabilidades únicas desta classe:</p>
 * <ul>
 *   <li>Carregar o arquivo FXML com o {@link FXMLLoader}</li>
 *   <li>Configurar o {@link Stage} (título, dimensões, etc.)</li>
 *   <li>Exibir a janela principal</li>
 * </ul>
 *
 * <p>Esta classe NÃO deve conter lógica de negócio, criação manual
 * de componentes de UI ou qualquer regra da aplicação.</p>
 */
public class CopyApp extends Application {

    private static final String FXML_PATH  = "/copy/view/copy-view.fxml";
    private static final String APP_TITLE  = "Copiador de Diretórios";
    private static final double WIN_WIDTH  = 660;
    private static final double WIN_HEIGHT = 430;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Scene scene = new Scene(loader.load(), WIN_WIDTH, WIN_HEIGHT);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(APP_TITLE);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
