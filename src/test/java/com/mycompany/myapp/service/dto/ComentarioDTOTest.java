package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ComentarioDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ComentarioDTO.class);
        ComentarioDTO comentarioDTO1 = new ComentarioDTO();
        comentarioDTO1.setId(1L);
        ComentarioDTO comentarioDTO2 = new ComentarioDTO();
        assertThat(comentarioDTO1).isNotEqualTo(comentarioDTO2);
        comentarioDTO2.setId(comentarioDTO1.getId());
        assertThat(comentarioDTO1).isEqualTo(comentarioDTO2);
        comentarioDTO2.setId(2L);
        assertThat(comentarioDTO1).isNotEqualTo(comentarioDTO2);
        comentarioDTO1.setId(null);
        assertThat(comentarioDTO1).isNotEqualTo(comentarioDTO2);
    }
}
