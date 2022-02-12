package us.logaming.myufrplanning.data.model;

public class GroupItem {
    private final String id;
    private final String title;

    public GroupItem(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
