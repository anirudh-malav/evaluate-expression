package com.evaluate.service.impl;

import com.evaluate.common.exceptions.AuthenticationFailed;
import com.evaluate.model.UserDetails;
import com.evaluate.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class UserService{

    @Autowired
    UserDetailsRepository userDetailsRepository;

    public UserDetails validateUser(String email, String password) {
        UserDetails userDetails = userDetailsRepository.validateUser(email, password);
        if (Objects.isNull(userDetails)) {
            throw new AuthenticationFailed("User Authentication Fail | UserEmail : " + email);
        }
        return userDetails;
    }

}
