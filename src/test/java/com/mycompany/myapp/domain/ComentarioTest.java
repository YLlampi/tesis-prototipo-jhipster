package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.ComentarioTestSamples.*;
import static com.mycompany.myapp.domain.PublicacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComentarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comentario.class);
        Comentario comentario1 = getComentarioSample1();
        Comentario comentario2 = new Comentario();
        assertThat(comentario1).isNotEqualTo(comentario2);

        comentario2.setId(comentario1.getId());
        assertThat(comentario1).isEqualTo(comentario2);

        comentario2 = getComentarioSample2();
        assertThat(comentario1).isNotEqualTo(comentario2);
    }

    @Test
    void publicacionTest() {
        Comentario comentario = getComentarioRandomSampleGenerator();
        Publicacion publicacionBack = getPublicacionRandomSampleGenerator();

        comentario.setPublicacion(publicacionBack);
        assertThat(comentario.getPublicacion()).isEqualTo(publicacionBack);

        comentario.publicacion(null);
        assertThat(comentario.getPublicacion()).isNull();
    }
}
