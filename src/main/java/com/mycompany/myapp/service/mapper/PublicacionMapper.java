package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Publicacion;
import com.mycompany.myapp.service.dto.PublicacionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Publicacion} and its DTO {@link PublicacionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PublicacionMapper extends EntityMapper<PublicacionDTO, Publicacion> {}
