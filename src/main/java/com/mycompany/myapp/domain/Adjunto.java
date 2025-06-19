package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Adjunto.
 */
@Table("adjunto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Adjunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nombre_archivo")
    private String nombreArchivo;

    @Column("archivo")
    private byte[] archivo;

    @NotNull
    @Column("archivo_content_type")
    private String archivoContentType;

    @Column("tipo_mime")
    private String tipoMime;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "comentarios", "adjuntos", "etiquetas", "autor" }, allowSetters = true)
    private Publicacion publicacion;

    @Column("publicacion_id")
    private Long publicacionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Adjunto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return this.nombreArchivo;
    }

    public Adjunto nombreArchivo(String nombreArchivo) {
        this.setNombreArchivo(nombreArchivo);
        return this;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public byte[] getArchivo() {
        return this.archivo;
    }

    public Adjunto archivo(byte[] archivo) {
        this.setArchivo(archivo);
        return this;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }

    public String getArchivoContentType() {
        return this.archivoContentType;
    }

    public Adjunto archivoContentType(String archivoContentType) {
        this.archivoContentType = archivoContentType;
        return this;
    }

    public void setArchivoContentType(String archivoContentType) {
        this.archivoContentType = archivoContentType;
    }

    public String getTipoMime() {
        return this.tipoMime;
    }

    public Adjunto tipoMime(String tipoMime) {
        this.setTipoMime(tipoMime);
        return this;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Publicacion getPublicacion() {
        return this.publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
        this.publicacionId = publicacion != null ? publicacion.getId() : null;
    }

    public Adjunto publicacion(Publicacion publicacion) {
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
        if (!(o instanceof Adjunto)) {
            return false;
        }
        return getId() != null && getId().equals(((Adjunto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Adjunto{" +
            "id=" + getId() +
            ", nombreArchivo='" + getNombreArchivo() + "'" +
            ", archivo='" + getArchivo() + "'" +
            ", archivoContentType='" + getArchivoContentType() + "'" +
            ", tipoMime='" + getTipoMime() + "'" +
            "}";
    }
}
