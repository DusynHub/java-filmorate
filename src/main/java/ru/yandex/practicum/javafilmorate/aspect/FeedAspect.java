package ru.yandex.practicum.javafilmorate.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.service.FeedService;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class FeedAspect {
    private final FeedService feedService;

    @AfterReturning(pointcut = "addLikeFilmControllerMethod() || deleteLikeFilmControllerMethod() " +
            "|| friendControllerMethod() || addReview() || updateReview()"
            , returning = "val")
    public void afterOperationAspect(JoinPoint jp, Object val) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object[] parameters = jp.getArgs();
        String methodName = methodSignature.getName();
        log.info("Подписка на событие после: {}", methodName);
        feedService.addFeed(methodName, parameters);
    }

    @Before("deleteReview()")
    public void beforeDeleteReviewAspect(JoinPoint jp) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object[] parameters = jp.getArgs();
        String methodName = methodSignature.getName();
        log.info("Подписка на событие перед: {}", methodName);
        feedService.addFeed(methodName, (Long) parameters[0]);
    }

    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.UserController.*Friend(..))")
    private void friendControllerMethod() {
    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.FilmController.likeFilm(..))")
    private void addLikeFilmControllerMethod() {
    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.FilmController.deleteLikeFromFilm(..))")
    private void deleteLikeFilmControllerMethod() {
    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.ReviewsController.addReview(..))")
    private void addReview(){

    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.ReviewsController.updateReview(..))")
    private void updateReview(){

    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.ReviewsController.deleteReview(..))")
    private void deleteReview(){

    }

}
