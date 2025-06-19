package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Etiqueta.
 */
@Table("etiqueta")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Etiqueta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 2)
    @Column("nombre")
    private String nombre;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "comentarios", "adjuntos", "etiquetas", "autor" }, allowSetters = true)
    private Set<Publicacion> publicacions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Etiqueta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Etiqueta nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Publicacion> getPublicacions() {
        return this.publicacions;
    }

    public void setPublicacions(Set<Publicacion> publicacions) {
        if (this.publicacions != null) {
            this.publicacions.forEach(i -> i.removeEtiqueta(this));
        }
        if (publicacions != null) {
            publicacions.forEach(i -> i.addEtiqueta(this));
        }
        this.publicacions = publicacions;
    }

    public Etiqueta publicacions(Set<Publicacion> publicacions) {
        this.setPublicacions(publicacions);
        return this;
    }

    public Etiqueta addPublicacion(Publicacion publicacion) {
        this.publicacions.add(publicacion);
        publicacion.getEtiquetas().add(this);
        return this;
    }

    public Etiqueta removePublicacion(Publicacion publicacion) {
        this.publicacions.remove(publicacion);
        publicacion.getEtiquetas().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etiqueta)) {
            return false;
        }
        return getId() != null && getId().equals(((Etiqueta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Etiqueta{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            "}";
    }
}
