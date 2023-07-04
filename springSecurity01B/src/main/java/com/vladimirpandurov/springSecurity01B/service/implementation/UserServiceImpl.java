package com.vladimirpandurov.springSecurity01B.service.implementation;

import com.vladimirpandurov.springSecurity01B.domain.Role;
import com.vladimirpandurov.springSecurity01B.domain.User;
import com.vladimirpandurov.springSecurity01B.dto.UserDTO;
import com.vladimirpandurov.springSecurity01B.repository.RoleRepository;
import com.vladimirpandurov.springSecurity01B.repository.UserRepository;
import com.vladimirpandurov.springSecurity01B.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.vladimirpandurov.springSecurity01B.dtomapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        return mapToUserDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDTO(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        this.userRepository.sendVerificationCode(user);
    }

    @Override
    public UserDTO verifyCode(String email, String code) {
        return mapToUserDTO(userRepository.verifyCode(email,code));
    }

    private UserDTO mapToUserDTO (User user) {
        return fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }
}
