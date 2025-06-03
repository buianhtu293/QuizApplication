package model;

public class Subject {
    private String name;
    private int xmlResId;

    public Subject(String name, int xmlResId) {
        this.name = name;
        this.xmlResId = xmlResId;
    }

    public String getName() {
        return name;
    }

    public int getXmlResId() {
        return xmlResId;
    }
}
