package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class AdjuntoAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAdjuntoAllPropertiesEquals(Adjunto expected, Adjunto actual) {
        assertAdjuntoAutoGeneratedPropertiesEquals(expected, actual);
        assertAdjuntoAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAdjuntoAllUpdatablePropertiesEquals(Adjunto expected, Adjunto actual) {
        assertAdjuntoUpdatableFieldsEquals(expected, actual);
        assertAdjuntoUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAdjuntoAutoGeneratedPropertiesEquals(Adjunto expected, Adjunto actual) {
        assertThat(actual)
            .as("Verify Adjunto auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAdjuntoUpdatableFieldsEquals(Adjunto expected, Adjunto actual) {
        assertThat(actual)
            .as("Verify Adjunto relevant properties")
            .satisfies(a -> assertThat(a.getNombreArchivo()).as("check nombreArchivo").isEqualTo(expected.getNombreArchivo()))
            .satisfies(a -> assertThat(a.getArchivo()).as("check archivo").isEqualTo(expected.getArchivo()))
            .satisfies(a ->
                assertThat(a.getArchivoContentType()).as("check archivo contenty type").isEqualTo(expected.getArchivoContentType())
            )
            .satisfies(a -> assertThat(a.getTipoMime()).as("check tipoMime").isEqualTo(expected.getTipoMime()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAdjuntoUpdatableRelationshipsEquals(Adjunto expected, Adjunto actual) {
        assertThat(actual)
            .as("Verify Adjunto relationships")
            .satisfies(a -> assertThat(a.getPublicacion()).as("check publicacion").isEqualTo(expected.getPublicacion()));
    }
}
