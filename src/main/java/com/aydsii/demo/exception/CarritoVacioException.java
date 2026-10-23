package com.aydsii.demo.exception;

/** Se lanza cuando el cliente no tiene carrito activo o el carrito no tiene items. Responde HTTP 400. */
public class CarritoVacioException extends RuntimeException {

    public CarritoVacioException(Long idCliente) {
        super("El cliente " + idCliente + " no tiene un carrito activo con productos");
    }
}
