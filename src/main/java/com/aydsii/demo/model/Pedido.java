package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Compra realizada por un cliente. Está compuesto por uno o más {@link DetallePedido}.
 * Tabla "pedidos" (no "pedido") para evitar conflictos con palabras reservadas.
 */
@Entity
@Table(name = "pedidos")
//lombok, lo mismo, resuelve codigo repetido
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    /** Relación "realiza": un cliente realiza muchos pedidos. */
    //indica a la base de datos que no traiga los datos de una relación (o colección) hasta que se soliciten explícitamente en el código.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private TipoEntrega tipoEntrega;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    /** Relación "define": estado actual del pedido. */
    //indica a la base de datos que no traiga los datos de una relación (o colección) hasta que se soliciten explícitamente en el código.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoPedido estado;

    /** Relación "incluye" (composición): los detalles se guardan junto con el pedido. */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    /**
     * Relación "abona": un pedido tiene uno o más pagos.
     * Lado inverso (mappedBy): la clave foránea vive en la tabla "pagos", por eso acá
     * solo se usa para LEER los pagos del pedido (no tiene cascade).
     */
    @OneToMany(mappedBy = "pedido")
    private List<Pago> pagos = new ArrayList<>();

    /**
     * Relación "genera" (0..1): el envío del pedido, o null si se retira en el local.
     * Lado inverso (mappedBy): la clave foránea vive en la tabla "envios".
     */
    //indica a la base de datos que no traiga los datos de una relación (o colección) hasta que se soliciten explícitamente en el código.
    @OneToOne(mappedBy = "pedido", fetch = FetchType.LAZY)
    private Envio envio;

    /** Equivale al «create»(cliente, fecha, tipoEntrega) del diagrama de secuencia. */
    public Pedido(Cliente cliente, LocalDateTime fecha, TipoEntrega tipoEntrega) {
        this.cliente = cliente;
        this.fecha = fecha;
        this.tipoEntrega = tipoEntrega;
    }

    /** Agrega un detalle y lo enlaza con este pedido (mantiene ambos lados sincronizados). */
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        detalles.add(detalle);
    }

    /**
     * Confirma el pedido asignándole el estado CONFIRMADO.
     * Recibe el {@link EstadoPedido} porque el estado es una entidad persistida
     * (la entidad no puede consultar repositorios por sí sola).
     */
    public void confirmar(EstadoPedido estadoConfirmado) {
        this.estado = estadoConfirmado;
    }

    /**
     * Indica si este pedido fue realizado por el cliente dado.
     * Sirve para que un cliente no pueda ver pedidos de otro.
     *
     * @param idCliente id del cliente que consulta
     */
    public boolean perteneceA(Long idCliente) {
        return cliente.getIdCliente().equals(idCliente);
    }

    /** @return el estado actual del pedido (PENDIENTE, CONFIRMADO, ENVIADO, ...). */
    public EstadoPedido obtenerEstado() {
        return estado;
    }

    /** @return true si el pedido tiene un envío registrado. */
    public boolean tieneEnvio() {
        return envio != null;
    }
}
