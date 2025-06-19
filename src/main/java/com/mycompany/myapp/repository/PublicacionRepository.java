package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Publicacion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Publicacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PublicacionRepository extends ReactiveCrudRepository<Publicacion, Long>, PublicacionRepositoryInternal {
    @Override
    Mono<Publicacion> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Publicacion> findAllWithEagerRelationships();

    @Override
    Flux<Publicacion> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM publicacion entity JOIN rel_publicacion__etiqueta joinTable ON entity.id = joinTable.etiqueta_id WHERE joinTable.etiqueta_id = :id"
    )
    Flux<Publicacion> findByEtiqueta(Long id);

    @Query("SELECT * FROM publicacion entity WHERE entity.autor_id = :id")
    Flux<Publicacion> findByAutor(Long id);

    @Query("SELECT * FROM publicacion entity WHERE entity.autor_id IS NULL")
    Flux<Publicacion> findAllWhereAutorIsNull();

    @Override
    <S extends Publicacion> Mono<S> save(S entity);

    @Override
    Flux<Publicacion> findAll();

    @Override
    Mono<Publicacion> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PublicacionRepositoryInternal {
    <S extends Publicacion> Mono<S> save(S entity);

    Flux<Publicacion> findAllBy(Pageable pageable);

    Flux<Publicacion> findAll();

    Mono<Publicacion> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Publicacion> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Publicacion> findOneWithEagerRelationships(Long id);

    Flux<Publicacion> findAllWithEagerRelationships();

    Flux<Publicacion> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
