package ua.mytreo.java.dbservice.dataSets;


public class MessagesDataSet {
    private long id;
    private String text;
    private int contact_id;
    private int from0to1;
    private int success;
    private long time;


    public MessagesDataSet(long id, String text, int contact_id, int from0to1, int success, long time) {
        this.id = id;
        this.text = text;
        this.contact_id = contact_id;
        this.from0to1 = from0to1;
        this.success = success;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public int getFrom0to1() {
        return from0to1;
    }

    public void setFrom0to1(int from0to1) {
        this.from0to1 = from0to1;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MessagesDataSet{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", contact_id=" + contact_id +
                ", from0to1=" + from0to1 +
                ", success=" + success +
                ", time=" + time +
                '}';
    }
}
