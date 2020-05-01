package ro.ubbcluj.cs.ams.profile.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.ubbcluj.cs.ams.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
