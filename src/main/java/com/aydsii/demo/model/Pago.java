package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Pago asociado a un {@link Pedido} ("abona") realizado con un {@link MedioPago} ("usa"). */
@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_medio_pago", nullable = false)
    private MedioPago medioPago;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    /** Equivale al «create»(pedidoGuardado, medioPago, monto, fecha) del diagrama. */
    public Pago(Pedido pedido, MedioPago medioPago, BigDecimal monto, LocalDateTime fecha) {
        this.pedido = pedido;
        this.medioPago = medioPago;
        this.monto = monto;
        this.fecha = fecha;
        this.estado = EstadoPago.PENDIENTE;
    }

    /** Registra el pago: pasa de PENDIENTE a APROBADO. */
    public void registrar() {
        this.estado = EstadoPago.APROBADO;
    }
}
