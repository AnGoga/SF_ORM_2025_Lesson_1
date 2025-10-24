package ru.mephi.test.repository;

import ru.mephi.test.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с пользователями
 */
public interface UserRepository {
    /**
     * Найти всех пользователей
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Найти пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return пользователь, если найден
     */
    Optional<User> findById(Long id);

    /**
     * Сохранить пользователя
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь с установленным ID
     */
    User save(User user);

    List<User> saveAll(List<User> users);

    /**
     * Обновить пользователя
     *
     * @param user пользователь для обновления
     * @return обновленный пользователь
     */
    User update(User user);

    /**
     * Удалить пользователя по ID
     *
     * @param id идентификатор пользователя
     * @return true, если пользователь был удален
     */
    boolean deleteById(Long id);
}