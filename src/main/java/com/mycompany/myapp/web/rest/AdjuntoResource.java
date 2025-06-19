package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Adjunto;
import com.mycompany.myapp.repository.AdjuntoRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Adjunto}.
 */
@RestController
@RequestMapping("/api/adjuntos")
@Transactional
public class AdjuntoResource {

    private static final Logger LOG = LoggerFactory.getLogger(AdjuntoResource.class);

    private static final String ENTITY_NAME = "adjunto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdjuntoRepository adjuntoRepository;

    public AdjuntoResource(AdjuntoRepository adjuntoRepository) {
        this.adjuntoRepository = adjuntoRepository;
    }

    /**
     * {@code POST  /adjuntos} : Create a new adjunto.
     *
     * @param adjunto the adjunto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new adjunto, or with status {@code 400 (Bad Request)} if the adjunto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Adjunto>> createAdjunto(@Valid @RequestBody Adjunto adjunto) throws URISyntaxException {
        LOG.debug("REST request to save Adjunto : {}", adjunto);
        if (adjunto.getId() != null) {
            throw new BadRequestAlertException("A new adjunto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return adjuntoRepository
            .save(adjunto)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/adjuntos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /adjuntos/:id} : Updates an existing adjunto.
     *
     * @param id the id of the adjunto to save.
     * @param adjunto the adjunto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adjunto,
     * or with status {@code 400 (Bad Request)} if the adjunto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the adjunto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Adjunto>> updateAdjunto(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Adjunto adjunto
    ) throws URISyntaxException {
        LOG.debug("REST request to update Adjunto : {}, {}", id, adjunto);
        if (adjunto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adjunto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return adjuntoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return adjuntoRepository
                    .save(adjunto)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /adjuntos/:id} : Partial updates given fields of an existing adjunto, field will ignore if it is null
     *
     * @param id the id of the adjunto to save.
     * @param adjunto the adjunto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adjunto,
     * or with status {@code 400 (Bad Request)} if the adjunto is not valid,
     * or with status {@code 404 (Not Found)} if the adjunto is not found,
     * or with status {@code 500 (Internal Server Error)} if the adjunto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Adjunto>> partialUpdateAdjunto(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Adjunto adjunto
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Adjunto partially : {}, {}", id, adjunto);
        if (adjunto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adjunto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return adjuntoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Adjunto> result = adjuntoRepository
                    .findById(adjunto.getId())
                    .map(existingAdjunto -> {
                        if (adjunto.getNombreArchivo() != null) {
                            existingAdjunto.setNombreArchivo(adjunto.getNombreArchivo());
                        }
                        if (adjunto.getArchivo() != null) {
                            existingAdjunto.setArchivo(adjunto.getArchivo());
                        }
                        if (adjunto.getArchivoContentType() != null) {
                            existingAdjunto.setArchivoContentType(adjunto.getArchivoContentType());
                        }
                        if (adjunto.getTipoMime() != null) {
                            existingAdjunto.setTipoMime(adjunto.getTipoMime());
                        }

                        return existingAdjunto;
                    })
                    .flatMap(adjuntoRepository::save);

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
     * {@code GET  /adjuntos} : get all the adjuntos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of adjuntos in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Adjunto>> getAllAdjuntos(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Adjuntos");
        if (eagerload) {
            return adjuntoRepository.findAllWithEagerRelationships().collectList();
        } else {
            return adjuntoRepository.findAll().collectList();
        }
    }

    /**
     * {@code GET  /adjuntos} : get all the adjuntos as a stream.
     * @return the {@link Flux} of adjuntos.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Adjunto> getAllAdjuntosAsStream() {
        LOG.debug("REST request to get all Adjuntos as a stream");
        return adjuntoRepository.findAll();
    }

    /**
     * {@code GET  /adjuntos/:id} : get the "id" adjunto.
     *
     * @param id the id of the adjunto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the adjunto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Adjunto>> getAdjunto(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Adjunto : {}", id);
        Mono<Adjunto> adjunto = adjuntoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(adjunto);
    }

    /**
     * {@code DELETE  /adjuntos/:id} : delete the "id" adjunto.
     *
     * @param id the id of the adjunto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAdjunto(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Adjunto : {}", id);
        return adjuntoRepository
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
