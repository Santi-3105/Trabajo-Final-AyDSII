package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Persona que realiza compras. Solo los clientes con {@code activo = true}
 * pueden comprar (se valida en PedidoService).
 */
@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Constructor vacío requerido por JPA
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 120)
    private String email;

    @Column(length = 30)
    private String telefono;

    /** Si es false el cliente está dado de baja y no puede comprar. */
    @Column(nullable = false)
    private Boolean activo = true;

    public Cliente(String nombre, String apellido, String dni, LocalDate fechaNacimiento,
                   String email, String telefono, Boolean activo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
    }

    /**
     * Indica si el cliente está habilitado para operar (activo = true).
     * Se usa tanto al comprar como al consultar pedidos.
     */
    public boolean estaActivo() {
        return Boolean.TRUE.equals(activo);
    }
}
