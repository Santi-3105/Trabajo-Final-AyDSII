package com.aydsii.demo.repository;

import com.aydsii.demo.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Acceso a datos de {@link EstadoPedido}. */
@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {

    /** Busca un estado por su nombre (ej: "CONFIRMADO"). */
    Optional<EstadoPedido> findByNombre(String nombre);
}
