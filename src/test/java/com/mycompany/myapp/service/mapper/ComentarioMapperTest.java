package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.ComentarioAsserts.*;
import static com.mycompany.myapp.domain.ComentarioTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComentarioMapperTest {

    private ComentarioMapper comentarioMapper;

    @BeforeEach
    void setUp() {
        comentarioMapper = new ComentarioMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getComentarioSample1();
        var actual = comentarioMapper.toEntity(comentarioMapper.toDto(expected));
        assertComentarioAllPropertiesEquals(expected, actual);
    }
}
