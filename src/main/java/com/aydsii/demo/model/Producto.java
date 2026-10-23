package com.aydsii.demo.model;

import com.aydsii.demo.exception.StockInsuficienteException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Producto del catálogo. Conoce su stock y sabe verificarlo / descontarlo
 * (métodos {@code verificar_stock} y {@code descontar_stock} del diagrama estático).
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 80)
    private String categoria;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    /** true = disponible para la venta, false = discontinuado. */
    @Column(nullable = false)
    private Boolean estado = true;

    /**
     * Control de concurrencia optimista: si dos compras intentan descontar el stock
     * del mismo producto a la vez, la segunda falla (HTTP 409) en lugar de pisar el valor.
     */
    @Version
    private Long version;

    public Producto(String nombre, String categoria, String descripcion,
                    BigDecimal precio, Integer stock, Boolean estado) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.estado = estado;
    }

    /**
     * Verifica que el producto esté disponible y que alcance el stock.
     *
     * @param cantidad unidades que se quieren comprar
     * @throws StockInsuficienteException si no está disponible o no hay stock suficiente
     */
    public void verificarStock(Integer cantidad) {
        if (!Boolean.TRUE.equals(estado)) {
            throw new StockInsuficienteException(
                    "El producto '" + nombre + "' no está disponible para la venta");
        }
        if (stock < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para '" + nombre + "': solicitado " + cantidad
                            + ", disponible " + stock);
        }
    }

    /**
     * Resta la cantidad comprada al stock. Vuelve a verificar por seguridad
     * (por ejemplo si el mismo producto aparece dos veces en el carrito).
     *
     * @param cantidad unidades a descontar
     */
    public void descontarStock(Integer cantidad) {
        verificarStock(cantidad);
        this.stock -= cantidad;
    }
}
