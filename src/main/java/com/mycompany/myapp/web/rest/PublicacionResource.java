package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Publicacion;
import com.mycompany.myapp.repository.PublicacionRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Publicacion}.
 */
@RestController
@RequestMapping("/api/publicacions")
@Transactional
public class PublicacionResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicacionResource.class);

    private static final String ENTITY_NAME = "publicacion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PublicacionRepository publicacionRepository;

    public PublicacionResource(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    /**
     * {@code POST  /publicacions} : Create a new publicacion.
     *
     * @param publicacion the publicacion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new publicacion, or with status {@code 400 (Bad Request)} if the publicacion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Publicacion>> createPublicacion(@Valid @RequestBody Publicacion publicacion) throws URISyntaxException {
        LOG.debug("REST request to save Publicacion : {}", publicacion);
        if (publicacion.getId() != null) {
            throw new BadRequestAlertException("A new publicacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return publicacionRepository
            .save(publicacion)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/publicacions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /publicacions/:id} : Updates an existing publicacion.
     *
     * @param id the id of the publicacion to save.
     * @param publicacion the publicacion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicacion,
     * or with status {@code 400 (Bad Request)} if the publicacion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publicacion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Publicacion>> updatePublicacion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Publicacion publicacion
    ) throws URISyntaxException {
        LOG.debug("REST request to update Publicacion : {}, {}", id, publicacion);
        if (publicacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicacion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return publicacionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return publicacionRepository
                    .save(publicacion)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /publicacions/:id} : Partial updates given fields of an existing publicacion, field will ignore if it is null
     *
     * @param id the id of the publicacion to save.
     * @param publicacion the publicacion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicacion,
     * or with status {@code 400 (Bad Request)} if the publicacion is not valid,
     * or with status {@code 404 (Not Found)} if the publicacion is not found,
     * or with status {@code 500 (Internal Server Error)} if the publicacion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Publicacion>> partialUpdatePublicacion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Publicacion publicacion
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Publicacion partially : {}, {}", id, publicacion);
        if (publicacion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicacion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return publicacionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Publicacion> result = publicacionRepository
                    .findById(publicacion.getId())
                    .map(existingPublicacion -> {
                        if (publicacion.getTitulo() != null) {
                            existingPublicacion.setTitulo(publicacion.getTitulo());
                        }
                        if (publicacion.getContenido() != null) {
                            existingPublicacion.setContenido(publicacion.getContenido());
                        }
                        if (publicacion.getFechaPublicacion() != null) {
                            existingPublicacion.setFechaPublicacion(publicacion.getFechaPublicacion());
                        }

                        return existingPublicacion;
                    })
                    .flatMap(publicacionRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /publicacions} : get all the publicacions.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of publicacions in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Publicacion>> getAllPublicacions(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Publicacions");
        if (eagerload) {
            return publicacionRepository.findAllWithEagerRelationships().collectList();
        } else {
            return publicacionRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /publicacions} : get all the publicacions as a stream.
     * @return the {@link Flux} of publicacions.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Publicacion> getAllPublicacionsAsStream() {
        LOG.debug("REST request to get all Publicacions as a stream");
        return publicacionRepository.findAll();
    }

    /**
     * {@code GET  /publicacions/:id} : get the "id" publicacion.
     *
     * @param id the id of the publicacion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publicacion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Publicacion>> getPublicacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Publicacion : {}", id);
        Mono<Publicacion> publicacion = publicacionRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(publicacion);
    }

    /**
     * {@code DELETE  /publicacions/:id} : delete the "id" publicacion.
     *
     * @param id the id of the publicacion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePublicacion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Publicacion : {}", id);
        return publicacionRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
