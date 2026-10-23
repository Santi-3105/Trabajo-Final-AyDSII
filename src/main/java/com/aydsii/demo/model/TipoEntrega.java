package com.aydsii.demo.model;

/**
 * Forma en que el cliente recibe su pedido.
 * Si es ENVIO se registra además una entidad {@link Envio}.
 */
public enum TipoEntrega {
    /** El cliente retira el pedido personalmente (no genera Envio). */
    RETIRO,
    /** El pedido se envía a un domicilio (genera Envio). */
    ENVIO
}
