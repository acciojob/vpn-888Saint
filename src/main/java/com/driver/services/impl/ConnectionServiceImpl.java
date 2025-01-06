package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Autowired
    UserRepository userRepository2;

    @Autowired
    ServiceProviderRepository serviceProviderRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Override
    public User connect(int userId, String countryName) throws Exception {
        // Fetch the user
        User user = userRepository2.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user is already connected
        if (Boolean.TRUE.equals(user.getConnected())) {
            throw new Exception("User is already connected to a VPN");
        }

        // If the user is in their original country, no need for VPN
        if (user.getOriginalCountry().getCountryName().equalsIgnoreCase(countryName)) {
            throw new Exception("User is already in their original country, no VPN required");
        }

        // Find the service provider for the specified country
        List<ServiceProvider> serviceProviders = user.getServiceProviderList();
        ServiceProvider suitableServiceProvider = null;
        Country desiredCountry = null;

        for (ServiceProvider serviceProvider : serviceProviders) {
            for (Country country : serviceProvider.getCountryList()) {
                if (country.getCountryName().equalsIgnoreCase(countryName)) {
                    suitableServiceProvider = serviceProvider;
                    desiredCountry = country;
                    break;
                }
            }
            if (suitableServiceProvider != null) break;
        }

        if (suitableServiceProvider == null || desiredCountry == null) {
            throw new Exception("No service provider available for the specified country");
        }

        // Create a new connection
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(suitableServiceProvider);

        connectionRepository.save(connection);

        // Update the user's masked IP and connection status
        String maskedIp = desiredCountry.getCountryName().substring(0, 3).toUpperCase() + "." + suitableServiceProvider.getId() + "." + user.getId();
        user.setMaskedIp(maskedIp);
        user.setConnected(true);

        userRepository2.save(user);

        return user;
    }

    @Override
    public User disconnect(int userId) throws Exception {
        // Fetch the user
        User user = userRepository2.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the user is already disconnected
        if (Boolean.FALSE.equals(user.getConnected())) {
            throw new Exception("User is already disconnected");
        }

        // Disconnect the user
        user.setConnected(false);
        user.setMaskedIp(null); // Reset the masked IP

        userRepository2.save(user);

        return user;
    }

    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        // Fetch sender and receiver
        User sender = userRepository2.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository2.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // If receiver is in a different country, establish a connection for the sender
        if (!receiver.getOriginalCountry().getCountryName().equalsIgnoreCase(sender.getOriginalCountry().getCountryName())) {
            String receiverCountryName = receiver.getOriginalCountry().getCountryName();
            return connect(senderId, receiverCountryName);
        }

        return sender; // No connection needed if both are in the same country
    }
}
