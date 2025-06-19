package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Adjunto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Adjunto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdjuntoRepository extends ReactiveCrudRepository<Adjunto, Long>, AdjuntoRepositoryInternal {
    @Override
    Mono<Adjunto> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Adjunto> findAllWithEagerRelationships();

    @Override
    Flux<Adjunto> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM adjunto entity WHERE entity.publicacion_id = :id")
    Flux<Adjunto> findByPublicacion(Long id);

    @Query("SELECT * FROM adjunto entity WHERE entity.publicacion_id IS NULL")
    Flux<Adjunto> findAllWherePublicacionIsNull();

    @Override
    <S extends Adjunto> Mono<S> save(S entity);

    @Override
    Flux<Adjunto> findAll();

    @Override
    Mono<Adjunto> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AdjuntoRepositoryInternal {
    <S extends Adjunto> Mono<S> save(S entity);

    Flux<Adjunto> findAllBy(Pageable pageable);

    Flux<Adjunto> findAll();

    Mono<Adjunto> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Adjunto> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Adjunto> findOneWithEagerRelationships(Long id);

    Flux<Adjunto> findAllWithEagerRelationships();

    Flux<Adjunto> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
