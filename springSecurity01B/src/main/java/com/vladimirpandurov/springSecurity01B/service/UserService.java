package com.vladimirpandurov.springSecurity01B.service;

import com.vladimirpandurov.springSecurity01B.domain.User;
import com.vladimirpandurov.springSecurity01B.dto.UserDTO;

import java.net.URLConnection;

public interface UserService {

    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

    UserDTO verifyCode(String email, String code);
}
