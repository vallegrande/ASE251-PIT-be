package vallegrande.edu.pe.backend.rest;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(NoSuchElementException exception, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiErrorResponse("NOT_FOUND", exception.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
		return ResponseEntity.badRequest()
				.body(new ApiErrorResponse("VALIDATION_ERROR", "Los datos enviados no son válidos", request.getRequestURI()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception, HttpServletRequest request) {
		// Comentario explicativo: Captura los IllegalArgumentException para responder con HttpStatus.BAD_REQUEST en validaciones
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ApiErrorResponse("BAD_REQUEST", exception.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception exception, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiErrorResponse("INTERNAL_ERROR", "Ocurrió un error inesperado", request.getRequestURI()));
	}

	public record ApiErrorResponse(String code, String message, String path, LocalDateTime timestamp) {
		public ApiErrorResponse(String code, String message, String path) {
			this(code, message, path, LocalDateTime.now());
		}
	}
}