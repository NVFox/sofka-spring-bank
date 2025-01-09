package com.sofkau.bank.services.users;

import com.sofkau.bank.exceptions.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sofkau.bank.entities.User;
import com.sofkau.bank.exceptions.AlreadyExistsException;
import com.sofkau.bank.repositories.UserRepository;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new AlreadyExistsException();

        return userRepository.save(user);
    }

    @Override
    public User updateUser(String email, User user) {
        User stored = userRepository.findByEmail(email)
                .orElseThrow(NotFoundException::new);

        if (user.getPassword() != null)
            stored.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getName() != null)
            stored.setName(user.getName());

        if (Objects.equals(email, user.getEmail()))
            return userRepository.save(stored);

        if (userRepository.existsByEmail(user.getEmail()))
            throw new AlreadyExistsException();

        stored.setEmail(user.getEmail());

        return userRepository.save(stored);
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
}
