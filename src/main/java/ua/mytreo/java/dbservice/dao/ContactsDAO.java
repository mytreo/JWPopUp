package ua.mytreo.java.dbservice.dao;


import ua.mytreo.java.dbservice.dataSets.ContactsDataSet;
import ua.mytreo.java.dbservice.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactsDAO {
    private final String tableName = "tContacts";
    private final String[] colNames = {"id", "name", "namePC", "IP", "Group_id", "avatar"};
    private Executor executor;

    public ContactsDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public void createTable() throws SQLException {
        executor.execUpdate("CREATE TABLE IF NOT EXISTS 'tContacts' ('id' INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                + "'name' TEXT,'namePC' TEXT,"
                + "'IP' TEXT,'Group_id' INTEGER,'avatar' TEXT," +
                "FOREIGN KEY(Group_id) REFERENCES tContactGroups(id));");
        executor.execUpdate("CREATE TRIGGER IF NOT EXISTS 'TR_CONTACTS_1' " +
                "AFTER INSERT " +
                "ON 'tContacts' " +
                "FOR EACH ROW " +
                "BEGIN " +
                " UPDATE tContactGroups " +
                "     SET col = col+1 " +
                "   WHERE id = NEW.group_id; " +
                "END;");
        executor.execUpdate("CREATE TRIGGER IF NOT EXISTS 'TR_CONTACTS_2' " +
                "AFTER DELETE " +
                "ON 'tContacts' " +
                "FOR EACH ROW " +
                "BEGIN " +
                " UPDATE tContactGroups " +
                "     SET col = col-1 " +
                "   WHERE id = OLD.group_id; " +
                "END; ");
        executor.execUpdate("CREATE TRIGGER IF NOT EXISTS 'TR_CONTACTS_4' " +
                "AFTER UPDATE " +
                "ON [tContacts] " +
                "FOR EACH ROW " +
                "BEGIN " +
                " UPDATE tContactGroups " +
                "     SET col = col+1 " +
                "   WHERE id = NEW.group_id;  " +
                " UPDATE tContactGroups " +
                "     SET col = col-1 " +
                "   WHERE id = OLD.group_id; " +
                "END;");
    }

    public ContactsDataSet get(int id) throws SQLException {
        return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, " id=" + id)
                , result -> {
                    result.next();
                    return new ContactsDataSet(result.getInt(1), result.getString(2)
                            , result.getString(3), result.getString(4), result.getInt(5), result.getString(6));
                });
    }

    public List<ContactsDataSet> getByCond(String condition) throws SQLException {
        return executor.execQuery(executor.queryBuilder("select", tableName, colNames, null, condition)
                , result -> {
                    List<ContactsDataSet> resList = new ArrayList<>();
                    /*do {
                        result.next();
                        resList.add(new ContactsDataSet(result.getInt(1), result.getString(2)
                                , result.getString(3), result.getString(4), result.getInt(5), result.getString(6)));
                    } while (!result.isLast());*/
                    while (result.next()) {
                        resList.add(new ContactsDataSet(result.getInt(1), result.getString(2)
                                , result.getString(3), result.getString(4), result.getInt(5), result.getString(6)));
                    }
                    return resList;
                });
    }

    public void insertContact(ContactsDataSet contact) throws SQLException {
        executor.execUpdate(executor.queryBuilder("insert", tableName, colNames
                , new String[]{null, contact.getName(), contact.getNamePC(), contact.getIP()
                        , String.valueOf(contact.getGroup_id()), String.valueOf(contact.getAvatar())}, null));
    }

    public void updateContact(ContactsDataSet contact) throws SQLException {
        executor.execUpdate(executor.queryBuilder("update", tableName, colNames
                , new String[]{null, contact.getName(), contact.getNamePC(), contact.getIP()
                        , String.valueOf(contact.getGroup_id()), String.valueOf(contact.getAvatar())}, "id=" + contact.getId()));
    }

    public void removeContact(long id) throws SQLException {
        executor.execUpdate(executor.queryBuilder("delete", tableName, colNames,
                null, "id=" + id));
    }

    public void removeContactsByCond(String condition) throws SQLException {
        executor.execUpdate(executor.queryBuilder("delete", tableName, colNames,
                null, condition));
    }

    public void dropTable() throws SQLException {
        executor.execUpdate("DROP table " + tableName);
    }
}
