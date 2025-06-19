package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Etiqueta;
import com.mycompany.myapp.repository.EtiquetaRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Etiqueta}.
 */
@RestController
@RequestMapping("/api/etiquetas")
@Transactional
public class EtiquetaResource {

    private static final Logger LOG = LoggerFactory.getLogger(EtiquetaResource.class);

    private static final String ENTITY_NAME = "etiqueta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EtiquetaRepository etiquetaRepository;

    public EtiquetaResource(EtiquetaRepository etiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
    }

    /**
     * {@code POST  /etiquetas} : Create a new etiqueta.
     *
     * @param etiqueta the etiqueta to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etiqueta, or with status {@code 400 (Bad Request)} if the etiqueta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Etiqueta>> createEtiqueta(@Valid @RequestBody Etiqueta etiqueta) throws URISyntaxException {
        LOG.debug("REST request to save Etiqueta : {}", etiqueta);
        if (etiqueta.getId() != null) {
            throw new BadRequestAlertException("A new etiqueta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return etiquetaRepository
            .save(etiqueta)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/etiquetas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /etiquetas/:id} : Updates an existing etiqueta.
     *
     * @param id the id of the etiqueta to save.
     * @param etiqueta the etiqueta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etiqueta,
     * or with status {@code 400 (Bad Request)} if the etiqueta is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etiqueta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Etiqueta>> updateEtiqueta(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Etiqueta etiqueta
    ) throws URISyntaxException {
        LOG.debug("REST request to update Etiqueta : {}, {}", id, etiqueta);
        if (etiqueta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etiqueta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return etiquetaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return etiquetaRepository
                    .save(etiqueta)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /etiquetas/:id} : Partial updates given fields of an existing etiqueta, field will ignore if it is null
     *
     * @param id the id of the etiqueta to save.
     * @param etiqueta the etiqueta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etiqueta,
     * or with status {@code 400 (Bad Request)} if the etiqueta is not valid,
     * or with status {@code 404 (Not Found)} if the etiqueta is not found,
     * or with status {@code 500 (Internal Server Error)} if the etiqueta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Etiqueta>> partialUpdateEtiqueta(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Etiqueta etiqueta
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Etiqueta partially : {}, {}", id, etiqueta);
        if (etiqueta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etiqueta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return etiquetaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Etiqueta> result = etiquetaRepository
                    .findById(etiqueta.getId())
                    .map(existingEtiqueta -> {
                        if (etiqueta.getNombre() != null) {
                            existingEtiqueta.setNombre(etiqueta.getNombre());
                        }

                        return existingEtiqueta;
                    })
                    .flatMap(etiquetaRepository::save);

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
     * {@code GET  /etiquetas} : get all the etiquetas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of etiquetas in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Etiqueta>> getAllEtiquetas() {
        LOG.debug("REST request to get all Etiquetas");
        return etiquetaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /etiquetas} : get all the etiquetas as a stream.
     * @return the {@link Flux} of etiquetas.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Etiqueta> getAllEtiquetasAsStream() {
        LOG.debug("REST request to get all Etiquetas as a stream");
        return etiquetaRepository.findAll();
    }

    /**
     * {@code GET  /etiquetas/:id} : get the "id" etiqueta.
     *
     * @param id the id of the etiqueta to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etiqueta, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Etiqueta>> getEtiqueta(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Etiqueta : {}", id);
        Mono<Etiqueta> etiqueta = etiquetaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(etiqueta);
    }

    /**
     * {@code DELETE  /etiquetas/:id} : delete the "id" etiqueta.
     *
     * @param id the id of the etiqueta to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEtiqueta(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Etiqueta : {}", id);
        return etiquetaRepository
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
