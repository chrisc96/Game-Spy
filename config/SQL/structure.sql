drop database if exists `game-spy-dev`;
create database `game-spy-dev`;
use `game-spy-dev`;

/*
 * Main game titles
 */
create table `game-titles` (
    `id` int primary key auto_increment,
    `name` varchar(128),
    `steam-id` int
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
    `website` varchar(128),
    `pc-requirements` text,
    `mac-requirements` text,
    `linux-requirements` text,
    `developers` varchar(64),
    `publishers` varchar(64),

    `price-currency` varchar(16),
    `price-initial` int,
    `price-final` int,
    `discount-percent` int,

    `platform-windows` boolean,
    `platform-mac` boolean,
    `platform-linux` boolean


);

/*
 * Keys
 */
alter table `game-titles`
    add constraint FK_SteamId
        foreign key(`steam-id`) references `steam-apps`(`id`);

alter table `steam-apps`
    add constraint FK_TitleId
        foreign key(`game-titles-id`) references `game-titles`(`id`);