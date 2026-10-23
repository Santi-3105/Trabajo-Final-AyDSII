package com.aydsii.demo.exception;

/** Se lanza cuando el cliente no existe o está inactivo (activo = false). Responde HTTP 404. */
public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(Long idCliente) {
        super("No se encontró un cliente activo con id " + idCliente);
    }
}
