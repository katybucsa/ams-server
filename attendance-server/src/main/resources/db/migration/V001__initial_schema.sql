create table attendance_info (
  id serial,
  course_id int,
  activity_id int,
  professor_id varchar(128),
  created_at timestamp,
  remaining_time int, --minutes
  constraint pk_attendance_info primary key (id)
);

create table attendance (
  id serial,
  student_id varchar(128),
  created_at timestamp,
  attendance_info_id serial,
  constraint pk_attendance primary key (id),
  constraint fk_attendance_info foreign key (attendance_info_id) references attendance_info(id)
);
