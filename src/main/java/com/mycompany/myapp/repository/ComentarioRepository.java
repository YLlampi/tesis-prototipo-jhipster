package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Comentario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Comentario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ComentarioRepository extends ReactiveCrudRepository<Comentario, Long>, ComentarioRepositoryInternal {
    @Override
    Mono<Comentario> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Comentario> findAllWithEagerRelationships();

    @Override
    Flux<Comentario> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM comentario entity WHERE entity.publicacion_id = :id")
    Flux<Comentario> findByPublicacion(Long id);

    @Query("SELECT * FROM comentario entity WHERE entity.publicacion_id IS NULL")
    Flux<Comentario> findAllWherePublicacionIsNull();

    @Override
    <S extends Comentario> Mono<S> save(S entity);

    @Override
    Flux<Comentario> findAll();

    @Override
    Mono<Comentario> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ComentarioRepositoryInternal {
    <S extends Comentario> Mono<S> save(S entity);

    Flux<Comentario> findAllBy(Pageable pageable);

    Flux<Comentario> findAll();

    Mono<Comentario> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Comentario> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Comentario> findOneWithEagerRelationships(Long id);

    Flux<Comentario> findAllWithEagerRelationships();

    Flux<Comentario> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
