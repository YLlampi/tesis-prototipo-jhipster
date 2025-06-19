package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Comentario;
import com.mycompany.myapp.domain.Publicacion;
import com.mycompany.myapp.service.dto.ComentarioDTO;
import com.mycompany.myapp.service.dto.PublicacionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comentario} and its DTO {@link ComentarioDTO}.
 */
@Mapper(componentModel = "spring")
public interface ComentarioMapper extends EntityMapper<ComentarioDTO, Comentario> {
    @Mapping(target = "publicacion", source = "publicacion", qualifiedByName = "publicacionId")
    ComentarioDTO toDto(Comentario s);

    @Named("publicacionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PublicacionDTO toDtoPublicacionId(Publicacion publicacion);
}
