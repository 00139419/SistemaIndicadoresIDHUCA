package com.uca.idhuca.sistema.indicadores.useCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.idhuca.sistema.indicadores.controllers.dto.UbicacionDTO;
import com.uca.idhuca.sistema.indicadores.exceptions.ValidationException;
import com.uca.idhuca.sistema.indicadores.models.Catalogo;
import com.uca.idhuca.sistema.indicadores.models.Ubicacion;
import com.uca.idhuca.sistema.indicadores.repositories.CatalogoRepository;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.ERROR;

@Component
public class EventosUseCase {
	
	@Autowired
	CatalogoRepository catalogoRepository;
	
	@Autowired
	ObjectMapper mapper;
	
	public Ubicacion mapearUbicacionDesdeDto(UbicacionDTO dto) throws ValidationException {
        Ubicacion ubicacion = new Ubicacion();

        if (dto.getDepartamento() != null && dto.getDepartamento().getCodigo() != null) {
            Catalogo departamento = catalogoRepository.findByCodigo(dto.getDepartamento().getCodigo());
            if (departamento == null) {
                throw new ValidationException(ERROR, "No se encontró el catálogo con código de departamento: " + dto.getDepartamento().getCodigo());
            }
            ubicacion.setDepartamento(departamento);
        }

        if (dto.getMunicipio() != null && dto.getMunicipio().getCodigo() != null) {
            Catalogo municipio = catalogoRepository.findByCodigo(dto.getMunicipio().getCodigo());
            if (municipio == null) {
                throw new ValidationException(ERROR, "No se encontró el catálogo con código de municipio: " + dto.getMunicipio().getCodigo());
            }
            ubicacion.setMunicipio(municipio);
        }

        if (dto.getLugarExacto() != null && dto.getLugarExacto().getCodigo() != null) {
            Catalogo lugarExacto = catalogoRepository.findByCodigo(dto.getLugarExacto().getCodigo());
            if (lugarExacto == null) {
                throw new ValidationException(ERROR, "No se encontró el catálogo con código de lugar exacto: " + dto.getLugarExacto().getCodigo());
            }
            ubicacion.setLugarExacto(lugarExacto);
        }

        return ubicacion;
    }

}
