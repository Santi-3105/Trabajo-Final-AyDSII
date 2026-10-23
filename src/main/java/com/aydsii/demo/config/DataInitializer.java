package com.aydsii.demo.config;

import com.aydsii.demo.model.*;
import com.aydsii.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Carga datos de ejemplo al iniciar la aplicación para poder probar la compra desde Swagger
 * sin tener que armar el carrito a mano (este caso de uso no incluye "agregar al carrito").
 * Solo corre si la base está vacía, así no duplica datos en cada arranque.
 * <p>
 * Para producción conviene borrar esta clase y cargar los datos maestros con scripts SQL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EstadoPedidoRepository estadoPedidoRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;

    @Override
    public void run(String... args) {
        if (estadoPedidoRepository.count() > 0) {
            log.info("Datos iniciales ya cargados, no se vuelven a crear");
            return;
        }

        // --- Datos maestros obligatorios (el service necesita el estado CONFIRMADO) ---
        estadoPedidoRepository.save(new EstadoPedido(EstadoPedido.PENDIENTE, "Pedido creado, pendiente de confirmación"));
        estadoPedidoRepository.save(new EstadoPedido(EstadoPedido.CONFIRMADO, "Pedido confirmado y pagado"));
        estadoPedidoRepository.save(new EstadoPedido(EstadoPedido.ENVIADO, "Pedido despachado"));
        estadoPedidoRepository.save(new EstadoPedido(EstadoPedido.ENTREGADO, "Pedido entregado al cliente"));
        estadoPedidoRepository.save(new EstadoPedido(EstadoPedido.CANCELADO, "Pedido cancelado"));

        medioPagoRepository.save(new MedioPago("Efectivo"));
        medioPagoRepository.save(new MedioPago("Tarjeta de crédito"));
        medioPagoRepository.save(new MedioPago("Transferencia bancaria"));

        // --- Productos ---
        Producto notebook = productoRepository.save(new Producto("Notebook 15\"", "Computación",
                "Notebook 8GB RAM / 256GB SSD", new BigDecimal("850000.00"), 5, true));
        Producto mouse = productoRepository.save(new Producto("Mouse inalámbrico", "Periféricos",
                "Mouse óptico inalámbrico", new BigDecimal("12500.50"), 10, true));
        Producto teclado = productoRepository.save(new Producto("Teclado mecánico", "Periféricos",
                "Teclado mecánico retroiluminado", new BigDecimal("45000.00"), 8, true));

        // --- Clientes (cada uno sirve para probar un escenario distinto) ---
        Cliente juan = clienteRepository.save(new Cliente("Juan", "Pérez", "30111222",
                LocalDate.of(1990, 5, 20), "juan@mail.com", "2302111111", true));
        clienteRepository.save(new Cliente("María", "Gómez", "28333444",
                LocalDate.of(1985, 3, 10), "maria@mail.com", "2302222222", false)); // INACTIVA
        Cliente carlos = clienteRepository.save(new Cliente("Carlos", "López", "35555666",
                LocalDate.of(1995, 8, 1), "carlos@mail.com", "2302333333", true));
        Cliente ana = clienteRepository.save(new Cliente("Ana", "Díaz", "33777888",
                LocalDate.of(1992, 11, 30), "ana@mail.com", "2302444444", true));

        // Carrito de Juan: compra exitosa (1 notebook + 2 mouse)
        Carrito carritoJuan = new Carrito(juan);
        carritoJuan.agregarItem(notebook, 1);
        carritoJuan.agregarItem(mouse, 2);
        carritoRepository.save(carritoJuan);

        // Carrito de Carlos: vacío -> error 400
        carritoRepository.save(new Carrito(carlos));

        // Carrito de Ana: pide 50 teclados y hay 8 -> error 409
        Carrito carritoAna = new Carrito(ana);
        carritoAna.agregarItem(teclado, 50);
        carritoRepository.save(carritoAna);

        log.info("Datos iniciales cargados");
    }
}
