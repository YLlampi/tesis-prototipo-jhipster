package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Comentario.
 */
@Table("comentario")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Comentario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("texto")
    private String texto;

    @Column("fecha_creacion")
    private Instant fechaCreacion;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "comentarios", "adjuntos", "etiquetas", "autor" }, allowSetters = true)
    private Publicacion publicacion;

    @Column("publicacion_id")
    private Long publicacionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Comentario id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return this.texto;
    }

    public Comentario texto(String texto) {
        this.setTexto(texto);
        return this;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public Comentario fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Publicacion getPublicacion() {
        return this.publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
        this.publicacionId = publicacion != null ? publicacion.getId() : null;
    }

    public Comentario publicacion(Publicacion publicacion) {
        this.setPublicacion(publicacion);
        return this;
    }

    public Long getPublicacionId() {
        return this.publicacionId;
    }

    public void setPublicacionId(Long publicacion) {
        this.publicacionId = publicacion;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comentario)) {
            return false;
        }
        return getId() != null && getId().equals(((Comentario) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comentario{" +
            "id=" + getId() +
            ", texto='" + getTexto() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            "}";
    }
}
