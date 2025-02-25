package org.isga.project_jee.model;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String tel;

    public Student(int id, String firstName, String lastName, String address, String tel) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.tel = tel;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }
    public String getTel() { return tel; }
}