package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AdjuntoTestSamples.*;
import static com.mycompany.myapp.domain.AutorTestSamples.*;
import static com.mycompany.myapp.domain.ComentarioTestSamples.*;
import static com.mycompany.myapp.domain.EtiquetaTestSamples.*;
import static com.mycompany.myapp.domain.PublicacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PublicacionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Publicacion.class);
        Publicacion publicacion1 = getPublicacionSample1();
        Publicacion publicacion2 = new Publicacion();
        assertThat(publicacion1).isNotEqualTo(publicacion2);

        publicacion2.setId(publicacion1.getId());
        assertThat(publicacion1).isEqualTo(publicacion2);

        publicacion2 = getPublicacionSample2();
        assertThat(publicacion1).isNotEqualTo(publicacion2);
    }

    @Test
    void comentarioTest() {
        Publicacion publicacion = getPublicacionRandomSampleGenerator();
        Comentario comentarioBack = getComentarioRandomSampleGenerator();

        publicacion.addComentario(comentarioBack);
        assertThat(publicacion.getComentarios()).containsOnly(comentarioBack);
        assertThat(comentarioBack.getPublicacion()).isEqualTo(publicacion);

        publicacion.removeComentario(comentarioBack);
        assertThat(publicacion.getComentarios()).doesNotContain(comentarioBack);
        assertThat(comentarioBack.getPublicacion()).isNull();

        publicacion.comentarios(new HashSet<>(Set.of(comentarioBack)));
        assertThat(publicacion.getComentarios()).containsOnly(comentarioBack);
        assertThat(comentarioBack.getPublicacion()).isEqualTo(publicacion);

        publicacion.setComentarios(new HashSet<>());
        assertThat(publicacion.getComentarios()).doesNotContain(comentarioBack);
        assertThat(comentarioBack.getPublicacion()).isNull();
    }

    @Test
    void adjuntoTest() {
        Publicacion publicacion = getPublicacionRandomSampleGenerator();
        Adjunto adjuntoBack = getAdjuntoRandomSampleGenerator();

        publicacion.addAdjunto(adjuntoBack);
        assertThat(publicacion.getAdjuntos()).containsOnly(adjuntoBack);
        assertThat(adjuntoBack.getPublicacion()).isEqualTo(publicacion);

        publicacion.removeAdjunto(adjuntoBack);
        assertThat(publicacion.getAdjuntos()).doesNotContain(adjuntoBack);
        assertThat(adjuntoBack.getPublicacion()).isNull();

        publicacion.adjuntos(new HashSet<>(Set.of(adjuntoBack)));
        assertThat(publicacion.getAdjuntos()).containsOnly(adjuntoBack);
        assertThat(adjuntoBack.getPublicacion()).isEqualTo(publicacion);

        publicacion.setAdjuntos(new HashSet<>());
        assertThat(publicacion.getAdjuntos()).doesNotContain(adjuntoBack);
        assertThat(adjuntoBack.getPublicacion()).isNull();
    }

    @Test
    void etiquetaTest() {
        Publicacion publicacion = getPublicacionRandomSampleGenerator();
        Etiqueta etiquetaBack = getEtiquetaRandomSampleGenerator();

        publicacion.addEtiqueta(etiquetaBack);
        assertThat(publicacion.getEtiquetas()).containsOnly(etiquetaBack);

        publicacion.removeEtiqueta(etiquetaBack);
        assertThat(publicacion.getEtiquetas()).doesNotContain(etiquetaBack);

        publicacion.etiquetas(new HashSet<>(Set.of(etiquetaBack)));
        assertThat(publicacion.getEtiquetas()).containsOnly(etiquetaBack);

        publicacion.setEtiquetas(new HashSet<>());
        assertThat(publicacion.getEtiquetas()).doesNotContain(etiquetaBack);
    }

    @Test
    void autorTest() {
        Publicacion publicacion = getPublicacionRandomSampleGenerator();
        Autor autorBack = getAutorRandomSampleGenerator();

        publicacion.setAutor(autorBack);
        assertThat(publicacion.getAutor()).isEqualTo(autorBack);

        publicacion.autor(null);
        assertThat(publicacion.getAutor()).isNull();
    }
}
