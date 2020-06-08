package ro.ubbcluj.cs.ams.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.auth.model.AuthClientDetails;

import java.util.Optional;

//@Repository
public interface ClientDetailsRepository extends JpaRepository<AuthClientDetails, String> {

    Optional<AuthClientDetails> findByClientId(String clientId);
}
