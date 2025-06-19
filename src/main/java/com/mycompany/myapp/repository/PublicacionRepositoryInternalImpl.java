package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Etiqueta;
import com.mycompany.myapp.domain.Publicacion;
import com.mycompany.myapp.repository.rowmapper.AutorRowMapper;
import com.mycompany.myapp.repository.rowmapper.PublicacionRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Publicacion entity.
 */
@SuppressWarnings("unused")
class PublicacionRepositoryInternalImpl extends SimpleR2dbcRepository<Publicacion, Long> implements PublicacionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AutorRowMapper autorMapper;
    private final PublicacionRowMapper publicacionMapper;

    private static final Table entityTable = Table.aliased("publicacion", EntityManager.ENTITY_ALIAS);
    private static final Table autorTable = Table.aliased("autor", "autor");

    private static final EntityManager.LinkTable etiquetaLink = new EntityManager.LinkTable(
        "rel_publicacion__etiqueta",
        "publicacion_id",
        "etiqueta_id"
    );

    public PublicacionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AutorRowMapper autorMapper,
        PublicacionRowMapper publicacionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Publicacion.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.autorMapper = autorMapper;
        this.publicacionMapper = publicacionMapper;
    }

    @Override
    public Flux<Publicacion> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Publicacion> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PublicacionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AutorSqlHelper.getColumns(autorTable, "autor"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(autorTable)
            .on(Column.create("autor_id", entityTable))
            .equals(Column.create("id", autorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Publicacion.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Publicacion> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Publicacion> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Publicacion> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Publicacion> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Publicacion> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Publicacion process(Row row, RowMetadata metadata) {
        Publicacion entity = publicacionMapper.apply(row, "e");
        entity.setAutor(autorMapper.apply(row, "autor"));
        return entity;
    }

    @Override
    public <S extends Publicacion> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Publicacion> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(etiquetaLink, entity.getId(), entity.getEtiquetas().stream().map(Etiqueta::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(etiquetaLink, entityId);
    }
}
