drop database if exists `game-spy-dev`;
create database `game-spy-dev`;
use `game-spy-dev`;

/*
 * Main game titles
 */
create table `game-titles` (
    `id` int primary key auto_increment,
    `app-type-id` int not null,
    `name` varchar(128),
    `steam-id` int DEFAULT null,
    `origin-id` int DEFAULT null,
    `uplay-id` int DEFAULT null,
    `g2a-id` int DEFAULT null
);

/*
 * Steam titles
 */
create table `steam-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int NOT null,

    `name` varchar(128),
    `detailed-description` text,
    `about-the-game` text,
    `short-description` varchar(128),

    `website` varchar(256), -- i.e link to developer page
    `referral-url` varchar(256), -- i.e store.steampowered.com/game
    `header-image` varchar(128),

    `required-age` int,
    `is-free` boolean,
    `supported-languages` varchar(128),
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

/*
  Origin titles
*/
create table `origin-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int NOT null
);

/*
  Uplay titles
*/
create table `uplay-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int NOT null
);

/*
  G2A titles
*/
create table `g2a-apps` (
    `id` int primary key auto_increment,
    `game-titles-id` int NOT null,

    `in-stock` boolean,
    `quantity-in-stock` int,
    `header-image` varchar(128),
    `referral-url` varchar(256), -- format is https://www.g2a.com + referral-url

    `detailed-description` text,
    `about-the-game` text,
    `short-description` varchar(128),

    `cheapest-price` float,
    `cheapest-price-with-shield` float, -- shield is g2a's protection server. Includes a fee
    `platform-id` int NOT null -- if the key is for steam activation/uplay/etc
);

/*
    Media screenshots/videos links for game titles
*/
create table `media-game-listings` (
    `id` int primary key auto_increment,
    `game-titles-id` int NOT null,
    `url` varchar(256),
    `platform-id` int
);

/*
    Table for games that have a specific platform activation
    methods and for media that comes from specfic platforms
*/
create table `platforms` (
    `id` int primary key auto_increment,
    `name` varchar(32),
    `data-preference` int not null -- we choose what listing data to show via this value (lowest selected first)
);

/*
    Game developers table
*/
create table `developers` (
     `id` int primary key auto_increment,
     `name` varchar(128)
);

/*
    Game publishers table
*/
create table `publishers` (
     `id` int primary key auto_increment,
     `name` varchar(128)
);

/*
    Table for each games regions (if locked)
*/
create table `regions` (
    `id` int primary key auto_increment,
    `g2a-app-id` int NOT null,
    -- include other FK id references here if the key is region locked
    `name` varchar(128)
);

/*
    Game listing type (bundle, game, dlc etc)
*/
create table `app-types` (
    `id` int primary key auto_increment,
    `game-titles-id` int NOT null
);

/*
  Junction table for game-titles and developer table
*/
create table `games-developers` (
      `game-id` int NOT null,
      `dev-id` int NOT null
);

/*
  Junction table for game-titles and publisher table
*/
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

alter table `g2a-apps`
    add constraint FK_G2a_Platform
        foreign key(`platform-id`) references `platforms`(`id`),
    add constraint FK_G2a_title_id
        foreign key(`game-titles-id`) references `game-titles`(`id`);

alter table `regions`
    add constraint FK_G2a_Regions
        foreign key(`g2a-app-id`) references `g2a-apps`(`id`);

alter table `app-types`
    add constraint FK_AppTypes
        foreign key(`game-titles-id`) references `game-titles`(`id`);
