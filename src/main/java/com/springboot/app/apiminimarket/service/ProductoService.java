package com.springboot.app.apiminimarket.service;

import com.springboot.app.apiminimarket.dto.ProductoRequest;
import com.springboot.app.apiminimarket.dto.ProductoResponse;
import com.springboot.app.apiminimarket.exception.DuplicateResourceException;
import com.springboot.app.apiminimarket.exception.ResourceNotFoundException;
import com.springboot.app.apiminimarket.mapper.ProductoMapper;
import com.springboot.app.apiminimarket.model.Categoria;
import com.springboot.app.apiminimarket.model.Producto;
import com.springboot.app.apiminimarket.repository.CategoriaRepository;
import com.springboot.app.apiminimarket.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
    }

    //Listar productos por estado
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductos(boolean activo, Pageable pageable){
        return productoRepository.findByActivo(activo, pageable)
                .map(productoMapper::toResponse);
    }

    //Listar productos por categoria y estado
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarPorCategoria(Long categoriaId, boolean activo, Pageable pageable) {
        //Validar que la categoría exista antes de buscar sus productos
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoría no encontrada con el id: " + categoriaId);
        }
        return productoRepository.findByCategoriaIdAndActivo(categoriaId, activo, pageable)
                .map(productoMapper::toResponse);
    }

    //Listar productos por nombre y estaado
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscarPorNombre(String nombre, boolean activo, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivo(nombre, activo, pageable)
                .map(productoMapper::toResponse);
    }

    //Obtener producto por ID junto a su categoría
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findByIdWithCategoria(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));
        return productoMapper.toResponse(producto);
    }

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {
        //Validar que no exista un producto con exactamente el mismo nombre
        if (productoRepository.findByNombre(request.nombre()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un producto registrado con el nombre: " + request.nombre());
        }

        //Validar que la categoría asociada exista en la base de datos
        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + request.idCategoria()));

        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(categoria);
        producto.setActivo(true);

        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toResponse(productoGuardado);
    }

    @Transactional
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request) {
        //Verificar que el producto exista
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));

        //Verificar que el nombre no pertenezca a OTRO producto existente
        productoRepository.findByNombre(request.nombre()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Ya existe otro producto con el nombre: " + request.nombre());
            }
        });

        //Verificar que la nueva categoría exista
        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + request.idCategoria()));

        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(categoria);

        Producto productoActualizado = productoRepository.save(producto);
        return productoMapper.toResponse(productoActualizado);
    }

    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));

        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
