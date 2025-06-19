package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EtiquetaTestSamples.*;
import static com.mycompany.myapp.domain.PublicacionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EtiquetaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Etiqueta.class);
        Etiqueta etiqueta1 = getEtiquetaSample1();
        Etiqueta etiqueta2 = new Etiqueta();
        assertThat(etiqueta1).isNotEqualTo(etiqueta2);

        etiqueta2.setId(etiqueta1.getId());
        assertThat(etiqueta1).isEqualTo(etiqueta2);

        etiqueta2 = getEtiquetaSample2();
        assertThat(etiqueta1).isNotEqualTo(etiqueta2);
    }

    @Test
    void publicacionTest() {
        Etiqueta etiqueta = getEtiquetaRandomSampleGenerator();
        Publicacion publicacionBack = getPublicacionRandomSampleGenerator();

        etiqueta.addPublicacion(publicacionBack);
        assertThat(etiqueta.getPublicacions()).containsOnly(publicacionBack);
        assertThat(publicacionBack.getEtiquetas()).containsOnly(etiqueta);

        etiqueta.removePublicacion(publicacionBack);
        assertThat(etiqueta.getPublicacions()).doesNotContain(publicacionBack);
        assertThat(publicacionBack.getEtiquetas()).doesNotContain(etiqueta);

        etiqueta.publicacions(new HashSet<>(Set.of(publicacionBack)));
        assertThat(etiqueta.getPublicacions()).containsOnly(publicacionBack);
        assertThat(publicacionBack.getEtiquetas()).containsOnly(etiqueta);

        etiqueta.setPublicacions(new HashSet<>());
        assertThat(etiqueta.getPublicacions()).doesNotContain(publicacionBack);
        assertThat(publicacionBack.getEtiquetas()).doesNotContain(etiqueta);
    }
}
