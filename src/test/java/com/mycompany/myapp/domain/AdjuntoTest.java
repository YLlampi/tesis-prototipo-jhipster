package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AdjuntoTestSamples.*;
import static com.mycompany.myapp.domain.PublicacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdjuntoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Adjunto.class);
        Adjunto adjunto1 = getAdjuntoSample1();
        Adjunto adjunto2 = new Adjunto();
        assertThat(adjunto1).isNotEqualTo(adjunto2);

        adjunto2.setId(adjunto1.getId());
        assertThat(adjunto1).isEqualTo(adjunto2);

        adjunto2 = getAdjuntoSample2();
        assertThat(adjunto1).isNotEqualTo(adjunto2);
    }

    @Test
    void publicacionTest() {
        Adjunto adjunto = getAdjuntoRandomSampleGenerator();
        Publicacion publicacionBack = getPublicacionRandomSampleGenerator();

        adjunto.setPublicacion(publicacionBack);
        assertThat(adjunto.getPublicacion()).isEqualTo(publicacionBack);

        adjunto.publicacion(null);
        assertThat(adjunto.getPublicacion()).isNull();
    }
}
