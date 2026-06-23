package copy.model;

/**
 * Record imutável que representa o resultado de uma operação de cópia.
 * Transporta o status (sucesso/falha) e a mensagem descritiva entre as camadas.
 *
 * @param success true se a operação foi concluída com sucesso
 * @param message mensagem descritiva para exibição ao usuário
 */
public record CopyResult(boolean success, String message) {

    /**
     * Cria um resultado de sucesso com a mensagem informada.
     */
    public static CopyResult ok(String message) {
        return new CopyResult(true, message);
    }

    /**
     * Cria um resultado de falha com a mensagem de erro informada.
     */
    public static CopyResult fail(String message) {
        return new CopyResult(false, message);
    }
}
