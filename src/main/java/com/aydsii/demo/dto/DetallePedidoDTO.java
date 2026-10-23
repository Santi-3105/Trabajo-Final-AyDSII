package com.aydsii.demo.dto;

import java.math.BigDecimal;

/** Una línea del pedido (producto comprado) dentro de {@link PedidoEstadoDTO}. */
public record DetallePedidoDTO(
        Long idDetalle,
        String producto,          // nombre del producto
        Integer cantidad,
        BigDecimal precioUnitario, // precio al momento de la compra
        BigDecimal subtotal
) {
}
