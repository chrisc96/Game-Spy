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
    `app-type` varchar(16)
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