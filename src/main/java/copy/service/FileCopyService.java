package copy.service;

import copy.model.CopyResult;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Serviço responsável por toda a lógica de negócio de cópia de diretórios.
 *
 * <p>Esta classe é totalmente independente de componentes de UI do JavaFX,
 * tornando-a facilmente testável e reutilizável. A única dependência do
 * JavaFX é a classe {@link Task}, que pertence ao módulo de concorrência
 * e não ao módulo de UI.</p>
 */
public class FileCopyService {

    /**
     * Cria e retorna uma {@link Task} que executa a cópia do diretório de origem
     * para o diretório de destino de forma assíncrona.
     *
     * <p>A validação dos parâmetros é feita dentro da Task para que o resultado
     * seja sempre retornado de forma uniforme via {@link CopyResult}, sem
     * lançar exceções para o chamador.</p>
     *
     * @param sourcePath caminho absoluto do diretório de origem
     * @param destPath   caminho absoluto do diretório de destino
     * @return uma Task que ao ser executada retorna um {@link CopyResult}
     */
    public Task<CopyResult> createCopyTask(String sourcePath, String destPath) {
        return new Task<>() {
            @Override
            protected CopyResult call() {
                CopyResult validationResult = validate(sourcePath, destPath);
                if (!validationResult.success()) {
                    return validationResult;
                }

                File source = new File(sourcePath);
                File dest   = new File(destPath);

                try {
                    FileUtils.copyDirectory(source, dest);
                    return CopyResult.ok("Cópia concluída com sucesso!");
                } catch (IOException e) {
                    return CopyResult.fail("Erro de I/O durante a cópia: " + e.getMessage());
                } catch (Exception e) {
                    return CopyResult.fail("Erro inesperado: " + e.getMessage());
                }
            }
        };
    }

    /**
     * Valida os parâmetros antes de iniciar a cópia.
     *
     * @param sourcePath caminho de origem
     * @param destPath   caminho de destino
     * @return {@link CopyResult#ok} se os parâmetros são válidos,
     *         ou {@link CopyResult#fail} com a mensagem de erro
     */
    private CopyResult validate(String sourcePath, String destPath) {
        if (sourcePath == null || sourcePath.isBlank()) {
            return CopyResult.fail("O caminho de origem não pode estar vazio.");
        }
        if (destPath == null || destPath.isBlank()) {
            return CopyResult.fail("O caminho de destino não pode estar vazio.");
        }

        File source = new File(sourcePath);

        if (!source.exists()) {
            return CopyResult.fail("O diretório de origem não existe: " + sourcePath);
        }
        if (!source.isDirectory()) {
            return CopyResult.fail("O caminho de origem não é um diretório válido.");
        }

        return CopyResult.ok("ok");
    }
}
