package ro.ubbcluj.cs.ams.profile.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ro.ubbcluj.cs.ams.model.Customer;
import ro.ubbcluj.cs.ams.profile.service.CustomerService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/services")
public class ProfileController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private Logger logger = LogManager.getLogger(ProfileController.class);


    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('create_profile')")
    public Customer save(@RequestBody Customer customer) {
        return customerService.save(customer);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public Customer fetch(@RequestParam int profileId) {
        return customerService.fetchById(profileId);
    }

    @RequestMapping(value = "/profiles", method = RequestMethod.GET)
//    @PreAuthorize("hasAuthority('ROLE_operator')")
    public List<Customer> fetch(Principal principal) {

        logger.info(principal.getName());
        return customerService.fetchAllProfiles();
    }
}
