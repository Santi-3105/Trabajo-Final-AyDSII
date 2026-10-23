package com.aydsii.demo.controller;

import com.aydsii.demo.dto.ApiResponse;
import com.aydsii.demo.dto.PedidoRequestDTO;
import com.aydsii.demo.dto.PedidoEstadoDTO;
import com.aydsii.demo.dto.PedidoResponseDTO;
import com.aydsii.demo.dto.PedidoResumenDTO;
import com.aydsii.demo.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Punto de entrada HTTP de los casos de uso sobre pedidos: comprar y consultar estado.
 * Solo recibe la solicitud, delega en {@link PedidoService} y arma la respuesta HTTP.
 * Los errores los resuelve el GlobalExceptionHandler.
 */
@Slf4j
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones sobre pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * POST /api/pedidos/comprar
     * {@code @Valid} dispara las validaciones del DTO antes de entrar al método.
     */
    @PostMapping("/comprar")
    @Operation(summary = "Comprar",
            description = "Convierte el carrito activo del cliente en un pedido: valida stock, "
                    + "descuenta inventario, registra el pago y el envío (si corresponde) y cierra el carrito.")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> comprarPedido(
            @Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {

        log.debug("POST /api/pedidos/comprar - request: {}", pedidoRequestDTO);

        PedidoResponseDTO pedidoCreado = pedidoService.comprarPedido(pedidoRequestDTO);

        ApiResponse<PedidoResponseDTO> apiResponse =
                ApiResponse.success(HttpStatus.CREATED.value(), "Pedido creado correctamente", pedidoCreado);

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * GET /api/pedidos?clienteId=1
     * Lista los pedidos del cliente (más recientes primero). Diagrama: pasos 1 a 13.
     * <p>
     * Como el sistema todavía no tiene login, el cliente se identifica con el parámetro
     * {@code clienteId}. Con autenticación, este dato saldría de la sesión del usuario.
     */
    @GetMapping
    @Operation(summary = "Listar pedidos del cliente",
            description = "Devuelve un resumen (id, fecha, total, estado) de todos los pedidos del cliente, "
                    + "del más reciente al más antiguo. Si no tiene pedidos devuelve una lista vacía (HTTP 200).")
    public ResponseEntity<ApiResponse<List<PedidoResumenDTO>>> listarPedidosDelCliente(
            @Parameter(description = "ID del cliente que consulta", example = "1")
            @RequestParam("clienteId") Long clienteId) {

        log.debug("GET /api/pedidos - clienteId: {}", clienteId);

        // Paso 2: delega en el service
        List<PedidoResumenDTO> pedidos = pedidoService.listarPedidosDelCliente(clienteId);

        // FE-01 (lista vacía) y flujo normal responden 200; solo cambia el mensaje
        String mensaje = pedidos.isEmpty()
                ? "El cliente no tiene pedidos registrados"
                : "Pedidos obtenidos correctamente";

        // Pasos 6/10: success(200, mensaje, lista)
        ApiResponse<List<PedidoResumenDTO>> apiResponse =
                ApiResponse.success(HttpStatus.OK.value(), mensaje, pedidos);

        // Paso 12-13: ResponseEntity.status(200).body(apiResponse) -> HTTP 200 + JSON
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    /**
     * GET /api/pedidos/{id}?clienteId=1
     * Devuelve el estado completo de un pedido. Diagrama: pasos 14 a 31.
     * <p>
     * Si el pedido no existe o no es del cliente, el service lanza
     * PedidoNoEncontradoException y el GlobalExceptionHandler responde 404
     * (pasos 18-19 del diagrama).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar estado de un pedido",
            description = "Devuelve estado, datos generales, detalles, pagos y envío (si corresponde) "
                    + "de un pedido del cliente. Responde 404 si el pedido no existe o no es del cliente.")
    public ResponseEntity<ApiResponse<PedidoEstadoDTO>> obtenerEstadoPedido(
            @Parameter(description = "ID del pedido a consultar", example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "ID del cliente que consulta", example = "1")
            @RequestParam("clienteId") Long clienteId) {

        log.debug("GET /api/pedidos/{} - clienteId: {}", id, clienteId);

        // Paso 15: delega en el service (puede lanzar PedidoNoEncontradoException -> 404)
        PedidoEstadoDTO estadoDTO = pedidoService.obtenerEstadoPedido(id, clienteId);

        // Paso 28: success(200, mensaje, estadoDTO)
        ApiResponse<PedidoEstadoDTO> apiResponse =
                ApiResponse.success(HttpStatus.OK.value(), "Estado del pedido obtenido correctamente", estadoDTO);

        // Paso 30-31: ResponseEntity.status(200).body(apiResponse) -> HTTP 200 + JSON
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
