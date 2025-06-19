package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AutorAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Autor;
import com.mycompany.myapp.repository.AutorRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.UserRepository;
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
 * Integration tests for the {@link AutorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AutorResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "i@{EF.q";
    private static final String UPDATED_EMAIL = ",2j@NJSnl.r";

    private static final String ENTITY_API_URL = "/api/autors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Autor autor;

    private Autor insertedAutor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Autor createEntity() {
        return new Autor().nombre(DEFAULT_NOMBRE).email(DEFAULT_EMAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Autor createUpdatedEntity() {
        return new Autor().nombre(UPDATED_NOMBRE).email(UPDATED_EMAIL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Autor.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        autor = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAutor != null) {
            autorRepository.delete(insertedAutor).block();
            insertedAutor = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createAutor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Autor
        var returnedAutor = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Autor.class)
            .returnResult()
            .getResponseBody();

        // Validate the Autor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAutorUpdatableFieldsEquals(returnedAutor, getPersistedAutor(returnedAutor));

        insertedAutor = returnedAutor;
    }

    @Test
    void createAutorWithExistingId() throws Exception {
        // Create the Autor with an existing ID
        autor.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        autor.setNombre(null);

        // Create the Autor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        autor.setEmail(null);

        // Create the Autor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAutorsAsStream() {
        // Initialize the database
        autorRepository.save(autor).block();

        List<Autor> autorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Autor.class)
            .getResponseBody()
            .filter(autor::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(autorList).isNotNull();
        assertThat(autorList).hasSize(1);
        Autor testAutor = autorList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertAutorAllPropertiesEquals(autor, testAutor);
        assertAutorUpdatableFieldsEquals(autor, testAutor);
    }

    @Test
    void getAllAutors() {
        // Initialize the database
        insertedAutor = autorRepository.save(autor).block();

        // Get all the autorList
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
            .value(hasItem(autor.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @Test
    void getAutor() {
        // Initialize the database
        insertedAutor = autorRepository.save(autor).block();

        // Get the autor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, autor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(autor.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingAutor() {
        // Get the autor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAutor() throws Exception {
        // Initialize the database
        insertedAutor = autorRepository.save(autor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the autor
        Autor updatedAutor = autorRepository.findById(autor.getId()).block();
        updatedAutor.nombre(UPDATED_NOMBRE).email(UPDATED_EMAIL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAutor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedAutor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAutorToMatchAllProperties(updatedAutor);
    }

    @Test
    void putNonExistingAutor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        autor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, autor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAutor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        autor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAutor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        autor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAutorWithPatch() throws Exception {
        // Initialize the database
        insertedAutor = autorRepository.save(autor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the autor using partial update
        Autor partialUpdatedAutor = new Autor();
        partialUpdatedAutor.setId(autor.getId());

        partialUpdatedAutor.nombre(UPDATED_NOMBRE).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAutor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAutor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Autor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAutorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAutor, autor), getPersistedAutor(autor));
    }

    @Test
    void fullUpdateAutorWithPatch() throws Exception {
        // Initialize the database
        insertedAutor = autorRepository.save(autor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the autor using partial update
        Autor partialUpdatedAutor = new Autor();
        partialUpdatedAutor.setId(autor.getId());

        partialUpdatedAutor.nombre(UPDATED_NOMBRE).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAutor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAutor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Autor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAutorUpdatableFieldsEquals(partialUpdatedAutor, getPersistedAutor(partialUpdatedAutor));
    }

    @Test
    void patchNonExistingAutor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        autor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, autor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAutor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        autor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAutor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        autor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(autor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Autor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAutor() {
        // Initialize the database
        insertedAutor = autorRepository.save(autor).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the autor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, autor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return autorRepository.count().block();
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

    protected Autor getPersistedAutor(Autor autor) {
        return autorRepository.findById(autor.getId()).block();
    }

    protected void assertPersistedAutorToMatchAllProperties(Autor expectedAutor) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAutorAllPropertiesEquals(expectedAutor, getPersistedAutor(expectedAutor));
        assertAutorUpdatableFieldsEquals(expectedAutor, getPersistedAutor(expectedAutor));
    }

    protected void assertPersistedAutorToMatchUpdatableProperties(Autor expectedAutor) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAutorAllUpdatablePropertiesEquals(expectedAutor, getPersistedAutor(expectedAutor));
        assertAutorUpdatableFieldsEquals(expectedAutor, getPersistedAutor(expectedAutor));
    }
}
