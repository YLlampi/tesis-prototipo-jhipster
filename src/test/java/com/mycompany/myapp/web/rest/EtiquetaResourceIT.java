package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EtiquetaAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Etiqueta;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.EtiquetaRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link EtiquetaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EtiquetaResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/etiquetas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Etiqueta etiqueta;

    private Etiqueta insertedEtiqueta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etiqueta createEntity() {
        return new Etiqueta().nombre(DEFAULT_NOMBRE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etiqueta createUpdatedEntity() {
        return new Etiqueta().nombre(UPDATED_NOMBRE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Etiqueta.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        etiqueta = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEtiqueta != null) {
            etiquetaRepository.delete(insertedEtiqueta).block();
            insertedEtiqueta = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEtiqueta() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Etiqueta
        var returnedEtiqueta = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Etiqueta.class)
            .returnResult()
            .getResponseBody();

        // Validate the Etiqueta in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEtiquetaUpdatableFieldsEquals(returnedEtiqueta, getPersistedEtiqueta(returnedEtiqueta));

        insertedEtiqueta = returnedEtiqueta;
    }

    @Test
    void createEtiquetaWithExistingId() throws Exception {
        // Create the Etiqueta with an existing ID
        etiqueta.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etiqueta.setNombre(null);

        // Create the Etiqueta, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEtiquetasAsStream() {
        // Initialize the database
        etiquetaRepository.save(etiqueta).block();

        List<Etiqueta> etiquetaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Etiqueta.class)
            .getResponseBody()
            .filter(etiqueta::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(etiquetaList).isNotNull();
        assertThat(etiquetaList).hasSize(1);
        Etiqueta testEtiqueta = etiquetaList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertEtiquetaAllPropertiesEquals(etiqueta, testEtiqueta);
        assertEtiquetaUpdatableFieldsEquals(etiqueta, testEtiqueta);
    }

    @Test
    void getAllEtiquetas() {
        // Initialize the database
        insertedEtiqueta = etiquetaRepository.save(etiqueta).block();

        // Get all the etiquetaList
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
            .value(hasItem(etiqueta.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE));
    }

    @Test
    void getEtiqueta() {
        // Initialize the database
        insertedEtiqueta = etiquetaRepository.save(etiqueta).block();

        // Get the etiqueta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, etiqueta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(etiqueta.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE));
    }

    @Test
    void getNonExistingEtiqueta() {
        // Get the etiqueta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEtiqueta() throws Exception {
        // Initialize the database
        insertedEtiqueta = etiquetaRepository.save(etiqueta).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiqueta
        Etiqueta updatedEtiqueta = etiquetaRepository.findById(etiqueta.getId()).block();
        updatedEtiqueta.nombre(UPDATED_NOMBRE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEtiqueta.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedEtiqueta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEtiquetaToMatchAllProperties(updatedEtiqueta);
    }

    @Test
    void putNonExistingEtiqueta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiqueta.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, etiqueta.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEtiqueta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiqueta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEtiqueta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiqueta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEtiquetaWithPatch() throws Exception {
        // Initialize the database
        insertedEtiqueta = etiquetaRepository.save(etiqueta).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiqueta using partial update
        Etiqueta partialUpdatedEtiqueta = new Etiqueta();
        partialUpdatedEtiqueta.setId(etiqueta.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEtiqueta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEtiqueta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Etiqueta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtiquetaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEtiqueta, etiqueta), getPersistedEtiqueta(etiqueta));
    }

    @Test
    void fullUpdateEtiquetaWithPatch() throws Exception {
        // Initialize the database
        insertedEtiqueta = etiquetaRepository.save(etiqueta).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiqueta using partial update
        Etiqueta partialUpdatedEtiqueta = new Etiqueta();
        partialUpdatedEtiqueta.setId(etiqueta.getId());

        partialUpdatedEtiqueta.nombre(UPDATED_NOMBRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEtiqueta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEtiqueta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Etiqueta in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtiquetaUpdatableFieldsEquals(partialUpdatedEtiqueta, getPersistedEtiqueta(partialUpdatedEtiqueta));
    }

    @Test
    void patchNonExistingEtiqueta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiqueta.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, etiqueta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEtiqueta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiqueta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEtiqueta() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiqueta.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(etiqueta))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Etiqueta in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEtiqueta() {
        // Initialize the database
        insertedEtiqueta = etiquetaRepository.save(etiqueta).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the etiqueta
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, etiqueta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return etiquetaRepository.count().block();
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

    protected Etiqueta getPersistedEtiqueta(Etiqueta etiqueta) {
        return etiquetaRepository.findById(etiqueta.getId()).block();
    }

    protected void assertPersistedEtiquetaToMatchAllProperties(Etiqueta expectedEtiqueta) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEtiquetaAllPropertiesEquals(expectedEtiqueta, getPersistedEtiqueta(expectedEtiqueta));
        assertEtiquetaUpdatableFieldsEquals(expectedEtiqueta, getPersistedEtiqueta(expectedEtiqueta));
    }

    protected void assertPersistedEtiquetaToMatchUpdatableProperties(Etiqueta expectedEtiqueta) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEtiquetaAllUpdatablePropertiesEquals(expectedEtiqueta, getPersistedEtiqueta(expectedEtiqueta));
        assertEtiquetaUpdatableFieldsEquals(expectedEtiqueta, getPersistedEtiqueta(expectedEtiqueta));
    }
}
