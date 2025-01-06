package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        // Create an Admin object
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);

        // Save the admin and return the saved object
        return adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        // Fetch admin by ID
        Admin admin = adminRepository1.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Create new ServiceProvider and associate it with the admin
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        // Save the service provider and associate with admin
        serviceProviderRepository1.save(serviceProvider);

        // Add the new service provider to the admin's list
        admin.getServiceProviders().add(serviceProvider);

        // Save the updated admin and return
        return adminRepository1.save(admin);
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception {
        // Fetch the service provider by ID
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).orElseThrow(() -> new IllegalArgumentException("Service Provider not found"));

        // Check if the countryName is valid (ind, aus, usa, chi, jpn)
        try {
            CountryName countryEnum = CountryName.valueOf(countryName.toUpperCase());

            // Create a new Country object
            Country country = new Country();
            country.setCountryNameFromEnum(countryEnum);
            country.setServiceProvider(serviceProvider); // Associate the country with the service provider

            // Save the country to the repository
            countryRepository1.save(country);

            // Add country to the service provider's list
            serviceProvider.getCountryList().add(country);

            // Save the service provider and return
            return serviceProviderRepository1.save(serviceProvider);
        } catch (IllegalArgumentException e) {
            throw new Exception("Country not found"); // Invalid country name
        }
    }
}
