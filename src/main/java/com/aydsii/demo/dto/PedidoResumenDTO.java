package com.aydsii.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fila del listado "mis pedidos" (GET /api/pedidos).
 * Solo lleva lo necesario para mostrar la lista; el detalle se pide aparte.
 * Equivale al «create»(id, fecha, total, estado) del diagrama.
 *
 * @param idPedido identificador del pedido
 * @param fecha    fecha y hora de la compra
 * @param total    importe total
 * @param estado   nombre del estado actual (ej: CONFIRMADO)
 */
public record PedidoResumenDTO(
        Long idPedido,
        LocalDateTime fecha,
        BigDecimal total,
        String estado
) {
}
