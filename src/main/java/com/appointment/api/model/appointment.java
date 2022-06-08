package com.appointment.api.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
//import javax.validation.constraints.Email;


import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Entity
@Table(name = "appointment")
public class appointment extends AuditModel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "service", nullable = false)
    private String service;

    @Column(name = "schedule_time", nullable = false)

    private Date schedule_time;

    @Column(name = "status",nullable =false)
    private String status="pending";

    public appointment() {
        super();
    }

    public appointment(String firstName, String lastName, String phoneNumber,
            String service, Date schedule_time, String accountnumber) {
        super();

        this.firstName = firstName;
        this.lastName = lastName;

        this.phoneNumber = phoneNumber;

        this.service = service;
        this.schedule_time = schedule_time;

    }

    public Long getId() {
        return id;
    }

    public String getservice() {
        return service;
    }

    public void setocccupation(String service) {
        this.service = service;
    }

    public Date getschedule_time() {
        return schedule_time;
    }

    public void setschedule_time(Date schedule_time) {
        this.schedule_time = schedule_time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public appointment orElseThrow(Object object) {
        return null;
    }

}
