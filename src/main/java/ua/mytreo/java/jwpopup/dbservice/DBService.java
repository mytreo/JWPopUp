package ua.mytreo.java.jwpopup.dbservice;

import ua.mytreo.java.jwpopup.dbservice.dao.ContactsDAO;
import ua.mytreo.java.jwpopup.dbservice.dao.GroupsDAO;
import ua.mytreo.java.jwpopup.dbservice.dao.MessagesDAO;
import ua.mytreo.java.jwpopup.dbservice.dataSets.ContactsDataSet;
import ua.mytreo.java.jwpopup.dbservice.dataSets.GroupsDataSet;
import ua.mytreo.java.jwpopup.dbservice.dataSets.MessagesDataSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * @author mytreo
 * @version 1.0
 * 08.02.2016.
 */
public class DBService {
    private final Connection connection;

    public DBService() {
        this.connection = getSQLiteConnection();
    }

    public void initBase() throws DBException {
        //порядок важен изза триггеров
        try {
            GroupsDAO gDao = new GroupsDAO(connection);
            gDao.createTable();
            if (gDao.getDefaultGroup() == null) {
                gDao.insertGroup("DEFAULT_GROUP");
            }
            new ContactsDAO(connection).createTable();
            new MessagesDAO(connection).createTable();

        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void cleanUpBase() throws DBException {
        try {
            new MessagesDAO(connection).dropTable();
            new ContactsDAO(connection).dropTable();
            new GroupsDAO(connection).dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void printConnectInfo() {
        try {
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getSQLiteConnection() {
        try {
            String url = "jdbc:sqlite:bwPopUpBase.sqlite3";
            String name = null;
            String pass = null;
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(url, name, pass);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ContactsDataSet getContact(int id) throws DBException {
        try {
            return (new ContactsDAO(connection).get(id));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public List<ContactsDataSet> getContactsByGroup(int grouId) throws DBException {
        try {
            return (new ContactsDAO(connection).getByCond("group_Id = " + grouId));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public GroupsDataSet getGroup(int id) throws DBException {
        try {
            return (new GroupsDAO(connection).get(id));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public List<GroupsDataSet> getAllGroups() throws DBException {
        try {
            return (new GroupsDAO(connection).getByCond(null));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public MessagesDataSet getMessage(long idMessage) throws DBException {
        try {
            return (new MessagesDAO(connection).get(idMessage));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public List<MessagesDataSet> getMessagesByUser(long idContact) throws DBException {
        try {
            return (new MessagesDAO(connection).getByCond("contact_id = " + idContact));
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void removeMessage(MessagesDataSet message) throws DBException {
        try {
            connection.setAutoCommit(false);
            MessagesDAO dao = new MessagesDAO(connection);
            dao.removeMessage(message.getId());
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void removeMessagesByContact(int contactId) throws DBException {
        try {
            connection.setAutoCommit(false);
            MessagesDAO dao = new MessagesDAO(connection);
            dao.removeMessagesByCond("contact_Id = " + contactId);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void removeMessagesByDate(long time) throws DBException {
        try {
            connection.setAutoCommit(false);
            MessagesDAO dao = new MessagesDAO(connection);
            dao.removeMessagesByCond("time < " + time);
            connection.commit();
            System.out.println("messages has been removed");
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }


    public void removeContact(ContactsDataSet contact) throws DBException {
        try {
            connection.setAutoCommit(false);
            ContactsDAO dao = new ContactsDAO(connection);
            //cascade delete message history with user
            dao.removeContact(contact.getId());
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void removeGroup(GroupsDataSet group) throws DBException {
        try {
            connection.setAutoCommit(false);
            GroupsDAO dao = new GroupsDAO(connection);
            ContactsDAO cDao = new ContactsDAO(connection);
            for (ContactsDataSet c : getContactsByGroup(group.getId())) {
                c.setGroup_id(1);
                cDao.updateContact(c);
            }
            dao.removeGroup(group.getId());
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void changeContactGroup(ContactsDataSet contact, int group_id) throws DBException {
        try {
            connection.setAutoCommit(false);
            ContactsDAO dao = new ContactsDAO(connection);
            contact.setGroup_id(group_id);
            dao.updateContact(contact);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }


    public void updContact(ContactsDataSet contact) throws DBException {
        try {
            connection.setAutoCommit(false);
            ContactsDAO dao = new ContactsDAO(connection);
            dao.updateContact(contact);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void updGroup(GroupsDataSet group) throws DBException {
        try {
            connection.setAutoCommit(false);
            GroupsDAO dao = new GroupsDAO(connection);
            dao.updateGroup(group);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void updMessage(MessagesDataSet message) throws DBException {
        try {
            connection.setAutoCommit(false);
            MessagesDAO dao = new MessagesDAO(connection);
            dao.updateMessage(message);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void addContact(ContactsDataSet contact) throws DBException {
        try {
            connection.setAutoCommit(false);
            ContactsDAO dao = new ContactsDAO(connection);
            dao.insertContact(contact);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void addGroup(String groupName) throws DBException {
        try {
            connection.setAutoCommit(false);
            GroupsDAO dao = new GroupsDAO(connection);
            dao.insertGroup(groupName);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    public void addMessage(MessagesDataSet message) throws DBException {
        try {
            connection.setAutoCommit(false);
            MessagesDAO dao = new MessagesDAO(connection);
            dao.insertMessage(message);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

}

