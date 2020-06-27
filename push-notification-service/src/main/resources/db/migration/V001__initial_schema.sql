create table subscription_keys
(
    id     serial,
    p256dh varchar(512) not null,
    auth   varchar(512) not null,
    constraint pk_subscription_keys primary key (id)
);

create table subscription
(
    id              serial,
    username        varchar(64),
    user_role       varchar(32),
    endpoint        varchar(1028) unique,
    expiration_time timestamp,
    subs_id         serial,
    constraint fk_subscription_keys foreign key (subs_id) references subscription_keys,
    constraint pk_subscription primary key (id)
);

create table notification
(
    id        serial,
    post_id   int,
    course_id varchar(16),
    type      varchar(16),
    title     varchar(256),
    body      varchar(2056),
    date      timestamp,
    constraint pk_notification primary key (id)
);

create table user_notif
(
    user_id   varchar(64),
    notif_id  serial,
    post_id   int,
    user_role varchar(32),
    read      bool,
    seen      bool,
    constraint fk_notification foreign key (notif_id) references notification (id),
    constraint pk_user_notif primary key (user_id, notif_id, post_id)
);
