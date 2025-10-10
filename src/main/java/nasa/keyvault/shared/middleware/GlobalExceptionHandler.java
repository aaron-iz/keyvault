package nasa.keyvault.shared.middleware;

import nasa.keyvault.shared.exceptions.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // NoSuchElementException → 404
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElement(NoSuchElementException ex) {
        return buildResponse("Resource not found", HttpStatus.NOT_FOUND, ex);
    }

    // IllegalArgumentException → 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse("Invalid argument", HttpStatus.BAD_REQUEST, ex);
    }

    // IllegalStateException → 400
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        return buildResponse("Illegal state", HttpStatus.BAD_REQUEST, ex);
    }

    // NullPointerException → 500
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointer(NullPointerException ex) {
        return buildResponse("Unexpected null encountered", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    // Database constraint violation → 400
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse("Database constraint violation", HttpStatus.BAD_REQUEST, ex);
    }

    // Unauthorized access -> 403
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Object> handleAccessException(UnauthorizedAccessException ex) {
        return buildResponse("You're unauthorized to perform this action.", HttpStatus.UNAUTHORIZED, ex);
    }

    // HTTP method not supported → 405
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return buildResponse("HTTP method not supported: " + ex.getMethod(), HttpStatus.METHOD_NOT_ALLOWED, ex);
    }

    // Validation errors from @Valid → 400
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                errors.append(err.getField()).append(": ").append(err.getDefaultMessage()).append("; ")
        );

        return buildResponse("Validation failed: " + errors, HttpStatus.BAD_REQUEST, ex);
    }

    // Catch-all → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex) {
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    // Utility method to build consistent JSON response
    private ResponseEntity<Object> buildResponse(String message, HttpStatus status, Exception ex) {
        logger.info("Unhandled exception was thrown, {}: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("exception", ex.getClass().getSimpleName());

        return new ResponseEntity<>(body, status);
    }
}

