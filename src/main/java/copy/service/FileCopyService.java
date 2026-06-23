package copy.service;

import copy.model.CopyResult;
import javafx.concurrent.Task;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileCopyService {

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
