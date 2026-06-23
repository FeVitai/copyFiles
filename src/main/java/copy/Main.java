package copy;

/**
 * Ponto de entrada da JVM.
 *
 * <p>Esta classe existe para contornar a restrição do JavaFX que impede
 * que uma classe que extende {@link javafx.application.Application} seja
 * usada diretamente como Main-Class em um JAR executável sem que o runtime
 * do JavaFX esteja no module-path. O {@link javafx.application.Application#launch}
 * deve ser chamado a partir de uma classe que NÃO extende Application.</p>
 */
public class Main {
    public static void main(String[] args) {
        copy.app.CopyApp.main(args);
    }
}
