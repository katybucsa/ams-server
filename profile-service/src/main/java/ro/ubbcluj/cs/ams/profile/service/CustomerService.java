package ro.ubbcluj.cs.ams.profile.service;



import ro.ubbcluj.cs.ams.model.Customer;

import java.util.List;

public interface CustomerService {

    Customer save(Customer customer);

    Customer fetchById(Integer profileId);

    List<Customer> fetchAllProfiles();
}
