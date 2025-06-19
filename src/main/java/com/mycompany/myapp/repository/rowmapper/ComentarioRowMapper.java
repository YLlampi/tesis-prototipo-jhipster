package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Comentario;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Comentario}, with proper type conversions.
 */
@Service
public class ComentarioRowMapper implements BiFunction<Row, String, Comentario> {

    private final ColumnConverter converter;

    public ComentarioRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Comentario} stored in the database.
     */
    @Override
    public Comentario apply(Row row, String prefix) {
        Comentario entity = new Comentario();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTexto(converter.fromRow(row, prefix + "_texto", String.class));
        entity.setFechaCreacion(converter.fromRow(row, prefix + "_fecha_creacion", Instant.class));
        entity.setPublicacionId(converter.fromRow(row, prefix + "_publicacion_id", Long.class));
        return entity;
    }
}
