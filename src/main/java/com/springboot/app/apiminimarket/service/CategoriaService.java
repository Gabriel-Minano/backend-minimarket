package com.springboot.app.apiminimarket.service;

import com.springboot.app.apiminimarket.dto.CategoriaRequest;
import com.springboot.app.apiminimarket.dto.CategoriaResponse;
import com.springboot.app.apiminimarket.exception.DuplicateResourceException;
import com.springboot.app.apiminimarket.exception.ResourceNotFoundException;
import com.springboot.app.apiminimarket.mapper.CategoriaMapper;
import com.springboot.app.apiminimarket.model.Categoria;
import com.springboot.app.apiminimarket.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarCategorias(boolean activo, Pageable pageable){
        return categoriaRepository.findByActivo(activo, pageable)
                .map(categoriaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id){
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id)
        );
        return categoriaMapper.toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request){
        // Validar que no exista otra categoría con el mismo nombre
        if(categoriaRepository.findByNombre(request.nombre()).isPresent()){
            throw new DuplicateResourceException("El nombre de la categoría ya existe: " + request.nombre());
        }
        Categoria categoria = categoriaMapper.toEntity(request);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizarCategoria(Long id, CategoriaRequest request){
        Categoria categoria = categoriaRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));

        // Verificar que el nombre no pertenezca a OTRA categoría existente
        /*
        * Si el nombre esta presente y si ambas ID no son iguales significa
        * que ocurrirá una duplicación, entonces se debe lanzar una exception
        * */
        categoriaRepository.findByNombre(request.nombre()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("El nombre de la categoría ya existe: " + request.nombre());
            }
        });

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void desactivarCategoria(Long id){
        Categoria categoria = categoriaRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }

    @Transactional
    public CategoriaResponse reactivarCategoria(Long id){
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));
        categoria.setActivo(true);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }
}