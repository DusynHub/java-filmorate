package ru.yandex.practicum.javafilmorate.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.javafilmorate.service.FeedService;

@Aspect
@Component
@Slf4j
public class FeedAspect {
    private final FeedService feedService;

    public FeedAspect(FeedService feedService) {
        this.feedService = feedService;
    }


    @AfterReturning(pointcut = "addLikeFilmControllerMethod() || deleteLikeFilmControllerMethod() " +
            "|| friendControllerMethod() || addReview() || updateReview() || deleteReview()"+
            "|| addLikeReviewController() || delLikeReviewController()"
            , returning = "val")
    public void afterOperationAspect(JoinPoint jp, Object val) {

        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object[] parameters = jp.getArgs();
        String methodName = methodSignature.getName();
        // проверка наличия объекта при добавлении ревью
        log.info("Подписка на событие : {}", methodName);
        if (val == null) {
            feedService.addFeed(methodName, parameters);
        } else {
        }
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
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.ReviewsController.deleteDislike(..))")
    private void deleteReview(){

    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.ReviewsController.likeReview(..))")
    private void addLikeReviewController(){

    }
    @Pointcut("execution(public * ru.yandex.practicum.javafilmorate.controllers.ReviewsController.dislikeReview(..))")
    private void delLikeReviewController(){

    }

}
