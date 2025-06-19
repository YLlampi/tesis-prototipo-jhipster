package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Comentario} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ComentarioDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String texto;

    private Instant fechaCreacion;

    private PublicacionDTO publicacion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public PublicacionDTO getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(PublicacionDTO publicacion) {
        this.publicacion = publicacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComentarioDTO)) {
            return false;
        }

        ComentarioDTO comentarioDTO = (ComentarioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, comentarioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComentarioDTO{" +
            "id=" + getId() +
            ", texto='" + getTexto() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", publicacion=" + getPublicacion() +
            "}";
    }
}
