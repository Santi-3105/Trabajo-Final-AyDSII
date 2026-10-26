package com.aydsii.demo.service;

import com.aydsii.demo.dto.*;
import com.aydsii.demo.exception.*;
import com.aydsii.demo.model.*;
import com.aydsii.demo.repository.*;
//Nos resuelve de escribir codigo repetitivo una y otra vez, basicamente genera este mediante notaciones en tiempo de compilacion como getters y setters 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lógica de negocio de los casos de uso sobre pedidos:
 * <ul>
 *   <li>Comprar: sigue el diagrama de secuencia POST /api/pedidos/comprar.</li>
 *   <li>Consultar estado de pedido: sigue el diagrama GET /api/pedidos y GET /api/pedidos/{id}.</li>
 * </ul>
 */

//Es una anotación de Lombok para crear mensajes automaticos por consolo del tipo log.info, basicamente para detectar errores en caso de fallas
@Slf4j
//Indica que es de tipo service
@Service
//{ this.x = x; } -> remplaza esta notación y hace exactamente eso de forma automatica, otra anotacion de lombok
@RequiredArgsConstructor // Inyecta por constructor todos los campos final
public class PedidoService {

    private final ClienteRepository clienteRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final PagoRepository pagoRepository;
    private final EnvioRepository envioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    /**
     * Convierte el carrito activo de un cliente en un pedido.
     * <p>
     * Es @Transactional: si CUALQUIER paso falla (ej: no hay stock del tercer producto)
     * se revierte TODO lo hecho antes (stock descontado, pedido, pago, etc.).
     *
     * @param dto datos de la compra (cliente, medio de pago, tipo de entrega, domicilio)
     * @return resumen del pedido creado
     */
    //Transacciones, es decir, cuando se termina toda la operacion guarda los cambios. En caso de que no pueda terminarla, los revierte
    //Esta son manejadas por RuntimeException, solo tenemos que extender nuestras exception de esa clase 
    @Transactional
    public PedidoResponseDTO comprarPedido(PedidoRequestDTO dto) {
        //dto.algo lo que hace es que una clase inmutable que genera sola constructor, accesores (que se llaman como el campo, sin get), equals, hashCode y toString
        //En otras palabras, no nos hace falta hacer gets para obtener los datos como idCliente
        log.info("Iniciando compra del cliente {}", dto.idCliente());

        // ===== 1. Validar cliente y carrito =====
        // Cliente debe existir y estar activo; si no -> ClienteNoEncontradoException (404)
        Cliente cliente = clienteRepository.findById(dto.idCliente())
                //Filtro tal que, si el cliente esta activo lo trae, sino lo deja vacio
                .filter(Cliente::estaActivo)
                .orElseThrow(() -> new ClienteNoEncontradoException(dto.idCliente()));

        // Carrito ACTIVO del cliente; si no existe o está vacío -> CarritoVacioException (400)
        Carrito carrito = carritoRepository
                .findByCliente_IdClienteAndEstado(dto.idCliente(), EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new CarritoVacioException(dto.idCliente()));
        if (carrito.getItems().isEmpty()) {
            throw new CarritoVacioException(dto.idCliente());
        }

        // Si la entrega es por envío, el domicilio es obligatorio
        if (dto.tipoEntrega() == TipoEntrega.ENVIO && dto.envio() == null) {
            throw new SolicitudInvalidaException("Para tipoEntrega ENVIO debe informar el domicilio de envío");
        }

        // ===== 2. Verificar stock de TODOS los productos antes de modificar nada =====
        // Se guardan los productos (releídos de la BD) en el mismo orden que los items del carrito
        List<Producto> productos = new ArrayList<>();
        for (ItemCarrito item : carrito.getItems()) {
            Producto producto = productoRepository.findById(item.getProducto().getIdProducto())
                    .orElseThrow(() -> new ProductoNoEncontradoException(item.getProducto().getIdProducto()));
            producto.verificarStock(item.getCantidad()); // lanza StockInsuficienteException (409)
            productos.add(producto);
        }

        // ===== 3. Crear pedido =====
        // Estado "CONFIRMADO" (tabla estados_pedido, cargada por DataInitializer)
        EstadoPedido estadoConfirmado = estadoPedidoRepository.findByNombre(EstadoPedido.CONFIRMADO)
                .orElseThrow(() -> new IllegalStateException("Falta el estado de pedido CONFIRMADO en la base de datos"));

        Pedido pedido = new Pedido(cliente, LocalDateTime.now(), dto.tipoEntrega());

        // Por cada item: crear el detalle, descontar stock y guardar el producto
        for (int i = 0; i < carrito.getItems().size(); i++) {
            ItemCarrito item = carrito.getItems().get(i);
            Producto producto = productos.get(i);

            // El precio se "congela" en el detalle al momento de la compra
            pedido.agregarDetalle(new DetallePedido(producto, item.getCantidad(), producto.getPrecio()));

            producto.descontarStock(item.getCantidad());
            productoRepository.save(producto);
        }

        BigDecimal total = carrito.calcularTotal();
        pedido.setTotal(total);
        pedido.confirmar(estadoConfirmado);

        // Guarda el pedido y, por cascade, todos sus detalles
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.debug("Pedido {} guardado con total {}", pedidoGuardado.getIdPedido(), total);

        // ===== 4. Registrar pago =====
        MedioPago medioPago = medioPagoRepository.findById(dto.idMedioPago())
                .orElseThrow(() -> new MedioPagoNoEncontradoException(dto.idMedioPago()));

        Pago pago = new Pago(pedidoGuardado, medioPago, pedidoGuardado.getTotal(), LocalDateTime.now());
        pago.registrar(); // PENDIENTE -> APROBADO
        pagoRepository.save(pago);

        // ===== 5. Registrar envío (solo si la entrega es por ENVIO) =====
        if (dto.tipoEntrega() == TipoEntrega.ENVIO) {
            var datos = dto.envio();
            Envio envio = new Envio(pedidoGuardado, datos.calle(), datos.numero(),
                    datos.localidad(), datos.provincia(), datos.codigoPostal());
            envioRepository.save(envio);
        }

        // ===== 6. Cerrar carrito =====
        carrito.finalizar(); // ACTIVO -> FINALIZADO
        carritoRepository.save(carrito);

        // ===== 7. Armar la respuesta =====
        log.info("Compra finalizada: pedido {} del cliente {}", pedidoGuardado.getIdPedido(), cliente.getIdCliente());
        return new PedidoResponseDTO(
                pedidoGuardado.getIdPedido(),
                pedidoGuardado.getFecha(),
                pedidoGuardado.getTipoEntrega(),
                pedidoGuardado.getTotal(),
                pedidoGuardado.getEstado().getNombre());
    }

    // =====================================================================
    //  CASO DE USO: CONSULTAR ESTADO DE PEDIDO
    // =====================================================================

    /**
     * Lista los pedidos de un cliente, del más reciente al más antiguo.
     * Diagrama: pasos 2 a 10 (GET /api/pedidos).
     * <p>
     * {@code readOnly = true}: la consulta no modifica datos, y además mantiene
     * abierta la sesión para poder leer las relaciones lazy (estado) al armar el DTO.
     *
     * @param clienteId cliente que consulta
     * @return lista de resúmenes; vacía si el cliente no tiene pedidos (FE-01)
     */
    //Solo lee, no modifica. Hibernate se ahorra trabajo (no vigila cambios) y la sesión queda abierta durante todo el método
    //Coincide con nuestro application.properties que tiene spring.jpa.open-in-view=false. Es decir que si se sale de una transaccion, la sesion se cierra apenas vuelva al repo
    @Transactional(readOnly = true)
    public List<PedidoResumenDTO> listarPedidosDelCliente(Long clienteId) {
        log.info("Listando pedidos del cliente {}", clienteId);

        // Paso 3-4: findByClienteIdOrderByFechaDesc -> pedidos
        List<Pedido> pedidos = pedidoRepository.findByCliente_IdClienteOrderByFechaDesc(clienteId);

        // FE-01: el cliente no tiene pedidos -> se devuelve listaVacia (el controller responde 200)
        if (pedidos.isEmpty()) {
            log.debug("El cliente {} no tiene pedidos", clienteId);
            //crea una lista vacia que no se puede modificar 
            return List.of();
        }

        // Paso 7-9: mapearAResumenDTO(pedidos) -> listaResumen
        return mapearAResumenDTO(pedidos);
    }

    /**
     * Devuelve el estado completo de UN pedido: estado, datos generales, detalles, pagos y envío.
     * Diagrama: pasos 15 a 27 (GET /api/pedidos/{id}).
     *
     * @param pedidoId  pedido que se quiere consultar
     * @param clienteId cliente que consulta (solo puede ver sus propios pedidos)
     * @return DTO con toda la información del pedido
     * @throws PedidoNoEncontradoException si no existe o no es de ese cliente (FE-02, HTTP 404)
     */
    @Transactional(readOnly = true)
    public PedidoEstadoDTO obtenerEstadoPedido(Long pedidoId, Long clienteId) {
        log.info("Consultando estado del pedido {} del cliente {}", pedidoId, clienteId);

        // Paso 16-17: findByIdAndClienteId -> Optional<pedido>
        // FE-02: si no existe o no es del cliente -> PedidoNoEncontradoException (paso 18)
        Pedido pedido = pedidoRepository.findByIdPedidoAndCliente_IdCliente(pedidoId, clienteId)
                .orElseThrow(() -> new PedidoNoEncontradoException(pedidoId));

        // Paso 20: obtenerEstado()
        EstadoPedido estado = pedido.obtenerEstado();

        // Paso 21: armarDetallePedido(pedido)
        List<DetallePedidoDTO> detalles = armarDetallePedido(pedido);

        // Paso 22: armarPagoPedido(pedido)
        List<PagoDTO> pagos = armarPagoPedido(pedido);

        // Pasos 23-25: según el tipo de entrega se arma (o se omite) la información de envío
        EnvioDTO envio;
        if (pedido.getTipoEntrega() == TipoEntrega.RETIRO) {
            // FA-01: el pedido se retira en el local -> no hay envío para mostrar
            envio = omitirEnvio();
        } else if (pedido.tieneEnvio()) {
            Envio datosEnvio = pedido.getEnvio();
            if (datosEnvio.fueDespachado()) {
                envio = armarEnvioCompleto(datosEnvio);   // ya salió: se muestra el seguimiento
            } else {
                envio = armarEnvioPendiente(datosEnvio);  // FA-02: todavía no fue despachado
            }
        } else {
            envio = omitirEnvio(); // defensivo: es ENVIO pero no hay registro de envío
        }

        // Datos generales del pedido (id, fecha, tipo de entrega y total)
        DatosGeneralesDTO datosGenerales = new DatosGeneralesDTO(
                pedido.getIdPedido(), pedido.getFecha(), pedido.getTipoEntrega(), pedido.getTotal());

        // Paso 26: «create»(estado, datosGenerales, detalles, pagos, envios)
        return new PedidoEstadoDTO(estado.getNombre(), datosGenerales, detalles, pagos, envio);
    }

    // ---------------------------------------------------------------------
    //  Métodos privados de armado de DTO (son los "mensajes a sí mismo" del diagrama)
    // ---------------------------------------------------------------------

    /** Paso 7: convierte cada pedido en un resumen para el listado. */
    private List<PedidoResumenDTO> mapearAResumenDTO(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(p -> new PedidoResumenDTO(   // paso 8: «create»(id, fecha, total, estado)
                        p.getIdPedido(),
                        p.getFecha(),
                        p.getTotal(),
                        p.obtenerEstado().getNombre()))
                .toList();
    }

    /** Paso 21: convierte los detalles del pedido (productos comprados) en DTOs. */
    private List<DetallePedidoDTO> armarDetallePedido(Pedido pedido) {
        return pedido.getDetalles().stream()
                .map(d -> new DetallePedidoDTO(
                        d.getIdDetalle(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getSubtotal()))
                .toList();
    }

    /** Paso 22: convierte los pagos del pedido en DTOs. */
    private List<PagoDTO> armarPagoPedido(Pedido pedido) {
        return pedido.getPagos().stream()
                .map(p -> new PagoDTO(
                        p.getIdPago(),
                        p.getFecha(),
                        p.getMonto(),
                        p.getEstado().name(),
                        p.getMedioPago().getDescripcion()))
                .toList();
    }

    /** Paso 23: pedido para RETIRO -> no hay información de envío (queda en null). */
    private EnvioDTO omitirEnvio() {
        return null;
    }

    /**
     * Paso 24: envío que todavía NO fue despachado.
     * Muestra solo el domicilio y la fecha estimada (si ya se conoce); el seguimiento va vacío.
     */
    private EnvioDTO armarEnvioPendiente(Envio envio) {
        return new EnvioDTO(
                "PENDIENTE",
                envio.getCalle(), envio.getNumero(), envio.getLocalidad(),
                envio.getProvincia(), envio.getCodigoPostal(),
                null, null, null, null,
                envio.getFechaEstimadaEntrega(), null);
    }

    /**
     * Paso 25: envío ya despachado (o entregado).
     * Incluye el seguimiento: empresa, código, ubicación actual y fechas.
     */
    private EnvioDTO armarEnvioCompleto(Envio envio) {
        String estadoEnvio = envio.fueEntregado() ? "ENTREGADO" : "DESPACHADO";
        return new EnvioDTO(
                estadoEnvio,
                envio.getCalle(), envio.getNumero(), envio.getLocalidad(),
                envio.getProvincia(), envio.getCodigoPostal(),
                envio.getEmpresaTransporte(), envio.getCodigoSeguimiento(),
                envio.getUbicacionActual(),
                envio.getFechaDespacho(), envio.getFechaEstimadaEntrega(), envio.getFechaEntrega());
    }
}
