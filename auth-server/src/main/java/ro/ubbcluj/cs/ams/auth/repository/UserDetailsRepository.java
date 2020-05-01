package ro.ubbcluj.cs.ams.auth.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ro.ubbcluj.cs.ams.auth.model.User;

import java.util.Optional;

public interface UserDetailsRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String name);

}
