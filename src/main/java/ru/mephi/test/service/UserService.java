package ru.mephi.test.service;

import ru.mephi.test.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 */
public interface UserService {
    /**
     * Получить всех пользователей
     *
     * @return список всех пользователей
     */
    List<User> getAllUsers();

    /**
     * Получить пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return пользователь, если найден
     */
    Optional<User> getUserById(Long id);

    /**
     * Создать нового пользователя
     *
     * @param user пользователь для создания
     * @return созданный пользователь с ID
     */
    User createUser(User user);

    /**
     * Обновить существующего пользователя
     *
     * @param user пользователь для обновления
     * @return обновленный пользователь
     */
    User updateUser(User user);

    /**
     * Удалить пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь был удален
     */
    boolean deleteUser(Long id);
}