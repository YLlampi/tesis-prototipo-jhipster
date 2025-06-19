package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AutorTestSamples.*;
import static com.mycompany.myapp.domain.PublicacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AutorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Autor.class);
        Autor autor1 = getAutorSample1();
        Autor autor2 = new Autor();
        assertThat(autor1).isNotEqualTo(autor2);

        autor2.setId(autor1.getId());
        assertThat(autor1).isEqualTo(autor2);

        autor2 = getAutorSample2();
        assertThat(autor1).isNotEqualTo(autor2);
    }

    @Test
    void publicacionTest() {
        Autor autor = getAutorRandomSampleGenerator();
        Publicacion publicacionBack = getPublicacionRandomSampleGenerator();

        autor.addPublicacion(publicacionBack);
        assertThat(autor.getPublicacions()).containsOnly(publicacionBack);
        assertThat(publicacionBack.getAutor()).isEqualTo(autor);

        autor.removePublicacion(publicacionBack);
        assertThat(autor.getPublicacions()).doesNotContain(publicacionBack);
        assertThat(publicacionBack.getAutor()).isNull();

        autor.publicacions(new HashSet<>(Set.of(publicacionBack)));
        assertThat(autor.getPublicacions()).containsOnly(publicacionBack);
        assertThat(publicacionBack.getAutor()).isEqualTo(autor);

        autor.setPublicacions(new HashSet<>());
        assertThat(autor.getPublicacions()).doesNotContain(publicacionBack);
        assertThat(publicacionBack.getAutor()).isNull();
    }
}
