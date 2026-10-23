package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Línea de un {@link Pedido}. Guarda una "foto" del precio unitario al momento de la compra,
 * así si el precio del producto cambia después, el pedido histórico no se modifica.
 */
@Entity
@Table(name = "detalles_pedido")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    /** Relación "contiene": muchos detalles refieren a un mismo producto. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    /**
     * Equivale al «create»(producto, cantidad, precioUnitario, subtotal) del diagrama.
     * El subtotal se calcula acá (precioUnitario x cantidad). El pedido se asigna
     * luego con {@link Pedido#agregarDetalle(DetallePedido)}.
     */
    public DetallePedido(Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
