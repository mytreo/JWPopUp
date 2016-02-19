package ua.mytreo.java.jwpopup.dbservice.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Executor {
    private final Connection connection;

    public Executor(Connection connection) {
        this.connection = connection;
    }

    public void execUpdate(String update) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(update);
        stmt.close();
    }

    public <T> T execQuery(String query,
                           ResultHandler<T> handler)
            throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(query);
        ResultSet result = stmt.getResultSet();
        T value = handler.handle(result);
        result.close();
        stmt.close();

        return value;
    }

    public String queryBuilder(String crud, String tableName, String[] colNames, Object[] values, String condition) {

        condition = (!(condition == null || condition.isEmpty())) ? " WHERE " + condition : "";
        StringBuilder sql = new StringBuilder();
        switch (crud) {
            case "insert": {
                sql.append("INSERT INTO ");
                sql.append(tableName);
                sql.append("(");

                // columns
                for (int i = 0; i < colNames.length; i++) {
                    sql.append(colNames[i]);
                    if (i < colNames.length - 1)
                        sql.append(", ");
                }
                sql.append(") ");
                sql.append("Values(");

                for (int i = 0; i < values.length; i++) {
                    if (values[i] instanceof String) {
                        sql.append("'");
                        sql.append(values[i]);
                        sql.append("'");
                    } else {
                        sql.append(values[i]);
                    }

                    if (i < values.length - 1)
                        sql.append(", ");
                }
                sql.append(") ").append(";");

                break;
            }
            case "select": {
                sql.append("SELECT ");
                // columns
                for (int i = 0; i < colNames.length; i++) {
                    sql.append(colNames[i]);
                    if (i < colNames.length - 1)
                        sql.append(", ");
                }
                sql.append(" FROM ")
                        .append(tableName)
                        .append(" ")
                        .append(condition)
                        .append(";");

                break;
            }
            case "delete": {
                sql.append("DELETE FROM ")
                        .append(tableName)
                        .append(" ")
                        .append(condition)
                        .append(";");
                break;
            }
            case "update": {
                sql.append("UPDATE ")
                        .append(tableName)
                        .append(" ")
                        .append("SET ");
                //с 1 т.к. пропускаем колонку id
                for (int i = 1; i < values.length; i++) {
                    sql.append(colNames[i]).append(" = ");

                    if (values[i] instanceof String) {
                        sql.append("'").append(values[i]).append("'");
                    } else {
                        sql.append(values[i]);
                    }
                    sql.append(" ");
                    if (i < values.length - 1)
                        sql.append(", ");
                }
                sql.append(condition).append(";");

                break;
            }
        }
        return sql.toString();
    }
}
