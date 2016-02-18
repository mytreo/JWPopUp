package ua.mytreo.java.dbservice.dataSets;

import java.io.Serializable;

public class ContactsDataSet implements Serializable {
    private int id;
    private String name;
    private String namePC;
    private String IP;
    private int Group_id;
    private String avatar;

    public ContactsDataSet() {

    }

    public ContactsDataSet(int id, String name, String namePC, String IP, int Group_id, String avatar) {
        this.id = id;
        this.name = name;
        this.namePC = namePC;
        this.IP = IP;
        this.Group_id = Group_id;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePC() {
        return namePC;
    }

    public void setNamePC(String namePC) {
        this.namePC = namePC;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getGroup_id() {
        return Group_id;
    }

    public void setGroup_id(int group_id) {
        Group_id = group_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "ContactsDataSet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", namePC='" + namePC + '\'' +
                ", IP='" + IP + '\'' +
                ", Group_id=" + Group_id +
                ", avatar='" + avatar + '\'' +
                '}';
    }

}
