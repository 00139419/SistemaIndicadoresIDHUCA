package com.uca.idhuca.sistema.indicadores.repositories;

import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.RegistroEvento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroEventoRepository extends JpaRepository<RegistroEvento, Long> {
    
    List<RegistroEvento> findByDerechoAsociadoCodigo(Catalogo derechoAsociado);
}
