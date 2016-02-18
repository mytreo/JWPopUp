package ua.mytreo.java.dbservice.dao;

import ua.mytreo.java.dbservice.dataSets.GroupsDataSet;
import ua.mytreo.java.dbservice.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mytreo
 * @version 1.0
 * 19.11.2015.
 */
public class GroupsDAO {
    private final String tableName = "tContactGroups";
    private final String[] colNames = {"id", "group_name", "col"};
    private Executor executor;

    public GroupsDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public void createTable() throws SQLException {
        executor.execUpdate("CREATE TABLE IF NOT EXISTS 'tContactGroups' ('id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "'group_name' TEXT,'col' INTEGER);");
    }

    public GroupsDataSet get(int id) throws SQLException {
        return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, " id = " + id)
                , result -> {
                    result.next();
                    return new GroupsDataSet(result.getInt(1), result.getString(2), result.getInt(3));
                });
    }

    public GroupsDataSet getDefaultGroup() throws SQLException {
        try {
            return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, " 1 = 1  LIMIT 1,1")//id=1 limit ne vsegda 1
                    , result -> {
                        result.next();
                        return new GroupsDataSet(result.getInt(1), result.getString(2), result.getInt(3));
                    });
        } catch (SQLException e) {
            return null;
        }
    }

    public List<GroupsDataSet> getByCond(String condition) throws SQLException {
        return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, condition)
                , result -> {
                    List<GroupsDataSet> resList = new ArrayList<>();
                    while (result.next()) {
                        resList.add(new GroupsDataSet(result.getInt(1), result.getString(2), result.getInt(3)));
                    }
                    return resList;
                });
    }

    public void insertGroup(String name) throws SQLException {
        executor.execUpdate(executor.queryBuilder("insert", tableName, colNames, new Object[]{null, name, 0}, null));
    }

    public void updateGroup(GroupsDataSet updGroupDataSet) throws SQLException {
        executor.execUpdate(executor.queryBuilder("update", tableName, colNames,
                new String[]{null, updGroupDataSet.getGroup_name()
                        , String.valueOf(updGroupDataSet.getCol())}, "id = " + updGroupDataSet.getId()));
    }

    public void removeGroup(long id) throws SQLException {
        executor.execUpdate(executor.queryBuilder("delete", tableName, colNames,
                null, "id = " + id));
    }

    public void dropTable() throws SQLException {
        executor.execUpdate("DROP table " + tableName);
    }

}
