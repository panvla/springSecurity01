package com.vladimirpandurov.springSecurity01B.service.implementation;

import com.vladimirpandurov.springSecurity01B.domain.User;
import com.vladimirpandurov.springSecurity01B.dto.UserDTO;
import com.vladimirpandurov.springSecurity01B.dtomapper.UserDTOMapper;
import com.vladimirpandurov.springSecurity01B.repository.UserRepository;
import com.vladimirpandurov.springSecurity01B.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;

    @Override
    public UserDTO createUser(User user) {
        return UserDTOMapper.fromUser(userRepository.create(user));
    }
}
