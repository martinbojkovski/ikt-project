package com.iktproject.service.impl;

import com.iktproject.model.User;
import com.iktproject.repository.UserRepository;
import com.iktproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(Long id,User user) {
        User userToUpdate = userRepository.findById(id).orElse(null);
        if (userToUpdate != null) {
//            userToUpdate.setUsername(user.getUsername());
//            userToUpdate.setPassword(user.getPassword());
            
            userToUpdate.setName(user.getName());
            userToUpdate.setSurname(user.getSurname());
            userToUpdate.setGrade_average(user.getGrade_average());
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
        userRepository.delete(user);
        }
    }
}
