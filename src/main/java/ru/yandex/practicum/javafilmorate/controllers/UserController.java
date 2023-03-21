package ru.yandex.practicum.javafilmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addNewUser(@RequestBody @Valid User user) {
        log.info("Получен запрос 'POST /users'");
        return userService.addNewUserToStorage(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Получен запрос 'PUT /users'");
        return userService.updateUserInStorage(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Получен запрос 'GET /users'");
        return userService.getAllUsersFromStorage();
    }

    @GetMapping("/{id}")
    public User getUsero(@PathVariable long id) {
        log.info(String.format("Получен запрос 'GET /users/%d'", id));
        return userService.getUserFromStorage(id);
    }

    @DeleteMapping("/{userId}")
    public User deleteUserById(@PathVariable long userId) {
        log.info(String.format("получен запрос 'DELETE /users/%d", userId));
        return userService.deleteUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info(String.format("Получен запрос 'PUT /users/%d/friends/%d'", id, friendId));
        userService.addUsersToFriendsInStorage(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info(String.format("Получен запрос 'DELETE /users/%d/friends/%d'", id, friendId));
        userService.deleteUsersFromFriendsInStorage(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        log.info(String.format("Получен запрос 'GET /users/%d/friends'", id));
        return userService.getUserFriendsFromStorage(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getUsersCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info(String.format("Получен запрос 'GET /users/%d/friends/common/%d'", id, otherId));
        return userService.getUsersCommonFriendsFromStorage(id, otherId);
    }
}
