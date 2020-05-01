create table specialization (
  id serial,
  name varchar(128),
  language varchar(64),
  constraint pk_specialization_id primary key (id)
);

create table subject (
  id varchar(16),
  name varchar(128),
  credits serial,
  spec_id serial,
  year serial,
  constraint pk_subject_id primary key (id),
  constraint fk_specialization_id foreign key (spec_id) references specialization (id)
);

create table subject_code (
  code serial,
  subject_name varchar(64) not null,
  constraint pk_subject_code_id primary key (code)
);