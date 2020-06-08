package ro.ubbcluj.cs.ams.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ro.ubbcluj.cs.ams.auth.config.AuthConfiguration;
import ro.ubbcluj.cs.ams.auth.model.AuthUserDetail;
import ro.ubbcluj.cs.ams.auth.model.User;
import ro.ubbcluj.cs.ams.auth.repository.UserDetailsRepository;

import java.util.Optional;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthConfiguration authProps;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    private Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        Optional<User> optionalUser = userDetailsRepository.findByUsername(name);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username or password incorrect!"));

        UserDetails userDetails = new AuthUserDetail(optionalUser.get());
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;
    }

//    public Principal authenticateUser(UserDto userDto) {
//
//        logger.info("==========authenticateUser==========");
//        //org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(authProps.getUser(), authProps.getPass(), Collections.emptyList());
//        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword());
//        Authentication authResp = authenticationManager.authenticate(authReq);
//        if (authResp == null || !authResp.isAuthenticated()) {
//            logger.error("==========authenticateUser failed==========");
//            logger.error("Invalid credentials provided");
//            throw new InternalAuthenticationServiceException("Unable to authenticate user for provided credentials");
//        }
//        logger.info("==========authenticateUser successful==========");
//        return authResp;
//    }
}
