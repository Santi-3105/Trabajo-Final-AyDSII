package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla de estados de un pedido (PENDIENTE, CONFIRMADO, ...).
 * Se carga al iniciar la aplicación desde DataInitializer.
 */
@Entity
@Table(name = "estados_pedido")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EstadoPedido {

    /** Nombres de los estados que usa el sistema (evita "strings mágicos" en el código). */
    public static final String PENDIENTE = "PENDIENTE";
    public static final String CONFIRMADO = "CONFIRMADO";
    public static final String ENVIADO = "ENVIADO";
    public static final String ENTREGADO = "ENTREGADO";
    public static final String CANCELADO = "CANCELADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(nullable = false, unique = true, length = 30)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    public EstadoPedido(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
