package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PublicacionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicacionDTO.class);
        PublicacionDTO publicacionDTO1 = new PublicacionDTO();
        publicacionDTO1.setId(1L);
        PublicacionDTO publicacionDTO2 = new PublicacionDTO();
        assertThat(publicacionDTO1).isNotEqualTo(publicacionDTO2);
        publicacionDTO2.setId(publicacionDTO1.getId());
        assertThat(publicacionDTO1).isEqualTo(publicacionDTO2);
        publicacionDTO2.setId(2L);
        assertThat(publicacionDTO1).isNotEqualTo(publicacionDTO2);
        publicacionDTO1.setId(null);
        assertThat(publicacionDTO1).isNotEqualTo(publicacionDTO2);
    }
}
