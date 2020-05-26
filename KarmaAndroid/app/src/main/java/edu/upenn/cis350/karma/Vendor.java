package edu.upenn.cis350.karma;

public class Vendor {

    private String name;
    private String location;
    private String description;
    private String email;

    public Vendor(String name, String location, String description, String email) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getEmail() {return email;};

    public String getDescription() {
        return description;
    }
}


