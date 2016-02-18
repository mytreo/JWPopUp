package ua.mytreo.java.dbservice.dataSets;


public class GroupsDataSet {

    private int id;
    private String group_name;
    private int col;

    public GroupsDataSet(int id, String group_name, int col) {
        this.id = id;
        this.group_name = group_name;
        this.col = col;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString() {
        return "GroupsDataSet{" +
                "id=" + id +
                ", group_name='" + group_name + '\'' +
                ", col=" + col +
                '}';
    }
}
