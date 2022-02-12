package us.logaming.myufrplanning.data.model;

public class PlanningItem {
    private final String title;
    private final String hour;
    private final String info1;
    private final String info2;
    private final boolean isASubject;

    public PlanningItem(String title, String hour, String info1, String info2, boolean isASubject) {
        this.title = title;
        this.hour = hour;
        this.info1 = info1;
        this.info2 = info2;
        this.isASubject = isASubject;
    }

    public String getTitle() {
        return title;
    }

    public String getHour() {
        return hour;
    }

    public String getInfo1() {
        return info1;
    }

    public String getInfo2() {
        return info2;
    }

    public boolean isASubject() {
        return isASubject;
    }
}
