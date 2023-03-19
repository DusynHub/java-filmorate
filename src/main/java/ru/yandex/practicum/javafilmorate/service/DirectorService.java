package ru.yandex.practicum.javafilmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.dao.DirectorDao;
import ru.yandex.practicum.javafilmorate.dao.MpaDao;
import ru.yandex.practicum.javafilmorate.model.Director;
import ru.yandex.practicum.javafilmorate.model.Mpa;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDao directorDao;

    public List<Director> getAllDirectorFromDb() {
        return directorDao.getAllDirectors();
    }

    public Director getDirectorFromDbById(long id) {

        return directorDao.getDirectorById(id);
    }

    public Director updateDirectorInDb(Director director) {
        return directorDao.updateDirectorInDb(director);
    }

    public Director createDirectorInDb(Director director) {
        return directorDao.createDirectorInDb(director);
    }

    public Director deleteDirectorInDb(long id) {
        return directorDao.deleteDirectorFromDb(id);
    }
}
