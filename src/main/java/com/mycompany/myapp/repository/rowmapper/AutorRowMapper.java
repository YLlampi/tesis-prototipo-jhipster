package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Autor;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Autor}, with proper type conversions.
 */
@Service
public class AutorRowMapper implements BiFunction<Row, String, Autor> {

    private final ColumnConverter converter;

    public AutorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Autor} stored in the database.
     */
    @Override
    public Autor apply(Row row, String prefix) {
        Autor entity = new Autor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
