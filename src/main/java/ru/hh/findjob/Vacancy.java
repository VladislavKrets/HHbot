package ru.hh.findjob;

/**
 * Created by lollipop on 13.06.2017.
 */
public class Vacancy {
    private String name;
    private String url;
    private String id;

    public Vacancy(String name, String url, String id) {
        this.name = name;
        this.url = url;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}
