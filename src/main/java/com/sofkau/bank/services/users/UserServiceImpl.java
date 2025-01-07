package com.sofkau.bank.services.users;

import org.springframework.stereotype.Service;

import com.sofkau.bank.entities.User;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new AlreadyExistsException();

        return userRepository.save(user);
    }
}
