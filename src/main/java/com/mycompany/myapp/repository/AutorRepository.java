package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Autor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Autor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AutorRepository extends ReactiveCrudRepository<Autor, Long>, AutorRepositoryInternal {
    @Query("SELECT * FROM autor entity WHERE entity.user_id = :id")
    Flux<Autor> findByUser(Long id);

    @Query("SELECT * FROM autor entity WHERE entity.user_id IS NULL")
    Flux<Autor> findAllWhereUserIsNull();

    @Override
    <S extends Autor> Mono<S> save(S entity);

    @Override
    Flux<Autor> findAll();

    @Override
    Mono<Autor> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AutorRepositoryInternal {
    <S extends Autor> Mono<S> save(S entity);

    Flux<Autor> findAllBy(Pageable pageable);

    Flux<Autor> findAll();

    Mono<Autor> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Autor> findAllBy(Pageable pageable, Criteria criteria);
}
