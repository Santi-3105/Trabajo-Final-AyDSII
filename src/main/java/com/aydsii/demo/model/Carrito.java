package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras de un cliente. Contiene {@link ItemCarrito} (composición).
 * <p>
 * Nota de diseño: el diagrama dice que el cliente "posee 0..1" carrito. Se modela como
 * muchos carritos por cliente (historial) pero con un único carrito ACTIVO a la vez,
 * porque al comprar el carrito se FINALIZA y el cliente necesita uno nuevo después.
 */
@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCarrito estado = EstadoCarrito.ACTIVO;

    /** Items del carrito. Se guardan/borran junto con el carrito (cascade + orphanRemoval). */
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();

    public Carrito(Cliente cliente) {
        this.cliente = cliente;
        this.estado = EstadoCarrito.ACTIVO;
    }

    /** Agrega un item al carrito manteniendo ambos lados de la relación sincronizados. */
    public void agregarItem(Producto producto, Integer cantidad) {
        items.add(new ItemCarrito(this, producto, cantidad));
    }

    /** @return suma de los subtotales (precio x cantidad) de todos los items. */
    public BigDecimal calcularTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Cierra el carrito: ya no se puede volver a comprar. */
    public void finalizar() {
        this.estado = EstadoCarrito.FINALIZADO;
    }
}
