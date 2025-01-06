package com.driver.model;

import javax.persistence.*;

@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String countryName; // Store the country name as a string

    @ManyToOne
    @JoinColumn(name = "service_provider_id", referencedColumnName = "id")
    private ServiceProvider serviceProvider;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Convert from CountryName enum to String for storage
    public void setCountryNameFromEnum(CountryName countryNameEnum) {
        this.countryName = countryNameEnum.name();
    }

    // Convert from String to CountryName enum for use
    public CountryName getCountryNameEnum() {
        return CountryName.valueOf(this.countryName);
    }
}
