
/*Dummy records for table users to test if authentication works*/
INSERT IGNORE INTO users (name, email, password, role, university_id) VALUES
('Mohammed Adil',  'ma3067@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'ORGANIZER', '415006855'),
('Osama Ahmed',    'oja5093@rit.edu',    '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'ADMIN',  '764000269'),
('Abdullah Kair',  'ahk3336@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '377003755'),
('Jason Venkataraghavan',  'jnv8919@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '757001356'),
('Ahmed Almarri',  'aaa8902@rit.edu',   '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'USER',  '769002024');
       /*The password is "password" it needs to stored BCryped form, it uses 12 rounds of hashing. */

INSERT IGNORE INTO events (title, description, scheduled_date, category, location, registration_start, registration_end, start_time, end_time, status, organizer_id) VALUES
('Spring Boot Workshop',      'Hands-on introduction to Spring Boot and REST APIs.',         '2026-04-01', 'Workshop', 'Building H, Room 101', '2026-03-15 08:00:00', '2026-03-31 23:59:59', '2026-04-01 10:00:00', '2026-04-01 12:00:00', 'SCHEDULED', 1),
('AI and Machine Learning',   'Overview of machine learning trends and tools.',              '2026-04-03', 'Talk',     'Building B, Room 204', '2026-03-15 08:00:00', '2026-04-02 23:59:59', '2026-04-03 14:00:00', '2026-04-03 16:00:00', 'SCHEDULED', 1),
('Campus Clubs',       'Networking social event open to all students.',               '2026-04-05', 'Social',   'Innovation Centre',   '2026-03-20 08:00:00', '2026-04-04 23:59:59', '2026-04-05 18:00:00', '2026-04-05 21:00:00', 'SCHEDULED', 1),
('Data Structures Review',    'Review session covering core CS data structure concepts.',    '2026-04-07', 'Academic', 'Block C, 210',      '2026-03-22 08:00:00', '2026-04-06 23:59:59', '2026-04-07 09:00:00', '2026-04-07 11:00:00', 'SCHEDULED', 1),
('Cybersecurity Talk',        'Introduction to ethical hacking and security practices.',    '2026-04-09', 'Talk',     'Building E, Room 102', '2026-03-25 08:00:00', '2026-04-08 23:59:59', '2026-04-09 11:00:00', '2026-04-09 13:00:00', 'SCHEDULED', 1),
('Angular Deep Dive',         'Advanced Angular patterns and component architecture.',       '2026-04-11', 'Workshop', 'Building C, Room G004',    '2026-03-25 08:00:00', '2026-04-10 23:59:59', '2026-04-11 13:00:00', '2026-04-11 15:00:00', 'SCHEDULED', 1),
('Basketball Tournament',     'Inter-department basketball competition.',                    '2026-04-13', 'Sports',   'Sports Complex',       '2026-03-28 08:00:00', '2026-04-12 23:59:59', '2026-04-13 08:00:00', '2026-04-13 17:00:00', 'SCHEDULED', 1),
('Database Design Workshop',  'Practical guide to relational database design with JPA.',    '2026-04-15', 'Workshop', 'Building B, Room G001',    '2026-04-01 08:00:00', '2026-04-14 23:59:59', '2026-04-15 10:00:00', '2026-04-15 12:00:00', 'SCHEDULED', 1),
('Career Fair Co-Op Preparation',  'Resume writing and interview preparation for final years.',  '2026-04-17', 'Academic', 'Building H, Room 214',        '2026-04-01 08:00:00', '2026-04-16 23:59:59', '2026-04-17 14:00:00', '2026-04-17 16:00:00', 'SCHEDULED', 1),
('End of Semester Goals',    'A talk event for all students that want to get tips to be satisfied by the end of the semester.',       '2026-04-20', 'Talk',   'Building H, Room 200',    '2026-04-05 08:00:00', '2026-04-19 23:59:59', '2026-04-20 17:00:00', '2026-04-20 20:00:00', 'SCHEDULED', 1);