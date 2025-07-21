package com.uca.idhuca.sistema.indicadores.services.impl;

import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.repositories.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no encontrado: " + email));

        log.info("usuario.getRol().getCodigo() " + usuario.getRol().getCodigo());
        
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getContrasenaHash(),
                usuario.isActivo(), true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol().getCodigo()))
        );
    }
}
