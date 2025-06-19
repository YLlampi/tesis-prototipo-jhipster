package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.PublicacionRepository;
import com.mycompany.myapp.service.dto.PublicacionDTO;
import com.mycompany.myapp.service.mapper.PublicacionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Publicacion}.
 */
@Service
@Transactional
public class PublicacionService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicacionService.class);

    private final PublicacionRepository publicacionRepository;

    private final PublicacionMapper publicacionMapper;

    public PublicacionService(PublicacionRepository publicacionRepository, PublicacionMapper publicacionMapper) {
        this.publicacionRepository = publicacionRepository;
        this.publicacionMapper = publicacionMapper;
    }

    /**
     * Save a publicacion.
     *
     * @param publicacionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PublicacionDTO> save(PublicacionDTO publicacionDTO) {
        LOG.debug("Request to save Publicacion : {}", publicacionDTO);
        return publicacionRepository.save(publicacionMapper.toEntity(publicacionDTO)).map(publicacionMapper::toDto);
    }

    /**
     * Update a publicacion.
     *
     * @param publicacionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PublicacionDTO> update(PublicacionDTO publicacionDTO) {
        LOG.debug("Request to update Publicacion : {}", publicacionDTO);
        return publicacionRepository.save(publicacionMapper.toEntity(publicacionDTO)).map(publicacionMapper::toDto);
    }

    /**
     * Partially update a publicacion.
     *
     * @param publicacionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PublicacionDTO> partialUpdate(PublicacionDTO publicacionDTO) {
        LOG.debug("Request to partially update Publicacion : {}", publicacionDTO);

        return publicacionRepository
            .findById(publicacionDTO.getId())
            .map(existingPublicacion -> {
                publicacionMapper.partialUpdate(existingPublicacion, publicacionDTO);

                return existingPublicacion;
            })
            .flatMap(publicacionRepository::save)
            .map(publicacionMapper::toDto);
    }

    /**
     * Get all the publicacions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PublicacionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Publicacions");
        return publicacionRepository.findAllBy(pageable).map(publicacionMapper::toDto);
    }

    /**
     * Returns the number of publicacions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return publicacionRepository.count();
    }

    /**
     * Get one publicacion by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PublicacionDTO> findOne(Long id) {
        LOG.debug("Request to get Publicacion : {}", id);
        return publicacionRepository.findById(id).map(publicacionMapper::toDto);
    }

    /**
     * Delete the publicacion by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Publicacion : {}", id);
        return publicacionRepository.deleteById(id);
    }
}
