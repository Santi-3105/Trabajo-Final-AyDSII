package com.aydsii.demo.dto;

import java.time.LocalDateTime;

/**
 * Envoltorio estándar de TODAS las respuestas de la API (éxito y error),
 * para que el cliente siempre reciba el mismo formato JSON.
 *
 * @param status    código HTTP (201, 404, 409, ...)
 * @param mensaje   descripción legible del resultado
 * @param data      contenido de la respuesta (null en errores)
 * @param timestamp momento en que se generó la respuesta
 * @param <T>       tipo del contenido
 */
public record ApiResponse<T>(int status, String mensaje, T data, LocalDateTime timestamp) {

    /** Construye una respuesta exitosa con datos. */
    public static <T> ApiResponse<T> success(int status, String mensaje, T data) {
        return new ApiResponse<>(status, mensaje, data, LocalDateTime.now());
    }

    /** Construye una respuesta de error (sin datos). */
    public static <T> ApiResponse<T> error(int status, String mensaje) {
        return new ApiResponse<>(status, mensaje, null, LocalDateTime.now());
    }
}
