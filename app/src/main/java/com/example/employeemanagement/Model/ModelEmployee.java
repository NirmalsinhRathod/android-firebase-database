package com.example.employeemanagement.Model;

import java.nio.LongBuffer;

public class ModelEmployee {
    String employeeId;
    String name;
    String gender;
    String address;
    Long contact;
    String salary;
    String email;
    String image;
    String designation;
    String about;


    public ModelEmployee()
    {

    }



    public ModelEmployee(String employeeId, String image, String name, String gender, Long contact, String address, String salary, String email, String designation, String about)
    {
        this.employeeId = employeeId;
        this.image = image;
        this.name=name;
        this.gender=gender;
        this.contact=contact;
        this.address=address;
        this.salary=salary;
        this.email=email;
        this.designation=designation;
        this.about=about;
    }
    public String getEmployeeId() { return employeeId; }
    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public Long getContact() {
        return contact;
    }

    public String getSalary() {
        return salary;
    }

    public String getEmail() {
        return email;
    }

    public String getDesignation() {
        return designation;
    }

    public String getAbout() {
        return about;
    }


}
