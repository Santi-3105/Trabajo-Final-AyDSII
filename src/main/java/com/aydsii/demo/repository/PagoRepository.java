package com.aydsii.demo.repository;

import com.aydsii.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link Pago}. Usa save heredado. */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
}
