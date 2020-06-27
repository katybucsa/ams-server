CREATE TABLE IF NOT EXISTS GRADE (
  grade_id serial,
  type_id int,
  teacher VARCHAR(64) NOT NULL,
  student VARCHAR(64) NOT NULL,
  value DOUBLE PRECISION NOT NULL,
  course_id VARCHAR(64) NOT NULL,
  date TIMESTAMP,
  constraint pk_grade primary key (grade_id)
);

