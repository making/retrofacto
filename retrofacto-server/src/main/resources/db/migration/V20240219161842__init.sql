create table boards
(
    name       varchar(255) not null,
    passphrase varchar(255),
    slug       varchar(255) not null unique,
    id         bytea        not null,
    primary key (id)
);

create table cards
(
    done       boolean                     not null,
    likes      integer                     not null,
    version    integer                     not null,
    text       varchar(255)                not null,
    column_id  bytea                       not null,
    id         bytea                       not null,
    primary key (id)
);

create table columns
(
    color    varchar(255) not null,
    emoji    varchar(255) not null,
    title    varchar(255) not null,
    board_id bytea        not null,
    id       bytea        not null,
    primary key (id)
);

alter table if exists cards
    add constraint cards_columns foreign key (column_id) references columns;
alter table if exists columns
    add constraint columns_board foreign key (board_id) references boards;