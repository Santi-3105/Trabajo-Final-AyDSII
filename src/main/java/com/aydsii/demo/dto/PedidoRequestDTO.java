package com.aydsii.demo.dto;

import com.aydsii.demo.model.TipoEntrega;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo (JSON) del POST /api/pedidos/comprar.
 * El carrito NO se envía: se busca en base de datos a partir del cliente.
 *
 * @param idCliente   cliente que realiza la compra
 * @param idMedioPago medio con el que se abona
 * @param tipoEntrega RETIRO o ENVIO
 * @param envio       domicilio de entrega (obligatorio solo si tipoEntrega = ENVIO)
 */
public record PedidoRequestDTO(

        @Schema(example = "1", description = "ID del cliente que compra")
        @NotNull(message = "El idCliente es obligatorio")
        Long idCliente,

        @Schema(example = "1", description = "ID del medio de pago")
        @NotNull(message = "El idMedioPago es obligatorio")
        Long idMedioPago,

        @Schema(example = "ENVIO", description = "RETIRO o ENVIO")
        @NotNull(message = "El tipoEntrega es obligatorio")
        TipoEntrega tipoEntrega,

        @Valid // valida también los campos internos del domicilio
        @Schema(description = "Domicilio de envío (solo si tipoEntrega = ENVIO)")
        EnvioRequestDTO envio
) {
}
