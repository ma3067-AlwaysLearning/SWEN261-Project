-- =========================
-- USERS
-- =========================
-- =========================
-- USERS
-- =========================

/*Dummy records for table users to test if authentication works*/
INSERT INTO users (name, email, password, role, university_id) VALUES
                                                                   ('Mohammed Adil',  'ma3067@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'ORGANIZER', '415006855'),
                                                                   ('Osama Ahmed',    'oja5093@rit.edu',    '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'ADMIN',  '764000269'),
                                                                   ('Abdullah Kair',  'ahk3336@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'STUDENT',  '377003755'),
                                                                   ('Jason Venkataraghavan',  'jnv8919@rit.edu',  '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'STUDENT',  '757001356'),
                                                                   ('Ahmed Almarri',  'aaa8902@rit.edu',   '$2a$12$exik4IGgQgu9.UN47CGgneA9Bi8IcJQGVjBU2Wyjl8Vr2KsKGOKcK', 'STUDENT',  '769002024');

-- =========================
-- EVENTS
-- =========================
INSERT INTO events (
    title,
    description,
    scheduled_date,
    category,
    location,
    registration_start,
    registration_end,
    start_time,
    end_time,
    status,
    capacity,
    organizer_id
) VALUES

-- Normal event
('Spring Boot Workshop',
 'Hands-on introduction to Spring Boot and REST APIs.',
 '2026-04-01',
 'Workshop',
 'Building H, Room 101',
 '2026-03-15 08:00:00',
 '2026-03-31 23:59:59',
 '2026-04-01 10:00:00',
 '2026-04-01 12:00:00',
 'SCHEDULED',
 30,
 1),

-- Another valid event
('Cybersecurity Seminar',
 'Learn about modern security threats and defenses.',
 '2026-04-05',
 'Seminar',
 'Building G, Room 202',
 '2026-03-20 08:00:00',
 '2026-04-04 23:59:59',
 '2026-04-05 14:00:00',
 '2026-04-05 16:00:00',
 'SCHEDULED',
 25,
 1),

-- PAST EVENT (tests blocking)
('Past Networking Event',
 'An old event to test blocking.',
 '2025-01-01',
 'Networking',
 'Building A, Room 1',
 '2024-12-01 08:00:00',
 '2024-12-31 23:59:59',
 '2025-01-01 10:00:00',
 '2025-01-01 12:00:00',
 'SCHEDULED',
 20,
 1),

-- OVERLAPPING EVENT (tests time conflict)
('AI Conference',
 'AI trends and research.',
 '2026-04-01',
 'Conference',
 'Building D, Hall',
 '2026-03-10 08:00:00',
 '2026-03-30 23:59:59',
 '2026-04-01 11:00:00',
 '2026-04-01 13:00:00',
 'SCHEDULED',
 40,
 1),

-- FULL CAPACITY EVENT (tests capacity limit)
('Limited Seats Event',
 'Only a few people can join.',
 '2026-04-10',
 'Workshop',
 'Building B, Room 10',
 '2026-03-15 08:00:00',
 '2026-04-09 23:59:59',
 '2026-04-10 10:00:00',
 '2026-04-10 12:00:00',
 'SCHEDULED',
 1,
 1);
-- =========================
-- EVENTS
-- =========================
INSERT INTO events (
    title,
    description,
    scheduled_date,
    category,
    location,
    registration_start,
    registration_end,
    start_time,
    end_time,
    status,
    capacity,
    organizer_id
) VALUES

-- Normal event
('Spring Boot Workshop',
 'Hands-on introduction to Spring Boot and REST APIs.',
 '2026-04-01',
 'Workshop',
 'Building H, Room 101',
 '2026-03-15 08:00:00',
 '2026-03-31 23:59:59',
 '2026-04-01 10:00:00',
 '2026-04-01 12:00:00',
 'SCHEDULED',
 30,
 1),

-- Another valid event
('Cybersecurity Seminar',
 'Learn about modern security threats and defenses.',
 '2026-04-05',
 'Seminar',
 'Building G, Room 202',
 '2026-03-20 08:00:00',
 '2026-04-04 23:59:59',
 '2026-04-05 14:00:00',
 '2026-04-05 16:00:00',
 'SCHEDULED',
 25,
 1),

-- PAST EVENT (tests blocking)
('Past Networking Event',
 'An old event to test blocking.',
 '2025-01-01',
 'Networking',
 'Building A, Room 1',
 '2024-12-01 08:00:00',
 '2024-12-31 23:59:59',
 '2025-01-01 10:00:00',
 '2025-01-01 12:00:00',
 'SCHEDULED',
 20,
 1),

-- OVERLAPPING EVENT (tests time conflict)
('AI Conference',
 'AI trends and research.',
 '2026-04-01',
 'Conference',
 'Building D, Hall',
 '2026-03-10 08:00:00',
 '2026-03-30 23:59:59',
 '2026-04-01 11:00:00',
 '2026-04-01 13:00:00',
 'SCHEDULED',
 40,
 1),

-- FULL CAPACITY EVENT (tests capacity limit)
('Limited Seats Event',
 'Only a few people can join.',
 '2026-04-10',
 'Workshop',
 'Building B, Room 10',
 '2026-03-15 08:00:00',
 '2026-04-09 23:59:59',
 '2026-04-10 10:00:00',
 '2026-04-10 12:00:00',
 'SCHEDULED',
 1,
 1);

-- =========================
-- ADDITIONAL EVENTS FOR TESTING (APPENDED)
-- =========================
INSERT INTO events (
    title,
    description,
    scheduled_date,
    category,
    location,
    registration_start,
    registration_end,
    start_time,
    end_time,
    status,
    capacity,
    organizer_id
) VALUES

-- ✅ FUTURE EVENT (normal registration should work)
('Cloud Computing Workshop',
 'Intro to AWS and cloud basics.',
 '2026-05-10',
 'Workshop',
 'Building C, Room 303',
 '2026-04-01 08:00:00',
 '2026-05-09 23:59:59',
 '2026-05-10 10:00:00',
 '2026-05-10 12:00:00',
 'SCHEDULED',
 50,
 1),

-- ❌ TIME CONFLICT (overlaps with above)
('DevOps Bootcamp',
 'CI/CD and deployment pipelines.',
 '2026-05-10',
 'Workshop',
 'Building C, Room 305',
 '2026-04-01 08:00:00',
 '2026-05-09 23:59:59',
 '2026-05-10 11:00:00',
 '2026-05-10 13:00:00',
 'SCHEDULED',
 40,
 1),

-- ❌ CAPACITY = 1 (for testing limit)
('Exclusive Research Session',
 'One-on-one research mentoring.',
 '2026-05-12',
 'Seminar',
 'Building F, Lab 1',
 '2026-04-01 08:00:00',
 '2026-05-11 23:59:59',
 '2026-05-12 09:00:00',
 '2026-05-12 10:00:00',
 'SCHEDULED',
 1,
 1);