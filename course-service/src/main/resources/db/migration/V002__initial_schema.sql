create table activity_type
(
    type_id serial,
    name    varchar(512) not null,
    constraint pk_activity_type primary key (type_id)
);

create table cp_link
(
    course_id VARCHAR(16) NOT NULL,
    type_id    INT         NOT NULL,
    user_id    VARCHAR(50) NOT NULL,
    constraint fk1_cp_link foreign key (course_id) references course (id),
    constraint fk2_cp_link foreign key (type_id) references activity_type (type_id),
    constraint pk_sp_link primary key (course_id, type_id, user_id)
);
