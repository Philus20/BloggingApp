package org.example.bloggingapp.Database.Services;

import org.example.bloggingapp.Database.DbInterfaces.ICrudQueries;

public class CrudQueries implements ICrudQueries {

    @Override
    public String getAllQuery(String tableName) {
        return "SELECT * FROM \"" + tableName + "\"";
    }

    @Override
    public String getByIntegerQuery(int columnValue, String tableName, String idColumnName) {
        return "SELECT * FROM \"" + tableName + "\" WHERE " + idColumnName + " = " + columnValue;
    }

    @Override
    public String getStringQuery(String columnValue, String tableName, String columnName) {
        return "SELECT * FROM \"" + tableName + "\" WHERE " + columnName + " = '" + columnValue + "'";
    }

    @Override
    public String createQuery(String tableName, String columns) {

        String[] cols = columns.split(", ");
        StringBuilder placeholders = new StringBuilder();

        for (int i = 0; i < cols.length; i++) {
            placeholders.append("?");
            if (i < cols.length - 1) {
                placeholders.append(", ");
            }
        }

        return "INSERT INTO \"" + tableName +
                "\" (\"" + columns.replace(", ", "\", \"") + "\") VALUES (" +
                placeholders + ")";
    }




    @Override
    public String updateByIdQuery(int id, String tableName, String column, String idColumnName) {
        return "UPDATE \""+tableName + "\" SET "+ column +" = ? WHERE "+ idColumnName +" = "+ id;
    }

    @Override
    public String deleteByIdQuery(int id, String tableName, String idColumnName) {
        return "DELETE FROM \""+tableName +"\" WHERE "+idColumnName+ " = "+ id;
    }
}
