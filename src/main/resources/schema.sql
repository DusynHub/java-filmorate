drop table if exists FILM cascade;
drop table if exists GENRE cascade;
drop table if exists MPA cascade;
drop table if exists LIKES cascade;
drop table if exists USERS cascade;
drop table if exists FILM_GENRE cascade;
drop table if exists FRIENDSHIP cascade;
drop table if exists REVIEWS cascade;
drop table if exists FEED_LIST cascade;
drop table if exists DIRECTOR cascade;
drop table if exists FILM_DIRECTOR cascade;

create table IF NOT EXISTS GENRE
(
    ID   BIGINT not null auto_increment,
    NAME CHARACTER VARYING(250),
    constraint GENRE_PK
        primary key (ID)
);

create unique index IF NOT EXISTS GENRE_ID_UINDEX
    on GENRE (ID);

create table IF NOT EXISTS MPA
(
    ID   BIGINT                 not null,
    NAME CHARACTER VARYING(250) not null,
    constraint MPA_PK
        primary key (ID)
);

create table IF NOT EXISTS FILM
(
    ID           BIGINT                 auto_increment,
    NAME         CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    MPA          BIGINT,
    RATE         INTEGER,
    LIKES_AMOUNT INTEGER,
    constraint FILM_PK
        primary key (ID),
    constraint MPA_FK
        foreign key (MPA) references MPA
            on update set null on delete set null
);

create unique index IF NOT EXISTS FILM_ID_UINDEX
    on FILM (ID);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  BIGINT not null,
    GENRE_ID BIGINT not null,
    constraint FILM_GENRE_PK
        primary key (FILM_ID, GENRE_ID),
    constraint FILM_FK
        foreign key (FILM_ID) references FILM
            on update cascade on delete cascade,
    constraint GENRE_FK
        foreign key (GENRE_ID) references GENRE
            on update cascade on delete cascade
);

create unique index IF NOT EXISTS FILM_GENRE_ID_UINDEX
    on FILM_GENRE (FILM_ID, GENRE_ID);

create unique index IF NOT EXISTS MPA_ID_UINDEX
    on MPA (ID);

create table IF NOT EXISTS USERS
(
    ID       BIGINT                 auto_increment,
    EMAIL    CHARACTER VARYING(250) not null,
    LOGIN    CHARACTER VARYING(250) not null,
    NAME     CHARACTER VARYING(250),
    BIRTHDAY DATE,
    constraint USER_PK
        primary key (ID)
);

create table IF NOT EXISTS FRIENDSHIP
(
    FRIEND1_ID BIGINT not null,
    FRIEND2_ID BIGINT not null,
    constraint FRIENDSHIP_PK
        primary key (FRIEND1_ID, FRIEND2_ID),
    constraint FRIENDSHIP_USER1_FK
        foreign key (FRIEND1_ID) references USERS
            on update cascade on delete cascade,
    constraint FRIENDSHIP_USER2_FK
        foreign key (FRIEND2_ID) references USERS
            on update cascade on delete cascade
);

create unique index IF NOT EXISTS FRIENDSHIP_ID_UINDEX
    on FRIENDSHIP (FRIEND1_ID, FRIEND2_ID);

create table IF NOT EXISTS LIKES
(
    FILM_ID BIGINT not null,
    USER_ID BIGINT not null,
    constraint LIKES_PK
        primary key (FILM_ID, USER_ID),
    constraint LIKES_FILM_FK
        foreign key (FILM_ID) references FILM
            on update cascade on delete cascade,
    constraint LIKES_USER_FK
        foreign key (USER_ID) references USERS
            on update cascade on delete cascade
);

create unique index IF NOT EXISTS LIKES_ID_UINDEX
    on LIKES (FILM_ID, USER_ID);

create unique index IF NOT EXISTS USER_ID_UINDEX
    on USERS (ID);

create table IF NOT EXISTS REVIEWS
(
    ID           BIGINT not null auto_increment,
    FILM_ID      BIGINT not null,
    USER_ID      BIGINT not null,
    CONTENT      CHARACTER VARYING,
    IS_POSITIVE  BOOLEAN,
    USEFUL       INTEGER,
    constraint REVIEWS_PK
        primary key (ID),
    constraint REVIEWS_FK
        foreign key (FILM_ID) references FILM
            on update cascade on delete cascade,
    constraint REVIEWS_FK2
        foreign key (USER_ID) references USERS
            on update cascade on delete cascade
);


create unique index REVIEWS_ID_UINDEX
    on REVIEWS (ID);

create table IF NOT EXISTS event_type
(
    event_type ENUM ('LIKE', 'REVIEW', 'FRIEND')
);

create table IF NOT EXISTS operation_type
(
    operation_type ENUM ('ADD', 'REMOVE', 'UPDATE')
);

create table IF NOT EXISTS  FEED_LIST
(
    EVENT_ID   BIGINT not null auto_increment,
    USER_ID    BIGINT not null,
    EVENT_TYPE  ENUM ('LIKE', 'REVIEW', 'FRIEND'),
    OPERATION_TYPE ENUM ('ADD', 'REMOVE', 'UPDATE'),
    ENTITY_ID BIGINT not null,
    FEED_DATE TIMESTAMP,
    constraint FEED_LIST_PK
        primary key (EVENT_ID),
    constraint FEED_LIST_FK
        foreign key (USER_ID) references USERS
            on update cascade on delete cascade
);

create unique index IF NOT EXISTS FEED_LIST_ID_UINDEX
    on FEED_LIST (EVENT_ID);

create table IF NOT EXISTS  DIRECTOR
(
    ID   BIGINT not null auto_increment,
    NAME CHARACTER VARYING(250),
    constraint DIRECTOR_PK
        primary key (ID)
);

create unique index IF NOT EXISTS DIRECTOR_ID_UINDEX
    on DIRECTOR (ID);

create table IF NOT EXISTS FILM_DIRECTOR
(
    FILM_ID BIGINT not null,
    DIRECTOR_ID BIGINT not null,
    constraint FILM_DIRECTOR_PK
        primary key (FILM_ID, DIRECTOR_ID),
    constraint FILM_DIRECTOR_FILM_FK
        foreign key (FILM_ID) references FILM
            on update cascade on delete cascade,
    constraint FILM_DIRECTOR_FK
        foreign key (DIRECTOR_ID) references DIRECTOR
            on update cascade on delete cascade
);

create unique index IF NOT EXISTS FILM_DIRECTOR_ID_UINDEX
    on FILM_DIRECTOR (FILM_ID, DIRECTOR_ID);