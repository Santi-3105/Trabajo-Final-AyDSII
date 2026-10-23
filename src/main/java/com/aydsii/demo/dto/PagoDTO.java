package com.aydsii.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Un pago del pedido dentro de {@link PedidoEstadoDTO}. */
public record PagoDTO(
        Long idPago,
        LocalDateTime fecha,
        BigDecimal monto,
        String estado,      // PENDIENTE / APROBADO
        String medioPago    // descripción del medio de pago (ej: "Efectivo")
) {
}
