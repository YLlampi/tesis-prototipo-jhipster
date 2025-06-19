package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Etiqueta;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Etiqueta}, with proper type conversions.
 */
@Service
public class EtiquetaRowMapper implements BiFunction<Row, String, Etiqueta> {

    private final ColumnConverter converter;

    public EtiquetaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Etiqueta} stored in the database.
     */
    @Override
    public Etiqueta apply(Row row, String prefix) {
        Etiqueta entity = new Etiqueta();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        return entity;
    }
}
