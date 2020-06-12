create table post
(
    id           serial,
    title        varchar(264)  not null,
    text         varchar(2056) not null,
    course_id    varchar(16),
    professor_id varchar(32),
    date         timestamp,
    type         varchar(16),
    constraint fk_course_id foreign key (course_id) references course (id),
    constraint pk_post primary key (id)
);

create table event
(
    id    serial,
    date  date,
    hour  time,
    place varchar(256),
    constraint fk_post_id foreign key (id) references post (id),
    constraint pk_event primary key (id)
);

create table participation
(
    event_id serial,
    user_id  varchar(32),
    constraint fk_event_id foreign key (event_id) references event (id),
    constraint pk_participant primary key (event_id, user_id)
);
