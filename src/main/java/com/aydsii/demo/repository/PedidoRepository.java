package com.aydsii.demo.repository;

import com.aydsii.demo.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de {@link Pedido}.
 * <p>
 * Nota sobre los nombres: el diagrama dice "findByClienteIdOrderByFechaDesc" y
 * "findByIdAndClienteId", pero en nuestras entidades las propiedades se llaman
 * {@code idCliente} e {@code idPedido}. Spring Data necesita que el nombre del
 * método coincida con las propiedades, por eso se escriben así
 * ("Cliente_IdCliente" = pedido.cliente.idCliente).
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Pedidos de un cliente, del más nuevo al más viejo (paso 3 del diagrama de listado).
     * {@code @EntityGraph} trae el estado junto con el pedido en una sola consulta,
     * así al armar el resumen no se hace una consulta extra por cada pedido.
     */
    @EntityGraph(attributePaths = "estado")
    List<Pedido> findByCliente_IdClienteOrderByFechaDesc(Long idCliente);

    /**
     * Busca un pedido por id SOLO si pertenece al cliente indicado (paso 16 del diagrama).
     * Si el pedido no existe o es de otro cliente devuelve Optional vacío,
     * y desde afuera ambos casos se ven igual (404).
     */
    Optional<Pedido> findByIdPedidoAndCliente_IdCliente(Long idPedido, Long idCliente);
}
