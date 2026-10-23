package com.aydsii.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Datos del domicilio de envío. Solo es obligatorio cuando tipoEntrega = ENVIO.
 */
public record EnvioRequestDTO(

        @Schema(example = "San Martín")
        @NotBlank(message = "La calle es obligatoria")
        String calle,

        @Schema(example = "123")
        @NotBlank(message = "El número es obligatorio")
        String numero,

        @Schema(example = "General Pico")
        @NotBlank(message = "La localidad es obligatoria")
        String localidad,

        @Schema(example = "La Pampa")
        @NotBlank(message = "La provincia es obligatoria")
        String provincia,

        @Schema(example = "6360")
        @NotBlank(message = "El código postal es obligatorio")
        String codigoPostal
) {
}
