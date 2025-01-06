package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;

    @Autowired
    ServiceProviderRepository serviceProviderRepository3;

    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception {
        // Validate the country name
        try {
            CountryName countryEnum = CountryName.valueOf(countryName.toUpperCase());

            // Create new user object
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setConnected(false); // Initially not connected
            user.setMaskedIp(null); // No masked IP initially

            // Create the original country for the user
            Country country = new Country();
            country.setCountryNameFromEnum(countryEnum);
            country.setUser(user); // Associate the user with the country

            user.setOriginalIp(countryEnum.toCode() + "." + user.getId()); // Set the original IP address

            // Save the country and the user
            countryRepository3.save(country);  // Save the country first
            user.setOriginalCountry(country); // Assign the country to the user
            return userRepository3.save(user); // Save and return the user
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid country name"); // Throw exception if invalid country
        }
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        // Fetch the user by ID
        User user = userRepository3.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch the service provider by ID
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId)
                .orElseThrow(() -> new IllegalArgumentException("Service provider not found"));

        // Add the service provider to the user's list of subscribed providers
        user.getServiceProviderList().add(serviceProvider);

        // Save the updated user and return
        return userRepository3.save(user);
    }
}
