create table specialization (
  id serial,
  name varchar(128),
  language varchar(64),
  constraint pk_specialization_id primary key (id)
);

create table course (
  id varchar(16),
  name varchar(128),
  credits serial,
  spec_id serial,
  year serial,
  constraint pk_course_id primary key (id),
  constraint fk_specialization_id foreign key (spec_id) references specialization (id)
);

create table course_code (
  code serial,
  course_name varchar(64) not null,
  constraint pk_course_code_id primary key (code)
);
