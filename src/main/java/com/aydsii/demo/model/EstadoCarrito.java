package com.aydsii.demo.model;

/**
 * Estados posibles de un {@link Carrito}.
 * Un cliente solo puede tener un carrito en estado ACTIVO a la vez.
 */
public enum EstadoCarrito {
    /** Carrito en uso, todavía se pueden agregar productos y comprarlo. */
    ACTIVO,
    /** Carrito ya comprado (se cierra al finalizar el caso de uso "comprar"). */
    FINALIZADO
}
