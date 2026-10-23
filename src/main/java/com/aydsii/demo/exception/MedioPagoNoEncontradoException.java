package com.aydsii.demo.exception;

/** Se lanza cuando el medio de pago indicado no existe. Responde HTTP 404. */
public class MedioPagoNoEncontradoException extends RuntimeException {

    public MedioPagoNoEncontradoException(Long idMedioPago) {
        super("No se encontró el medio de pago con id " + idMedioPago);
    }
}
