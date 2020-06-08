insert into s_group
values (default, '211', 1, 1),
       (default, '212', 1, 1),
       (default, '213', 1, 1),
       (default, '214', 1, 1),
       (default, '215', 1, 1),
       (default, '216', 1, 1),
       (default, '217', 1, 1),
       (default, '221', 1, 2),
       (default, '222', 1, 2),
       (default, '223', 1, 2),
       (default, '224', 1, 2),
       (default, '225', 1, 2),
       (default, '226', 1, 2),
       (default, '227', 1, 2),
       (default, '231', 1, 3),
       (default, '232', 1, 3),
       (default, '233', 1, 3),
       (default, '234', 1, 3),
       (default, '235', 1, 3),
       (default, '236', 1, 3),
       (default, '237', 1, 3);

/* NOT GOOD */
insert into student
values ('katy', 15),
       ('bogdan', 18),
       ('magda', 20);

insert into enrollment
values ('katy', 'so'),
       ('bogdan', 'bd'),
       ('magda', 'so'),
       ('katy', 'bd'),
       ('katy', 'map');
