package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Adjunto;
import com.mycompany.myapp.repository.rowmapper.AdjuntoRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Adjunto entity.
 */
@SuppressWarnings("unused")
class AdjuntoRepositoryInternalImpl extends SimpleR2dbcRepository<Adjunto, Long> implements AdjuntoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PublicacionRowMapper publicacionMapper;
    private final AdjuntoRowMapper adjuntoMapper;

    private static final Table entityTable = Table.aliased("adjunto", EntityManager.ENTITY_ALIAS);
    private static final Table publicacionTable = Table.aliased("publicacion", "publicacion");

    public AdjuntoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PublicacionRowMapper publicacionMapper,
        AdjuntoRowMapper adjuntoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Adjunto.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.publicacionMapper = publicacionMapper;
        this.adjuntoMapper = adjuntoMapper;
    }

    @Override
    public Flux<Adjunto> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Adjunto> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AdjuntoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PublicacionSqlHelper.getColumns(publicacionTable, "publicacion"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(publicacionTable)
            .on(Column.create("publicacion_id", entityTable))
            .equals(Column.create("id", publicacionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Adjunto.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Adjunto> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Adjunto> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Adjunto> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Adjunto> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Adjunto> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Adjunto process(Row row, RowMetadata metadata) {
        Adjunto entity = adjuntoMapper.apply(row, "e");
        entity.setPublicacion(publicacionMapper.apply(row, "publicacion"));
        return entity;
    }

    @Override
    public <S extends Adjunto> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
