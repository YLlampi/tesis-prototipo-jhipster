package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ComentarioAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Comentario;
import com.mycompany.myapp.repository.ComentarioRepository;
import com.mycompany.myapp.repository.EntityManager;
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
 * Integration tests for the {@link ComentarioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ComentarioResourceIT {

    private static final String DEFAULT_TEXTO = "AAAAAAAAAA";
    private static final String UPDATED_TEXTO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/comentarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Mock
    private ComentarioRepository comentarioRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Comentario comentario;

    private Comentario insertedComentario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comentario createEntity() {
        return new Comentario().texto(DEFAULT_TEXTO).fechaCreacion(DEFAULT_FECHA_CREACION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comentario createUpdatedEntity() {
        return new Comentario().texto(UPDATED_TEXTO).fechaCreacion(UPDATED_FECHA_CREACION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Comentario.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        comentario = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedComentario != null) {
            comentarioRepository.delete(insertedComentario).block();
            insertedComentario = null;
        }
        deleteEntities(em);
    }

    @Test
    void createComentario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Comentario
        var returnedComentario = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Comentario.class)
            .returnResult()
            .getResponseBody();

        // Validate the Comentario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertComentarioUpdatableFieldsEquals(returnedComentario, getPersistedComentario(returnedComentario));

        insertedComentario = returnedComentario;
    }

    @Test
    void createComentarioWithExistingId() throws Exception {
        // Create the Comentario with an existing ID
        comentario.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTextoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        comentario.setTexto(null);

        // Create the Comentario, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllComentariosAsStream() {
        // Initialize the database
        comentarioRepository.save(comentario).block();

        List<Comentario> comentarioList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Comentario.class)
            .getResponseBody()
            .filter(comentario::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(comentarioList).isNotNull();
        assertThat(comentarioList).hasSize(1);
        Comentario testComentario = comentarioList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertComentarioAllPropertiesEquals(comentario, testComentario);
        assertComentarioUpdatableFieldsEquals(comentario, testComentario);
    }

    @Test
    void getAllComentarios() {
        // Initialize the database
        insertedComentario = comentarioRepository.save(comentario).block();

        // Get all the comentarioList
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
            .value(hasItem(comentario.getId().intValue()))
            .jsonPath("$.[*].texto")
            .value(hasItem(DEFAULT_TEXTO))
            .jsonPath("$.[*].fechaCreacion")
            .value(hasItem(DEFAULT_FECHA_CREACION.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllComentariosWithEagerRelationshipsIsEnabled() {
        when(comentarioRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(comentarioRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllComentariosWithEagerRelationshipsIsNotEnabled() {
        when(comentarioRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(comentarioRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getComentario() {
        // Initialize the database
        insertedComentario = comentarioRepository.save(comentario).block();

        // Get the comentario
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, comentario.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(comentario.getId().intValue()))
            .jsonPath("$.texto")
            .value(is(DEFAULT_TEXTO))
            .jsonPath("$.fechaCreacion")
            .value(is(DEFAULT_FECHA_CREACION.toString()));
    }

    @Test
    void getNonExistingComentario() {
        // Get the comentario
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingComentario() throws Exception {
        // Initialize the database
        insertedComentario = comentarioRepository.save(comentario).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comentario
        Comentario updatedComentario = comentarioRepository.findById(comentario.getId()).block();
        updatedComentario.texto(UPDATED_TEXTO).fechaCreacion(UPDATED_FECHA_CREACION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedComentario.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedComentario))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedComentarioToMatchAllProperties(updatedComentario);
    }

    @Test
    void putNonExistingComentario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comentario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, comentario.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchComentario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comentario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamComentario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comentario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateComentarioWithPatch() throws Exception {
        // Initialize the database
        insertedComentario = comentarioRepository.save(comentario).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comentario using partial update
        Comentario partialUpdatedComentario = new Comentario();
        partialUpdatedComentario.setId(comentario.getId());

        partialUpdatedComentario.fechaCreacion(UPDATED_FECHA_CREACION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComentario.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedComentario))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comentario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComentarioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedComentario, comentario),
            getPersistedComentario(comentario)
        );
    }

    @Test
    void fullUpdateComentarioWithPatch() throws Exception {
        // Initialize the database
        insertedComentario = comentarioRepository.save(comentario).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comentario using partial update
        Comentario partialUpdatedComentario = new Comentario();
        partialUpdatedComentario.setId(comentario.getId());

        partialUpdatedComentario.texto(UPDATED_TEXTO).fechaCreacion(UPDATED_FECHA_CREACION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComentario.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedComentario))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comentario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertComentarioUpdatableFieldsEquals(partialUpdatedComentario, getPersistedComentario(partialUpdatedComentario));
    }

    @Test
    void patchNonExistingComentario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comentario.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, comentario.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchComentario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comentario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamComentario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comentario.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(comentario))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comentario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteComentario() {
        // Initialize the database
        insertedComentario = comentarioRepository.save(comentario).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the comentario
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, comentario.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return comentarioRepository.count().block();
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

    protected Comentario getPersistedComentario(Comentario comentario) {
        return comentarioRepository.findById(comentario.getId()).block();
    }

    protected void assertPersistedComentarioToMatchAllProperties(Comentario expectedComentario) {
        // Test fails because reactive api returns an empty object instead of null
        // assertComentarioAllPropertiesEquals(expectedComentario, getPersistedComentario(expectedComentario));
        assertComentarioUpdatableFieldsEquals(expectedComentario, getPersistedComentario(expectedComentario));
    }

    protected void assertPersistedComentarioToMatchUpdatableProperties(Comentario expectedComentario) {
        // Test fails because reactive api returns an empty object instead of null
        // assertComentarioAllUpdatablePropertiesEquals(expectedComentario, getPersistedComentario(expectedComentario));
        assertComentarioUpdatableFieldsEquals(expectedComentario, getPersistedComentario(expectedComentario));
    }
}
