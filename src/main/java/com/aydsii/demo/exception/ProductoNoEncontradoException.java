package com.aydsii.demo.exception;

/** Se lanza cuando un producto del carrito ya no existe en el catálogo. Responde HTTP 404. */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long idProducto) {
        super("No se encontró el producto con id " + idProducto);
    }
}
