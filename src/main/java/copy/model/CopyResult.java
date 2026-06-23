package copy.model;

public record CopyResult(boolean success, String message) {

    public static CopyResult ok(String message) {
        return new CopyResult(true, message);
    }

    public static CopyResult fail(String message) {
        return new CopyResult(false, message);
    }
}
