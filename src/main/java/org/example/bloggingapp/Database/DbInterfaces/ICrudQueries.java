package org.example.bloggingapp.Database.DbInterfaces;

public interface ICrudQueries
{
    String getAllQuery(String tableName);
    String getByIntegerQuery( int columnValue,String tableName, String idColumnName);
    String getStringQuery(String columnValue, String tableName, String columnName);
    String createQuery(String tableName, String columns);
    String updateByIdQuery(int id, String tableName, String columns, String idColumnName);
    String deleteByIdQuery( int id, String tableName, String idColumnName);
}
