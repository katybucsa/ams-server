CREATE TABLE IF NOT EXISTS S_GROUP
(
    group_id serial,
    name     varchar(64) not null,
    spec_id  int         not null,
    year     int         not null,
    constraint uq_name_spec_id unique (name, spec_id),
    constraint pk_group primary key (group_id)
);

CREATE TABLE IF NOT EXISTS STUDENT
(
    user_id  varchar(64),
    group_id int not null,
    constraint fk_student_group_id foreign key (group_id) references S_GROUP (group_id),
    constraint pk_student primary key (user_id)
);

CREATE TABLE ENROLLMENT
(
    student_id varchar(64) not null,
    course_id  varchar(64) not null,
    constraint fk_enrollment_student_id foreign key (student_id) references STUDENT (user_id),
    constraint pk_enrollment primary key (student_id, course_id)
);
