drop database if exists `game-spy-dev`;
create database `game-spy-dev`;
use `game-spy-dev`;

/*
 * Main game titles
 */
create table `game-titles` (
    `id` int primary key auto_increment,
    `name` varchar(128),
    `steam-id` int,
    `origin-id` int,
    `uplay-id` int,
    `g2a-id` int
);

/*
 * Steam titles
 */
create table `steam-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int,
    `app-type` varchar(16) not null,

    `name` varchar(128),
    `required-age` int,
    `is-free` boolean,
    `detailed-description` text,
    `about-the-game` text,
    `short-description` varchar(128),
    `supported-languages` varchar(128),
    `header-image` varchar(128),
    `website` varchar(256),
    `pc-requirements` text,
    `mac-requirements` text,
    `linux-requirements` text,

    `price-currency` varchar(16),
    `price-initial` int,
    `price-final` int,
    `discount-percent` int,

    `platform-windows` boolean,
    `platform-mac` boolean,
    `platform-linux` boolean
);

create table `origin-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int
);

create table `uplay-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int
);

create table `g2a-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int
);

create table `media-game-listings` (
    `id` int primary key auto_increment,
    `game-titles-id` int,
    `url` varchar(256),
    `platform-id` int
);

create table `platforms` (
    `id` int primary key auto_increment,
    `name` varchar(32)
);

create table `developers` (
     `id` int primary key auto_increment,
     `name` varchar(128)
);

create table `publishers` (
     `id` int primary key auto_increment,
     `name` varchar(128)
);

create table `games-developers` (
      `game-id` int NOT null,
      `dev-id` int NOT null
);

create table `games-publishers` (
      `game-id` int NOT null,
      `publisher-id` int NOT null
);

/*
 * Keys
 */
alter table `game-titles`
    add constraint FK_SteamId
        foreign key(`steam-id`) references `steam-apps`(`id`),
    add constraint FK_OriginId
        foreign key(`origin-id`) references `origin-apps`(`id`),
    add constraint FK_UplayId
        foreign key(`uplay-id`) references `uplay-apps`(`id`),
    add constraint FK_G2aId
        foreign key(`g2a-id`) references `g2a-apps`(`id`);

alter table `steam-apps`
    add constraint FK_Steam_GameId
        foreign key(`game-titles-id`) references `game-titles`(`id`);

alter table `media-game-listings`
    add constraint FK_Media_GameId
        foreign key(`game-titles-id`) references `game-titles`(`id`),
    add constraint FK_PlatformId
        foreign key(`platform-id`) references `platforms`(`id`);


alter table `games-developers`
    add constraint PK_Developer
        primary key(`game-id`, `dev-id`),
    add constraint FK_Dev_GameId
        foreign key(`game-id`) references `game-titles`(`id`),
    add constraint FK_DevId
        foreign key(`dev-id`) references `developers`(`id`);

alter table `games-publishers`
    add constraint PK_Publisher
        primary key(`game-id`, `publisher-id`),
    add constraint FK_Pub_GameId
        foreign key(`game-id`) references `game-titles`(`id`),
    add constraint FK_PubId
        foreign key(`publisher-id`) references `publishers`(`id`);
