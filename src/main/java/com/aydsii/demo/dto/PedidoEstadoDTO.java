package com.aydsii.demo.dto;

import java.util.List;

/**
 * Respuesta de GET /api/pedidos/{id}: todo lo que el cliente necesita para
 * saber en qué situación está su pedido.
 * Equivale al «create»(estado, datosGenerales, detalles, pagos, envios) del diagrama
 * (el envío es 0..1, por eso el campo se llama "envio" en singular).
 *
 * @param estado         nombre del estado actual del pedido
 * @param datosGenerales id, fecha, tipo de entrega y total
 * @param detalles       productos comprados
 * @param pagos          pagos realizados
 * @param envio          datos de envío; null si el pedido es para RETIRO
 */
public record PedidoEstadoDTO(
        String estado,
        DatosGeneralesDTO datosGenerales,
        List<DetallePedidoDTO> detalles,
        List<PagoDTO> pagos,
        EnvioDTO envio
) {
}
