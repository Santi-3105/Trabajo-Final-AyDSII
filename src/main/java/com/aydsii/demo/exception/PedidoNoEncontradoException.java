package com.aydsii.demo.exception;

/**
 * Se lanza cuando el pedido no existe o no pertenece al cliente que lo consulta (FE-02).
 * No se distingue entre ambos casos a propósito, para no revelar a un cliente
 * qué ids de pedido existen. Responde HTTP 404.
 */
public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException(Long idPedido) {
        super("No se encontró el pedido " + idPedido + " para el cliente indicado");
    }
}
