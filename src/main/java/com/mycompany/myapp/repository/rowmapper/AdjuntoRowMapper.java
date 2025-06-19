package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Adjunto;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Adjunto}, with proper type conversions.
 */
@Service
public class AdjuntoRowMapper implements BiFunction<Row, String, Adjunto> {

    private final ColumnConverter converter;

    public AdjuntoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Adjunto} stored in the database.
     */
    @Override
    public Adjunto apply(Row row, String prefix) {
        Adjunto entity = new Adjunto();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombreArchivo(converter.fromRow(row, prefix + "_nombre_archivo", String.class));
        entity.setArchivoContentType(converter.fromRow(row, prefix + "_archivo_content_type", String.class));
        entity.setArchivo(converter.fromRow(row, prefix + "_archivo", byte[].class));
        entity.setTipoMime(converter.fromRow(row, prefix + "_tipo_mime", String.class));
        entity.setPublicacionId(converter.fromRow(row, prefix + "_publicacion_id", Long.class));
        return entity;
    }
}
