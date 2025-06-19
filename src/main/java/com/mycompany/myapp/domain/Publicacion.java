package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Modelo de Dominio para una aplicación de Blog (Versión Simplificada y Corregida).
 * Se enfoca en las entidades y relaciones para asegurar la generación.
 */
@Schema(
    description = "Modelo de Dominio para una aplicación de Blog (Versión Simplificada y Corregida).\nSe enfoca en las entidades y relaciones para asegurar la generación."
)
@Table("publicacion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Publicacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Column("titulo")
    private String titulo;

    @Column("contenido")
    private String contenido;

    @Column("fecha_publicacion")
    private Instant fechaPublicacion;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "publicacion" }, allowSetters = true)
    private Set<Comentario> comentarios = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "publicacion" }, allowSetters = true)
    private Set<Adjunto> adjuntos = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "publicacions" }, allowSetters = true)
    private Set<Etiqueta> etiquetas = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "publicacions" }, allowSetters = true)
    private Autor autor;

    @Column("autor_id")
    private Long autorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Publicacion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public Publicacion titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return this.contenido;
    }

    public Publicacion contenido(String contenido) {
        this.setContenido(contenido);
        return this;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Instant getFechaPublicacion() {
        return this.fechaPublicacion;
    }

    public Publicacion fechaPublicacion(Instant fechaPublicacion) {
        this.setFechaPublicacion(fechaPublicacion);
        return this;
    }

    public void setFechaPublicacion(Instant fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Set<Comentario> getComentarios() {
        return this.comentarios;
    }

    public void setComentarios(Set<Comentario> comentarios) {
        if (this.comentarios != null) {
            this.comentarios.forEach(i -> i.setPublicacion(null));
        }
        if (comentarios != null) {
            comentarios.forEach(i -> i.setPublicacion(this));
        }
        this.comentarios = comentarios;
    }

    public Publicacion comentarios(Set<Comentario> comentarios) {
        this.setComentarios(comentarios);
        return this;
    }

    public Publicacion addComentario(Comentario comentario) {
        this.comentarios.add(comentario);
        comentario.setPublicacion(this);
        return this;
    }

    public Publicacion removeComentario(Comentario comentario) {
        this.comentarios.remove(comentario);
        comentario.setPublicacion(null);
        return this;
    }

    public Set<Adjunto> getAdjuntos() {
        return this.adjuntos;
    }

    public void setAdjuntos(Set<Adjunto> adjuntos) {
        if (this.adjuntos != null) {
            this.adjuntos.forEach(i -> i.setPublicacion(null));
        }
        if (adjuntos != null) {
            adjuntos.forEach(i -> i.setPublicacion(this));
        }
        this.adjuntos = adjuntos;
    }

    public Publicacion adjuntos(Set<Adjunto> adjuntos) {
        this.setAdjuntos(adjuntos);
        return this;
    }

    public Publicacion addAdjunto(Adjunto adjunto) {
        this.adjuntos.add(adjunto);
        adjunto.setPublicacion(this);
        return this;
    }

    public Publicacion removeAdjunto(Adjunto adjunto) {
        this.adjuntos.remove(adjunto);
        adjunto.setPublicacion(null);
        return this;
    }

    public Set<Etiqueta> getEtiquetas() {
        return this.etiquetas;
    }

    public void setEtiquetas(Set<Etiqueta> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public Publicacion etiquetas(Set<Etiqueta> etiquetas) {
        this.setEtiquetas(etiquetas);
        return this;
    }

    public Publicacion addEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.add(etiqueta);
        return this;
    }

    public Publicacion removeEtiqueta(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
        return this;
    }

    public Autor getAutor() {
        return this.autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
        this.autorId = autor != null ? autor.getId() : null;
    }

    public Publicacion autor(Autor autor) {
        this.setAutor(autor);
        return this;
    }

    public Long getAutorId() {
        return this.autorId;
    }

    public void setAutorId(Long autor) {
        this.autorId = autor;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Publicacion)) {
            return false;
        }
        return getId() != null && getId().equals(((Publicacion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Publicacion{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", contenido='" + getContenido() + "'" +
            ", fechaPublicacion='" + getFechaPublicacion() + "'" +
            "}";
    }
}
