package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Etiqueta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Etiqueta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EtiquetaRepository extends ReactiveCrudRepository<Etiqueta, Long>, EtiquetaRepositoryInternal {
    @Override
    <S extends Etiqueta> Mono<S> save(S entity);

    @Override
    Flux<Etiqueta> findAll();

    @Override
    Mono<Etiqueta> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EtiquetaRepositoryInternal {
    <S extends Etiqueta> Mono<S> save(S entity);

    Flux<Etiqueta> findAllBy(Pageable pageable);

    Flux<Etiqueta> findAll();

    Mono<Etiqueta> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Etiqueta> findAllBy(Pageable pageable, Criteria criteria);
}
