package app.taskmanager.exception;

public class ResourceForbiddenException extends RuntimeException {
    public ResourceForbiddenException(String message) {
        super(message);
    }
}
