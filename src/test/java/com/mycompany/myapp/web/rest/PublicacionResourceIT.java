package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PublicacionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Publicacion;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.PublicacionRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link PublicacionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PublicacionResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENIDO = "AAAAAAAAAA";
    private static final String UPDATED_CONTENIDO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_PUBLICACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_PUBLICACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/publicacions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Mock
    private PublicacionRepository publicacionRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Publicacion publicacion;

    private Publicacion insertedPublicacion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publicacion createEntity() {
        return new Publicacion().titulo(DEFAULT_TITULO).contenido(DEFAULT_CONTENIDO).fechaPublicacion(DEFAULT_FECHA_PUBLICACION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Publicacion createUpdatedEntity() {
        return new Publicacion().titulo(UPDATED_TITULO).contenido(UPDATED_CONTENIDO).fechaPublicacion(UPDATED_FECHA_PUBLICACION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_publicacion__etiqueta").block();
            em.deleteAll(Publicacion.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        publicacion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPublicacion != null) {
            publicacionRepository.delete(insertedPublicacion).block();
            insertedPublicacion = null;
        }
        deleteEntities(em);
    }

    @Test
    void createPublicacion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Publicacion
        var returnedPublicacion = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Publicacion.class)
            .returnResult()
            .getResponseBody();

        // Validate the Publicacion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPublicacionUpdatableFieldsEquals(returnedPublicacion, getPersistedPublicacion(returnedPublicacion));

        insertedPublicacion = returnedPublicacion;
    }

    @Test
    void createPublicacionWithExistingId() throws Exception {
        // Create the Publicacion with an existing ID
        publicacion.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacion.setTitulo(null);

        // Create the Publicacion, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPublicacionsAsStream() {
        // Initialize the database
        publicacionRepository.save(publicacion).block();

        List<Publicacion> publicacionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Publicacion.class)
            .getResponseBody()
            .filter(publicacion::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(publicacionList).isNotNull();
        assertThat(publicacionList).hasSize(1);
        Publicacion testPublicacion = publicacionList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertPublicacionAllPropertiesEquals(publicacion, testPublicacion);
        assertPublicacionUpdatableFieldsEquals(publicacion, testPublicacion);
    }

    @Test
    void getAllPublicacions() {
        // Initialize the database
        insertedPublicacion = publicacionRepository.save(publicacion).block();

        // Get all the publicacionList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(publicacion.getId().intValue()))
            .jsonPath("$.[*].titulo")
            .value(hasItem(DEFAULT_TITULO))
            .jsonPath("$.[*].contenido")
            .value(hasItem(DEFAULT_CONTENIDO))
            .jsonPath("$.[*].fechaPublicacion")
            .value(hasItem(DEFAULT_FECHA_PUBLICACION.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPublicacionsWithEagerRelationshipsIsEnabled() {
        when(publicacionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(publicacionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPublicacionsWithEagerRelationshipsIsNotEnabled() {
        when(publicacionRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(publicacionRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPublicacion() {
        // Initialize the database
        insertedPublicacion = publicacionRepository.save(publicacion).block();

        // Get the publicacion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, publicacion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(publicacion.getId().intValue()))
            .jsonPath("$.titulo")
            .value(is(DEFAULT_TITULO))
            .jsonPath("$.contenido")
            .value(is(DEFAULT_CONTENIDO))
            .jsonPath("$.fechaPublicacion")
            .value(is(DEFAULT_FECHA_PUBLICACION.toString()));
    }

    @Test
    void getNonExistingPublicacion() {
        // Get the publicacion
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPublicacion() throws Exception {
        // Initialize the database
        insertedPublicacion = publicacionRepository.save(publicacion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacion
        Publicacion updatedPublicacion = publicacionRepository.findById(publicacion.getId()).block();
        updatedPublicacion.titulo(UPDATED_TITULO).contenido(UPDATED_CONTENIDO).fechaPublicacion(UPDATED_FECHA_PUBLICACION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPublicacion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedPublicacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublicacionToMatchAllProperties(updatedPublicacion);
    }

    @Test
    void putNonExistingPublicacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, publicacion.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPublicacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPublicacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePublicacionWithPatch() throws Exception {
        // Initialize the database
        insertedPublicacion = publicacionRepository.save(publicacion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacion using partial update
        Publicacion partialUpdatedPublicacion = new Publicacion();
        partialUpdatedPublicacion.setId(publicacion.getId());

        partialUpdatedPublicacion.contenido(UPDATED_CONTENIDO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPublicacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPublicacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Publicacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicacionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublicacion, publicacion),
            getPersistedPublicacion(publicacion)
        );
    }

    @Test
    void fullUpdatePublicacionWithPatch() throws Exception {
        // Initialize the database
        insertedPublicacion = publicacionRepository.save(publicacion).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacion using partial update
        Publicacion partialUpdatedPublicacion = new Publicacion();
        partialUpdatedPublicacion.setId(publicacion.getId());

        partialUpdatedPublicacion.titulo(UPDATED_TITULO).contenido(UPDATED_CONTENIDO).fechaPublicacion(UPDATED_FECHA_PUBLICACION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPublicacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPublicacion))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Publicacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicacionUpdatableFieldsEquals(partialUpdatedPublicacion, getPersistedPublicacion(partialUpdatedPublicacion));
    }

    @Test
    void patchNonExistingPublicacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacion.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, publicacion.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPublicacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPublicacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacion.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(publicacion))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Publicacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePublicacion() {
        // Initialize the database
        insertedPublicacion = publicacionRepository.save(publicacion).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the publicacion
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, publicacion.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return publicacionRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Publicacion getPersistedPublicacion(Publicacion publicacion) {
        return publicacionRepository.findById(publicacion.getId()).block();
    }

    protected void assertPersistedPublicacionToMatchAllProperties(Publicacion expectedPublicacion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPublicacionAllPropertiesEquals(expectedPublicacion, getPersistedPublicacion(expectedPublicacion));
        assertPublicacionUpdatableFieldsEquals(expectedPublicacion, getPersistedPublicacion(expectedPublicacion));
    }

    protected void assertPersistedPublicacionToMatchUpdatableProperties(Publicacion expectedPublicacion) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPublicacionAllUpdatablePropertiesEquals(expectedPublicacion, getPersistedPublicacion(expectedPublicacion));
        assertPublicacionUpdatableFieldsEquals(expectedPublicacion, getPersistedPublicacion(expectedPublicacion));
    }
}
