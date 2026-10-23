package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Datos de envío de un {@link Pedido} ("genera", 0..1). Solo existe si el
 * tipo de entrega del pedido es ENVIO. Los datos de seguimiento se completan
 * más adelante (otros casos de uso).
 */
@Entity
@Table(name = "envios")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Long idEnvio;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false, unique = true)
    private Pedido pedido;

    @Column(nullable = false, length = 120)
    private String calle;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, length = 80)
    private String localidad;

    @Column(nullable = false, length = 80)
    private String provincia;

    @Column(name = "codigo_postal", nullable = false, length = 15)
    private String codigoPostal;

    // --- Datos de seguimiento: se completan después de la compra ---
    @Column(name = "empresa_transporte", length = 80)
    private String empresaTransporte;

    @Column(name = "codigo_seguimiento", length = 80)
    private String codigoSeguimiento;

    @Column(name = "ubicacion_actual", length = 120)
    private String ubicacionActual;

    @Column(name = "fecha_despacho")
    private LocalDate fechaDespacho;

    @Column(name = "fecha_estimada_entrega")
    private LocalDate fechaEstimadaEntrega;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    /** Equivale al «create»(pedidoGuardado, calle, numero, localidad, provincia, codigoPostal). */
    public Envio(Pedido pedido, String calle, String numero, String localidad,
                 String provincia, String codigoPostal) {
        this.pedido = pedido;
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
        this.provincia = provincia;
        this.codigoPostal = codigoPostal;
    }

    /** @return true si el envío ya salió del depósito (tiene fecha de despacho). */
    public boolean fueDespachado() {
        return fechaDespacho != null;
    }

    /** @return true si el envío ya fue entregado al cliente (tiene fecha de entrega). */
    public boolean fueEntregado() {
        return fechaEntrega != null;
    }
}
