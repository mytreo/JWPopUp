package ua.mytreo.java.dbservice.dao;



import ua.mytreo.java.dbservice.dataSets.MessagesDataSet;
import ua.mytreo.java.dbservice.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *  @author mytreo
 * @version 1.0
 * 19.11.2015.
 */
public class MessagesDAO {
    protected String tableName = "tMessages";
    protected String colNames[] = {"id", "text", "contact_id", "from0to1", "success", "time"};
    private Executor executor;

    public MessagesDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public void createTable() throws SQLException {
        executor.execUpdate("CREATE TABLE IF NOT EXISTS 'tMessages' ('id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "'text' TEXT,'contact_id' INTEGER,"
                + "'from0to1' INTEGER , 'success' INTEGER , 'time' INTEGER," +
                "FOREIGN KEY(contact_id) REFERENCES tContacts(id) ON DELETE CASCADE);");
    }

    public MessagesDataSet get(long id) throws SQLException {
        return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, "where id=" + id)
                , result -> {
                    result.next();
                    return new MessagesDataSet(result.getLong(1), result.getString(2)
                            , result.getInt(3), result.getInt(4), result.getInt(5), result.getInt(6));
                });
    }

    public List<MessagesDataSet> getByCond(String condition) throws SQLException {
        return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, condition)
                , result -> {
                    List<MessagesDataSet> resList = new ArrayList<>();
                    while(result.next()){
                        resList.add(new MessagesDataSet(result.getLong(1), result.getString(2)
                                , result.getInt(3), result.getInt(4), result.getInt(5), result.getInt(6)));
                    }

                    return resList;
                });
    }

    public void insertMessage(MessagesDataSet message) throws SQLException {
        executor.execUpdate(executor.queryBuilder("insert", tableName, colNames
                , new String[]{null, message.getText(), String.valueOf(message.getContact_id())
                        , String.valueOf(message.getFrom0to1()), String.valueOf(message.getSuccess())
                        , String.valueOf(message.getTime())}, null));
    }

    public void updateMessage(MessagesDataSet message) throws SQLException {
        executor.execUpdate(executor.queryBuilder("update", tableName, colNames,
                new String[]{null, message.getText(), String.valueOf(message.getContact_id())
                        , String.valueOf(message.getFrom0to1()), String.valueOf(message.getSuccess())
                        , String.valueOf(message.getTime())}, "id=" + message.getId()));
    }

    public void removeMessage(long id) throws SQLException {
        executor.execUpdate(executor.queryBuilder("delete", tableName, colNames,
                null, "id=" + id));
    }

    public void removeMessagesByCond(String condition) throws SQLException {
        executor.execUpdate(executor.queryBuilder("delete", tableName, colNames,
                null, condition));
    }

    public void dropTable() throws SQLException {
        executor.execUpdate("DROP table " + tableName);
    }


}
