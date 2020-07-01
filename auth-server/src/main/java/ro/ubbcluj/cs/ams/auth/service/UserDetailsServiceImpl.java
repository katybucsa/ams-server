package ro.ubbcluj.cs.ams.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.ubbcluj.cs.ams.auth.model.AuthUserDetail;
import ro.ubbcluj.cs.ams.auth.model.User;
import ro.ubbcluj.cs.ams.auth.repository.UserDetailsRepository;

import java.util.Optional;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    private Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        Optional<User> optionalUser = userDetailsRepository.findByUsername(name);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username or password incorrect!"));

        UserDetails userDetails = new AuthUserDetail(optionalUser.get());
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;
    }

    public User findUserByUsername(String name) throws UsernameNotFoundException {

        Optional<User> optionalUser = userDetailsRepository.findByUsername(name);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username or password incorrect!"));

        return optionalUser.get();
    }
}
