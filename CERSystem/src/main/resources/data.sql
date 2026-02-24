TRUNCATE TABLE users;
/*Dummy records for table users to test if authentication works*/
INSERT IGNORE INTO users (name, email, password, role, university_id) VALUES
('Mohammed Adil',  'ma3067@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'ADMIN', '415006855'),
('Osama Ahmed',    'oja5093@rit.edu',    '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '764000269'),
('Abdullah Kair',  'ahk3336@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '377003755'),
('Jason Venkataraghavan',  'jnv8919@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '757001356'),
('Ahmed Almarri',  'aaa8902@rit.edu',   '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '769002024');
       /*The password is "password" it needs to stored BCryped form, it uses 12 rounds of hashing. */