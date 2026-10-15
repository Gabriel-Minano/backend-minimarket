package com.springboot.app.apiminimarket.service;

import com.springboot.app.apiminimarket.dto.DetalleVentaRequest;
import com.springboot.app.apiminimarket.dto.VentaRequest;
import com.springboot.app.apiminimarket.dto.VentaResponse;
import com.springboot.app.apiminimarket.exception.ResourceNotFoundException;
import com.springboot.app.apiminimarket.mapper.VentaMapper;
import com.springboot.app.apiminimarket.model.DetalleVenta;
import com.springboot.app.apiminimarket.model.Producto;
import com.springboot.app.apiminimarket.model.Usuario;
import com.springboot.app.apiminimarket.model.Venta;
import com.springboot.app.apiminimarket.repository.ProductoRepository;
import com.springboot.app.apiminimarket.repository.UsuarioRepository;
import com.springboot.app.apiminimarket.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VentaService {
    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final VentaMapper ventaMapper;

    public VentaService(VentaRepository ventaRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository, VentaMapper ventaMapper) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.ventaMapper = ventaMapper;
    }

    //Paginar ventas por estado
    @Transactional(readOnly = true)
    public Page<VentaResponse> listarVentas(boolean activo, Pageable pageable) {
        return ventaRepository.findByActivo(activo, pageable)
                .map(ventaMapper::toResponse);
    }

    //Obtener venta por ID
    @Transactional(readOnly = true)
    public VentaResponse obtenerPorId(Long id) {
        Venta venta = ventaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con el id: " + id));
        return ventaMapper.toResponse(venta);
    }

    //Registro de venta
    @Transactional
    public VentaResponse registrarVenta(VentaRequest request) {
        //Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + request.idUsuario()));

        //Inicializar venta
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setActivo(true);

        BigDecimal totalVenta = BigDecimal.ZERO;

        //Iterar sobre los items del carrito
        for (DetalleVentaRequest detalleReq : request.detalles()) {
            Producto producto = productoRepository.findById(detalleReq.idProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + detalleReq.idProducto()));

            //Validación de stock
            if (producto.getStock() < detalleReq.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre() +
                        " (Stock actual: " + producto.getStock() + ", Solicitado: " + detalleReq.cantidad() + ")");
            }

            //Descontar stock al producto
            producto.setStock(producto.getStock() - detalleReq.cantidad());
            productoRepository.save(producto);

            //Calcular subtotales usando el precio actual del producto
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalleReq.cantidad()));

            //Crear el detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleReq.cantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);

            //Vinculación utilizando el HELPER
            venta.addDetalle(detalle);

            //Agregar subtotal al total general de la venta
            totalVenta = totalVenta.add(subtotal);
        }

        //Asignar el total a la venta
        venta.setTotal(totalVenta);

        //Guardar la venta CascadeType.ALL guarda tmb los detalles
        Venta ventaGuardada = ventaRepository.save(venta);

        return ventaMapper.toResponse(ventaGuardada);
    }

    //Anular venta
    @Transactional
    public void anularVenta(Long id) {
        Venta venta = ventaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con el id: " + id));

        //Verificar que la venta se encuentre anulada
        if (!venta.isActivo()) {
            throw new IllegalStateException("La venta ya se encuentra anulada previamente.");
        }

        //Revertir el stock
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }

        //Marcar como inactiva la venta
        venta.setActivo(false);
        ventaRepository.save(venta);
    }
}