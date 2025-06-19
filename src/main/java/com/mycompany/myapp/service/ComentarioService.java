package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.ComentarioRepository;
import com.mycompany.myapp.service.dto.ComentarioDTO;
import com.mycompany.myapp.service.mapper.ComentarioMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Comentario}.
 */
@Service
@Transactional
public class ComentarioService {

    private static final Logger LOG = LoggerFactory.getLogger(ComentarioService.class);

    private final ComentarioRepository comentarioRepository;

    private final ComentarioMapper comentarioMapper;

    public ComentarioService(ComentarioRepository comentarioRepository, ComentarioMapper comentarioMapper) {
        this.comentarioRepository = comentarioRepository;
        this.comentarioMapper = comentarioMapper;
    }

    /**
     * Save a comentario.
     *
     * @param comentarioDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ComentarioDTO> save(ComentarioDTO comentarioDTO) {
        LOG.debug("Request to save Comentario : {}", comentarioDTO);
        return comentarioRepository.save(comentarioMapper.toEntity(comentarioDTO)).map(comentarioMapper::toDto);
    }

    /**
     * Update a comentario.
     *
     * @param comentarioDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ComentarioDTO> update(ComentarioDTO comentarioDTO) {
        LOG.debug("Request to update Comentario : {}", comentarioDTO);
        return comentarioRepository.save(comentarioMapper.toEntity(comentarioDTO)).map(comentarioMapper::toDto);
    }

    /**
     * Partially update a comentario.
     *
     * @param comentarioDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ComentarioDTO> partialUpdate(ComentarioDTO comentarioDTO) {
        LOG.debug("Request to partially update Comentario : {}", comentarioDTO);

        return comentarioRepository
            .findById(comentarioDTO.getId())
            .map(existingComentario -> {
                comentarioMapper.partialUpdate(existingComentario, comentarioDTO);

                return existingComentario;
            })
            .flatMap(comentarioRepository::save)
            .map(comentarioMapper::toDto);
    }

    /**
     * Get all the comentarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ComentarioDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Comentarios");
        return comentarioRepository.findAllBy(pageable).map(comentarioMapper::toDto);
    }

    /**
     * Returns the number of comentarios available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return comentarioRepository.count();
    }

    /**
     * Get one comentario by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ComentarioDTO> findOne(Long id) {
        LOG.debug("Request to get Comentario : {}", id);
        return comentarioRepository.findById(id).map(comentarioMapper::toDto);
    }

    /**
     * Delete the comentario by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Comentario : {}", id);
        return comentarioRepository.deleteById(id);
    }
}
