package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Comentario;
import com.mycompany.myapp.repository.rowmapper.ComentarioRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Comentario entity.
 */
@SuppressWarnings("unused")
class ComentarioRepositoryInternalImpl extends SimpleR2dbcRepository<Comentario, Long> implements ComentarioRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PublicacionRowMapper publicacionMapper;
    private final ComentarioRowMapper comentarioMapper;

    private static final Table entityTable = Table.aliased("comentario", EntityManager.ENTITY_ALIAS);
    private static final Table publicacionTable = Table.aliased("publicacion", "publicacion");

    public ComentarioRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PublicacionRowMapper publicacionMapper,
        ComentarioRowMapper comentarioMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Comentario.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.publicacionMapper = publicacionMapper;
        this.comentarioMapper = comentarioMapper;
    }

    @Override
    public Flux<Comentario> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Comentario> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ComentarioSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PublicacionSqlHelper.getColumns(publicacionTable, "publicacion"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(publicacionTable)
            .on(Column.create("publicacion_id", entityTable))
            .equals(Column.create("id", publicacionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Comentario.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Comentario> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Comentario> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Comentario> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Comentario> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Comentario> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Comentario process(Row row, RowMetadata metadata) {
        Comentario entity = comentarioMapper.apply(row, "e");
        entity.setPublicacion(publicacionMapper.apply(row, "publicacion"));
        return entity;
    }

    @Override
    public <S extends Comentario> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
