package ru.mephi.test.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mephi.test.exceptions.AppException;
import ru.mephi.test.model.User;
import ru.mephi.test.repository.UserRepository;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("Новый пользователь не должен иметь ID");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Обновляемый пользователь должен иметь ID");
        }

        if (userRepository.findById(user.getId()).isEmpty()) {
            throw new AppException("Пользователь с ID " + user.getId() + " не найден");
        }

        return userRepository.update(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userRepository.deleteById(id);
    }
}