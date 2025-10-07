package br.tec.omny.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.impl.FileCountLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.upload.max-file-count:20}")
    private int maxFileCount;

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "Arquivo excede o tamanho máximo permitido");
    }

    @ExceptionHandler(FileCountLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFileCountExceeded(FileCountLimitExceededException ex) {
        return build(HttpStatus.BAD_REQUEST, "Quantidade de arquivos excede o limite permitido");
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipart(MultipartException ex, HttpServletRequest request) {
        Throwable cause = ex.getMostSpecificCause();
        String msg = cause != null ? cause.getMessage() : ex.getMessage();

        if (cause instanceof FileCountLimitExceededException) {
            String field = msg; // Tomcat geralmente usa o nome do campo na mensagem (ex.: attachment)
            StringBuilder friendly = new StringBuilder();
            friendly.append("Quantidade de arquivos excede o limite permitido (máx ").append(maxFileCount).append(")");
            if (field != null) {
                friendly.append("; campo reportado pelo servidor: ").append(field);
            }
            friendly.append(". Campos aceitos: banner_texto_img (1), empresa_imagem (1), servicos_imagens[] (até ").append(maxFileCount).append(")");
            friendly.append(". Envie menos arquivos ou divida em múltiplas requisições.");
            return build(HttpStatus.BAD_REQUEST, friendly.toString());
        }

        // Evitar acessar MultipartHttpServletRequest aqui, pois o parse já falhou
        return build(HttpStatus.BAD_REQUEST, msg != null ? msg : "Erro no upload multipart");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    

    // Size limit errors will arrive as MaxUploadSizeExceededException or MultipartException

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos");
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}


