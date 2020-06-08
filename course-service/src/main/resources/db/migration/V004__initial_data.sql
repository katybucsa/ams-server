insert into "course-db".public.activity_type (name)
values ('lab');

insert into course_code (course_name)
values ('Sisteme de operare');

insert into "course-db".public.specialization (name, language)
values ('info', 'romana');

insert into course (id, name, credits, spec_id, year)
values ('so', 'Sisteme de operare', 6, 1, 1),
       ('bd', 'Baze de date', 5, 1, 2),
       ('map', 'Metode avansate de programare', 5,1, 2),
       ('sd', 'Sisteme dinamice', 5,1, 1);

insert into cp_link (course_id, type_id, user_id)
values ('so', 1, 'dana'),
       ('bd', 1, 'dana'),
       ('map', 1, 'dana'),
       ('sd', 1, 'dana');
