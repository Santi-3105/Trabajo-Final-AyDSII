package com.aydsii.demo.dto;

import com.aydsii.demo.model.TipoEntrega;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos generales del pedido dentro de {@link PedidoEstadoDTO}. */
public record DatosGeneralesDTO(
        Long idPedido,
        LocalDateTime fecha,
        TipoEntrega tipoEntrega,
        BigDecimal total
) {
}
