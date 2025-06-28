package com.example.csc325_firebase_webview_auth.model;

import javafx.beans.property.*;

public class Person {
    private final StringProperty name;
    private final StringProperty major;
    private final IntegerProperty age;

    public Person(String name, String major, int age) {
        this.name = new SimpleStringProperty(name);
        this.major = new SimpleStringProperty(major);
        this.age = new SimpleIntegerProperty(age);
    }

    public String getName() { return name.get(); }
    public String getMajor() { return major.get(); }
    public int getAge() { return age.get(); }

    public StringProperty nameProperty() { return name; }
    public StringProperty majorProperty() { return major; }
    public IntegerProperty ageProperty() { return age; }
}
