package com.springboot.app.apiminimarket.service;

import com.springboot.app.apiminimarket.dto.UsuarioRequest;
import com.springboot.app.apiminimarket.dto.UsuarioResponse;
import com.springboot.app.apiminimarket.dto.UsuarioUpdateRequest;
import com.springboot.app.apiminimarket.exception.DuplicateResourceException;
import com.springboot.app.apiminimarket.exception.ResourceNotFoundException;
import com.springboot.app.apiminimarket.mapper.UsuarioMapper;
import com.springboot.app.apiminimarket.model.Rol;
import com.springboot.app.apiminimarket.model.Usuario;
import com.springboot.app.apiminimarket.repository.RolRepository;
import com.springboot.app.apiminimarket.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuarios(boolean activo, Pageable pageable) {
        return usuarioRepository.findByActivoWithQuery(activo, pageable)
                .map(usuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        //Validar que el correo no esté registrado
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateResourceException("El correo electrónico ya se encuentra registrado: " + request.email());
        }

        //Buscar y validar los roles en la base de datos
        List<Rol> rolesEncontrados = rolRepository.findAllById(request.idRoles());
        if (rolesEncontrados.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron los roles especificados.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        //Encriptación
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRoles(new HashSet<>(rolesEncontrados));
        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuarioGuardado);
    }

    @Transactional
    public UsuarioResponse actualizarUsuario(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));

        if (request.nombre() != null) usuario.setNombre(request.nombre());
        if (request.apellido() != null) usuario.setApellido(request.apellido());

        //Validar que el correo no pertenezca a otra persona
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioRepository.findByEmail(request.email()).isPresent()) {
                throw new DuplicateResourceException("El correo electrónico ya está en uso por otro usuario.");
            }
            usuario.setEmail(request.email());
        }

        //Encriptar nueva contraseña
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        //Si hay nuevos roles actualizar
        if (request.idRoles() != null && !request.idRoles().isEmpty()) {
            List<Rol> nuevosRoles = rolRepository.findAllById(request.idRoles());
            usuario.setRoles(new HashSet<>(nuevosRoles));
        }

        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuarioActualizado);
    }

    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el id: " + id));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
