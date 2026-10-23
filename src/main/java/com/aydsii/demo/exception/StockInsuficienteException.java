package com.aydsii.demo.exception;

/** Se lanza cuando un producto no está disponible o no alcanza el stock. Responde HTTP 409. */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
