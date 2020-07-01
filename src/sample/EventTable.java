package sample;

public class EventTable {

    private String date;
    private String title;
    private String detail;

    public EventTable() {
        this.date = "null";
        this.title = "null";
        this.detail = "";
    }

    public EventTable(String date, String title, String detail) {
        this.date = date;
        this.title = title;
        this.detail = detail;
    }

    //getter and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

}
