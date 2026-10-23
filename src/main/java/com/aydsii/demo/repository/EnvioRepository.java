package com.aydsii.demo.repository;

import com.aydsii.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link Envio}. Usa save heredado. */
@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
}
