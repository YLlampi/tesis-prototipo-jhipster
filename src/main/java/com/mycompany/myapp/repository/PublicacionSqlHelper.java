package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PublicacionSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("titulo", table, columnPrefix + "_titulo"));
        columns.add(Column.aliased("contenido", table, columnPrefix + "_contenido"));
        columns.add(Column.aliased("fecha_publicacion", table, columnPrefix + "_fecha_publicacion"));

        columns.add(Column.aliased("autor_id", table, columnPrefix + "_autor_id"));
        return columns;
    }
}
