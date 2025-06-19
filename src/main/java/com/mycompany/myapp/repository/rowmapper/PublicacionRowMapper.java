package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Publicacion;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Publicacion}, with proper type conversions.
 */
@Service
public class PublicacionRowMapper implements BiFunction<Row, String, Publicacion> {

    private final ColumnConverter converter;

    public PublicacionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Publicacion} stored in the database.
     */
    @Override
    public Publicacion apply(Row row, String prefix) {
        Publicacion entity = new Publicacion();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitulo(converter.fromRow(row, prefix + "_titulo", String.class));
        entity.setContenido(converter.fromRow(row, prefix + "_contenido", String.class));
        entity.setFechaPublicacion(converter.fromRow(row, prefix + "_fecha_publicacion", Instant.class));
        entity.setAutorId(converter.fromRow(row, prefix + "_autor_id", Long.class));
        return entity;
    }
}
