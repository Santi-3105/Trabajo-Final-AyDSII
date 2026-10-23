package com.aydsii.demo.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Medio de pago disponible (efectivo, tarjeta, transferencia, ...). */
@Entity
@Table(name = "medios_pago")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medio_pago")
    private Long idMedioPago;

    @Column(nullable = false, length = 80)
    private String descripcion;

    public MedioPago(String descripcion) {
        this.descripcion = descripcion;
    }
}
