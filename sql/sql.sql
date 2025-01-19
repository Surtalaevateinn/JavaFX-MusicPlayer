create table if not exists Albums
(
    id           int auto_increment constraint `PRIMARY`
    primary key,
    title        varchar(255) not null,
    artistId     int          not null,
    artwork_path varchar(255) null
    );

create index artistId
    on Albums (artistId);

create table if not exists Artists
(
    id         int auto_increment constraint `PRIMARY`
    primary key,
    name       varchar(255) not null,
    image_path varchar(255) null,
    constraint name
    unique (name)
    );

create table if not exists Songs
(
    id          int auto_increment constraint `PRIMARY`
    primary key,
    title       varchar(255)  not null,
    length      int           not null,
    trackNumber int           null,
    discNumber  int           null,
    playCount   int default 0 null,
    playDate    datetime      null,
    location    text          not null,
    albumId     int           not null
    );

create index albumId
    on Songs (albumId);

