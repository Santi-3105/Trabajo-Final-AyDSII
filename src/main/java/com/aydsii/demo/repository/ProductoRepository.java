package com.aydsii.demo.repository;

import com.aydsii.demo.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link Producto}. Usa findById y save heredados. */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
