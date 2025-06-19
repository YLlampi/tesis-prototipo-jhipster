package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ComentarioSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("texto", table, columnPrefix + "_texto"));
        columns.add(Column.aliased("fecha_creacion", table, columnPrefix + "_fecha_creacion"));

        columns.add(Column.aliased("publicacion_id", table, columnPrefix + "_publicacion_id"));
        return columns;
    }
}
