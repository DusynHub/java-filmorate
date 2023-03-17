package ru.yandex.practicum.javafilmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.javafilmorate.dao.FriendShipDao;
import ru.yandex.practicum.javafilmorate.dao.LikeDao;
import ru.yandex.practicum.javafilmorate.exceptions.EntityDoesNotExistException;
import ru.yandex.practicum.javafilmorate.model.Film;
import ru.yandex.practicum.javafilmorate.model.Friendship;
import ru.yandex.practicum.javafilmorate.model.Like;
import ru.yandex.practicum.javafilmorate.model.User;
import ru.yandex.practicum.javafilmorate.storage.UserStorage;
import ru.yandex.practicum.javafilmorate.util.StringValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendShipDao friendShipDao;
    private final LikeDao likeDao;
    private final FilmService filmService;


    public UserService(@Qualifier("userStorageDb") UserStorage userStorage, FriendShipDao friendShipDao, LikeDao likeDao, FilmService filmService) {
        this.userStorage = userStorage;
        this.friendShipDao = friendShipDao;
        this.likeDao = likeDao;
        this.filmService = filmService;
    }

    public User addNewUserToStorage(User user) {
        setUserNameIfNeeded(user);
        return userStorage.addUser(user);
    }

    public List<User> getAllUsersFromStorage() {

        List<User> users = userStorage.getAllUsers();
        List<Friendship> allFriendship = friendShipDao.getAllFriendship();

        Map<Long, User> allUsers = userStorage.getAllUsers()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<Long, List<User>> mappedUsers = new HashMap<>();

        for (Friendship friendship : allFriendship) {
            if (!mappedUsers.containsKey(friendship.getFriend1Id())) {
                mappedUsers.put(friendship.getFriend1Id(), new ArrayList<>());
            }
            mappedUsers.get(friendship.getFriend1Id()).add(allUsers.get(friendship.getFriend2Id()));
        }
        users.forEach(user -> user.setFriends(mappedUsers.get(user.getId())));
        return users;
    }

    public User updateUserInStorage(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserFromStorage(long id) {
        return userStorage.getUser(id);
    }

    public User deleteUserById(Long id) {
        checkIsUserIdNegative(id);
        return userStorage.removeUser(id);
    }

    public void addUsersToFriendsInStorage(long id, long friendId) {
        checkIsUserIdNegative(id);
        checkIsUserIdNegative(friendId);
        userStorage.getUser(id);
        userStorage.getUser(friendId);
        friendShipDao.addFriends(id, friendId);
    }

    public void deleteUsersFromFriendsInStorage(long id, long friendId) {
        checkIsUserIdNegative(id);
        checkIsUserIdNegative(friendId);
        friendShipDao.deleteFriends(id, friendId);
    }

    public List<User> getUserFriendsFromStorage(long id) {
        return friendShipDao.getUserFriends(id);
    }

    public List<User> getUsersCommonFriendsFromStorage(long id, long otherId) {
        checkIsUserIdNegative(id);
        checkIsUserIdNegative(otherId);
        return friendShipDao.getCommonFriends(id, otherId);
    }

    private void setUserNameIfNeeded(User user) {

        if (StringValidator.isNullOrEmpty(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public void checkIsUserIdNegative(long id) {
        if (id < 0) {
            log.debug("Пользователь с отрицательным id {} не может существовать.", id);
            throw new EntityDoesNotExistException(
                    String.format("Пользователь с отрицательным id %d не может существовать.", id));
        }
    }

    public List<Film> getUserRecommendationsFromStorage(long id) {
        checkIsUserIdNegative(id);
        userStorage.getUser(id);
        List<Like> userLikes = likeDao.getUserLikesById(id);

        if (userLikes.size() == 0) {
            return new ArrayList<>();
        }

        List<Long> filmsIdLikedByUser = userLikes.stream()
                .map(Like::getFilmId)
                .collect(Collectors.toList());

        List<Like> firstUserWithSameLikedFilms
                = likeDao.getFirstUserWithSameLikedFilms(filmsIdLikedByUser, id);

        if (firstUserWithSameLikedFilms.size() == 0) {
            return new ArrayList<>();
        }

        long firstUserIdWithSameLiked = firstUserWithSameLikedFilms.get(0).getUserId();

        return likeDao.getUserLikesById(firstUserIdWithSameLiked)
                                .stream()
                                .map(Like::getFilmId)
                                .filter(like -> !filmsIdLikedByUser.contains(like))
                                .map(filmService::getFilmFromStorage)
                                .collect(Collectors.toList());
    }


}
