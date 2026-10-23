package com.aydsii.demo.model;

/** Estados posibles de un {@link Pago}. */
public enum EstadoPago {
    /** Pago creado pero todavía no registrado/acreditado. */
    PENDIENTE,
    /** Pago registrado correctamente. */
    APROBADO
}
