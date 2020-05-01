create table activity_type
(
    type_id serial,
    name    varchar(512) not null,
    constraint pk_activity_type primary key (type_id)
);

create table sp_link
(
    subject_id VARCHAR(16) NOT NULL,
    type_id    INT         NOT NULL,
    user_id    VARCHAR(50) NOT NULL,
    constraint fk1_sp_link foreign key (subject_id) references subject (id),
    constraint fk2_sp_link foreign key (type_id) references activity_type (type_id),
    constraint pk_sp_link primary key (subject_id, type_id, user_id)
);
