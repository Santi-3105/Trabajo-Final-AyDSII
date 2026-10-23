package com.aydsii.demo.repository;

import com.aydsii.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link MedioPago}. Usa findById heredado. */
@Repository
public interface MedioPagoRepository extends JpaRepository<MedioPago, Long> {
}
