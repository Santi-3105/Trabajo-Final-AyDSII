package com.aydsii.demo.exception;

import org.springframework.http.ProblemDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * Captura las excepciones lanzadas en cualquier capa y las convierte en una
 * respuesta HTTP con un {@link ProblemDetail} (status, title y detail). Así el
 * controller y el service no tienen que preocuparse por armar las respuestas de error.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404: algo que se buscó por id no existe. */
    @ExceptionHandler({
            ClienteNoEncontradoException.class,
            MedioPagoNoEncontradoException.class,
            ProductoNoEncontradoException.class,
            PedidoNoEncontradoException.class })
    public ResponseEntity<ProblemDetail> handleNoEncontrado(RuntimeException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** 400: carrito vacío o solicitud inconsistente. */
    @ExceptionHandler({ CarritoVacioException.class, SolicitudInvalidaException.class })
    public ResponseEntity<ProblemDetail> handleSolicitudIncorrecta(RuntimeException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * 409: conflicto con el estado actual (sin stock, o modificación concurrente
     * del stock).
     */
    @ExceptionHandler({ StockInsuficienteException.class, OptimisticLockingFailureException.class })
    public ResponseEntity<ProblemDetail> handleConflicto(RuntimeException ex) {
        String mensaje = (ex instanceof OptimisticLockingFailureException)
                ? "El stock fue modificado por otra compra al mismo tiempo, intente nuevamente"
                : ex.getMessage();
        return responder(HttpStatus.CONFLICT, mensaje);
    }

    /** 400: falló alguna validación de los DTO (@NotNull, @NotBlank, ...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidacion(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos - " + detalle);
    }

    /**
     * 400: el JSON está mal formado o tiene un valor inválido (ej: tipoEntrega =
     * "XYZ").
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleJsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud es inválido (revise el formato JSON y los valores permitidos)");
    }

    /**
     * 400: falta un parámetro obligatorio de la URL (ej: GET /api/pedidos sin
     * ?clienteId=).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleParametroFaltante(MissingServletRequestParameterException ex) {
        return responder(HttpStatus.BAD_REQUEST, "Falta el parámetro obligatorio '" + ex.getParameterName() + "'");
    }

    /**
     * 400: un parámetro o id de la URL tiene el tipo equivocado (ej:
     * /api/pedidos/abc).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El valor del parámetro '" + ex.getName() + "' es inválido");
    }

    /** 404: la URL pedida no existe. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleRutaInexistente(NoResourceFoundException ex) {
        return responder(HttpStatus.NOT_FOUND, "La ruta solicitada no existe");
    }

    /** 405: la URL existe pero con otro método HTTP (ej: GET en vez de POST). */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMetodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido para esta ruta");
    }

    /**
     * 500: cualquier error no previsto. Se loguea completo pero al cliente no se le
     * expone el detalle.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenerico(Exception ex) {
        log.error("Error inesperado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    /** Arma la respuesta de error con un ProblemDetail (el mensaje viaja en "detail"). */
    private ResponseEntity<ProblemDetail> responder(HttpStatus status, String mensaje) {
        log.warn("Respuesta de error {}: {}", status.value(), mensaje);
        return ResponseEntity.status(status)
                .body(ProblemDetail.forStatusAndDetail(status, mensaje));
    }
}
