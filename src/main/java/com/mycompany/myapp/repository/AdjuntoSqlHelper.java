package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AdjuntoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nombre_archivo", table, columnPrefix + "_nombre_archivo"));
        columns.add(Column.aliased("archivo", table, columnPrefix + "_archivo"));
        columns.add(Column.aliased("archivo_content_type", table, columnPrefix + "_archivo_content_type"));
        columns.add(Column.aliased("tipo_mime", table, columnPrefix + "_tipo_mime"));

        columns.add(Column.aliased("publicacion_id", table, columnPrefix + "_publicacion_id"));
        return columns;
    }
}
