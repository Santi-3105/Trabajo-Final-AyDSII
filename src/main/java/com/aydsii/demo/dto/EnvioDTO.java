package com.aydsii.demo.dto;

import java.time.LocalDate;

/**
 * Información de envío dentro de {@link PedidoEstadoDTO}.
 * <ul>
 *   <li>Envío pendiente (no despachado): solo domicilio; los datos de seguimiento van en null.</li>
 *   <li>Envío despachado o entregado: domicilio + datos de seguimiento completos.</li>
 * </ul>
 *
 * @param estadoEnvio PENDIENTE, DESPACHADO o ENTREGADO (se calcula, no se guarda en BD)
 */
public record EnvioDTO(
        String estadoEnvio,
        String calle,
        String numero,
        String localidad,
        String provincia,
        String codigoPostal,
        String empresaTransporte,
        String codigoSeguimiento,
        String ubicacionActual,
        LocalDate fechaDespacho,
        LocalDate fechaEstimadaEntrega,
        LocalDate fechaEntrega
) {
}
