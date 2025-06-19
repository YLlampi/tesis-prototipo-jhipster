package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AdjuntoAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Adjunto;
import com.mycompany.myapp.repository.AdjuntoRepository;
import com.mycompany.myapp.repository.EntityManager;
import java.time.Duration;
import java.util.Base64;
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
 * Integration tests for the {@link AdjuntoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AdjuntoResourceIT {

    private static final String DEFAULT_NOMBRE_ARCHIVO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_ARCHIVO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_ARCHIVO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ARCHIVO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_ARCHIVO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ARCHIVO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_TIPO_MIME = "AAAAAAAAAA";
    private static final String UPDATED_TIPO_MIME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/adjuntos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AdjuntoRepository adjuntoRepository;

    @Mock
    private AdjuntoRepository adjuntoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Adjunto adjunto;

    private Adjunto insertedAdjunto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adjunto createEntity() {
        return new Adjunto()
            .nombreArchivo(DEFAULT_NOMBRE_ARCHIVO)
            .archivo(DEFAULT_ARCHIVO)
            .archivoContentType(DEFAULT_ARCHIVO_CONTENT_TYPE)
            .tipoMime(DEFAULT_TIPO_MIME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adjunto createUpdatedEntity() {
        return new Adjunto()
            .nombreArchivo(UPDATED_NOMBRE_ARCHIVO)
            .archivo(UPDATED_ARCHIVO)
            .archivoContentType(UPDATED_ARCHIVO_CONTENT_TYPE)
            .tipoMime(UPDATED_TIPO_MIME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Adjunto.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        adjunto = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAdjunto != null) {
            adjuntoRepository.delete(insertedAdjunto).block();
            insertedAdjunto = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAdjunto() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Adjunto
        var returnedAdjunto = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Adjunto.class)
            .returnResult()
            .getResponseBody();

        // Validate the Adjunto in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAdjuntoUpdatableFieldsEquals(returnedAdjunto, getPersistedAdjunto(returnedAdjunto));

        insertedAdjunto = returnedAdjunto;
    }

    @Test
    void createAdjuntoWithExistingId() throws Exception {
        // Create the Adjunto with an existing ID
        adjunto.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreArchivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        adjunto.setNombreArchivo(null);

        // Create the Adjunto, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAdjuntosAsStream() {
        // Initialize the database
        adjuntoRepository.save(adjunto).block();

        List<Adjunto> adjuntoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Adjunto.class)
            .getResponseBody()
            .filter(adjunto::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(adjuntoList).isNotNull();
        assertThat(adjuntoList).hasSize(1);
        Adjunto testAdjunto = adjuntoList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertAdjuntoAllPropertiesEquals(adjunto, testAdjunto);
        assertAdjuntoUpdatableFieldsEquals(adjunto, testAdjunto);
    }

    @Test
    void getAllAdjuntos() {
        // Initialize the database
        insertedAdjunto = adjuntoRepository.save(adjunto).block();

        // Get all the adjuntoList
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
            .value(hasItem(adjunto.getId().intValue()))
            .jsonPath("$.[*].nombreArchivo")
            .value(hasItem(DEFAULT_NOMBRE_ARCHIVO))
            .jsonPath("$.[*].archivoContentType")
            .value(hasItem(DEFAULT_ARCHIVO_CONTENT_TYPE))
            .jsonPath("$.[*].archivo")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_ARCHIVO)))
            .jsonPath("$.[*].tipoMime")
            .value(hasItem(DEFAULT_TIPO_MIME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAdjuntosWithEagerRelationshipsIsEnabled() {
        when(adjuntoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(adjuntoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAdjuntosWithEagerRelationshipsIsNotEnabled() {
        when(adjuntoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(adjuntoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getAdjunto() {
        // Initialize the database
        insertedAdjunto = adjuntoRepository.save(adjunto).block();

        // Get the adjunto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, adjunto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(adjunto.getId().intValue()))
            .jsonPath("$.nombreArchivo")
            .value(is(DEFAULT_NOMBRE_ARCHIVO))
            .jsonPath("$.archivoContentType")
            .value(is(DEFAULT_ARCHIVO_CONTENT_TYPE))
            .jsonPath("$.archivo")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_ARCHIVO)))
            .jsonPath("$.tipoMime")
            .value(is(DEFAULT_TIPO_MIME));
    }

    @Test
    void getNonExistingAdjunto() {
        // Get the adjunto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAdjunto() throws Exception {
        // Initialize the database
        insertedAdjunto = adjuntoRepository.save(adjunto).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the adjunto
        Adjunto updatedAdjunto = adjuntoRepository.findById(adjunto.getId()).block();
        updatedAdjunto
            .nombreArchivo(UPDATED_NOMBRE_ARCHIVO)
            .archivo(UPDATED_ARCHIVO)
            .archivoContentType(UPDATED_ARCHIVO_CONTENT_TYPE)
            .tipoMime(UPDATED_TIPO_MIME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAdjunto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedAdjunto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAdjuntoToMatchAllProperties(updatedAdjunto);
    }

    @Test
    void putNonExistingAdjunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adjunto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, adjunto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAdjunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adjunto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAdjunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adjunto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAdjuntoWithPatch() throws Exception {
        // Initialize the database
        insertedAdjunto = adjuntoRepository.save(adjunto).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the adjunto using partial update
        Adjunto partialUpdatedAdjunto = new Adjunto();
        partialUpdatedAdjunto.setId(adjunto.getId());

        partialUpdatedAdjunto.archivo(UPDATED_ARCHIVO).archivoContentType(UPDATED_ARCHIVO_CONTENT_TYPE).tipoMime(UPDATED_TIPO_MIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdjunto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAdjunto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Adjunto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdjuntoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAdjunto, adjunto), getPersistedAdjunto(adjunto));
    }

    @Test
    void fullUpdateAdjuntoWithPatch() throws Exception {
        // Initialize the database
        insertedAdjunto = adjuntoRepository.save(adjunto).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the adjunto using partial update
        Adjunto partialUpdatedAdjunto = new Adjunto();
        partialUpdatedAdjunto.setId(adjunto.getId());

        partialUpdatedAdjunto
            .nombreArchivo(UPDATED_NOMBRE_ARCHIVO)
            .archivo(UPDATED_ARCHIVO)
            .archivoContentType(UPDATED_ARCHIVO_CONTENT_TYPE)
            .tipoMime(UPDATED_TIPO_MIME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAdjunto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAdjunto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Adjunto in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAdjuntoUpdatableFieldsEquals(partialUpdatedAdjunto, getPersistedAdjunto(partialUpdatedAdjunto));
    }

    @Test
    void patchNonExistingAdjunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adjunto.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, adjunto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAdjunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adjunto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAdjunto() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        adjunto.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(adjunto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Adjunto in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAdjunto() {
        // Initialize the database
        insertedAdjunto = adjuntoRepository.save(adjunto).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the adjunto
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, adjunto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return adjuntoRepository.count().block();
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

    protected Adjunto getPersistedAdjunto(Adjunto adjunto) {
        return adjuntoRepository.findById(adjunto.getId()).block();
    }

    protected void assertPersistedAdjuntoToMatchAllProperties(Adjunto expectedAdjunto) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAdjuntoAllPropertiesEquals(expectedAdjunto, getPersistedAdjunto(expectedAdjunto));
        assertAdjuntoUpdatableFieldsEquals(expectedAdjunto, getPersistedAdjunto(expectedAdjunto));
    }

    protected void assertPersistedAdjuntoToMatchUpdatableProperties(Adjunto expectedAdjunto) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAdjuntoAllUpdatablePropertiesEquals(expectedAdjunto, getPersistedAdjunto(expectedAdjunto));
        assertAdjuntoUpdatableFieldsEquals(expectedAdjunto, getPersistedAdjunto(expectedAdjunto));
    }
}
