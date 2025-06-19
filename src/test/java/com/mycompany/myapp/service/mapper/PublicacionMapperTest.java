package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PublicacionAsserts.*;
import static com.mycompany.myapp.domain.PublicacionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PublicacionMapperTest {

    private PublicacionMapper publicacionMapper;

    @BeforeEach
    void setUp() {
        publicacionMapper = new PublicacionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPublicacionSample1();
        var actual = publicacionMapper.toEntity(publicacionMapper.toDto(expected));
        assertPublicacionAllPropertiesEquals(expected, actual);
    }
}
