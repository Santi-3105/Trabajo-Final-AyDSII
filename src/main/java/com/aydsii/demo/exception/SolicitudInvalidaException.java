package com.aydsii.demo.exception;

/** Se lanza cuando la solicitud es inconsistente (ej: tipoEntrega = ENVIO sin domicilio). Responde HTTP 400. */
public class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
