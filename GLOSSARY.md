# Glossary

## Domain-Specific Terms

| Term | Definition |
|---|---|
| Role | A classification that defines a user’s permissions in the system. |
| User | Foundational entity. Holds information shared between other accounts like usernames and passwords. |
| Admin | High-level user. Responsible for managing user accounts and approving possible events. |
| Student | Type of user. Able to register for events and can manage their schedule. |
| Organizer | Type of user. Can create, edit, and delete events for their approved departments. |
| Registration | Record holder. Tracks the status of a student’s intent to attend an event. |
| Event | A campus activity. Defined by title, description, starting and ending time, and capacity. |
| Location | Place where a particular event will take place. Defined by building, room, and address. |
| Calendar | A date collection that shows the events. |
| Calendar Entry | A specific record in the calendar that points to a particular event. |
| Notification | Alert system sent to users regarding registration updates, event changes, or system messages. |

## Technical Terms

| Term | Meaning |
|---|---|
| Boolean | A data type that holds only two values: `true` or `false`. |
| String | A data type that holds a sequence of characters. |
| Int | A data type that holds whole numbers. |
| DateTime | A data type used to store both the date and the specific time. |
| List<T> | A collection type that can hold multiple objects of the same type. |
| UUID | A 128-bit identifier used to assign a unique ID. |
| Hash | An algorithm that converts input into a fixed-length string that appears random. |
| Role, Admin, Student, Organizer, Registration, Event, Location, Calendar, Calendar Entry, Notification | Classes used in the system. |
| Abstract class (User) | A class designed to be extended/implemented by other classes. |
| EventStatus | An enum that tracks the state of an event. |
| Void | Indicates that a function/method does not return any value. |

## Acronyms and Abbreviations

| Word / Term | Abbreviation / Meaning |
|---|---|
| UUID | Universally Unique Identifier |
| reg | Registration (used in parameters) |
| ID / Id | Identifier |