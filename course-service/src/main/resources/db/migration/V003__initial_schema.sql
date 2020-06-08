create table post
(
    id       serial,
    title    varchar(264)  not null,
    text     varchar(2056) not null,
    course_id varchar(16),
    constraint fk_course_id foreign key (course_id) references course (id)
);
