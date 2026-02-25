# Glossary

## Domain-Specific Terms

| Term | Definition |
|---|---|
| Role | A classification that defines a user’s permissions in the system. |
| User | Foundational entity. Holds information shared between other accounts like usernames and passwords. |
| Admin | High-level user. Responsible for managing user accounts and approving possible events. |
| University ID | A unique identifier assigned to each user that links them to their university. For example: `415000000`. |
| Student | Type of user. Able to register for events and can manage their schedule. |
| Authentication | The process of verifying a user's identity by checking their submitted email and password against stored credentials. |
| Authorization | The process of determining whether an authenticated user has permission to access a specific resource based on their role. |
| Login | The action of submitting credentials to the system to establish an authenticated session and gain access to protected pages. |
| Logout | The action of terminating an authenticated session, clearing session data on the server, and removing the session cookie from the browser. |
| Session | A server-side record of an authenticated user's state between HTTP requests. Created on login and destroyed on logout. |
| Dashboard | The main page displayed after successful login. Only accessible to authenticated users — unauthenticated requests are redirected to the login page. |
| Credential | Identifying information used to verify a user's identity. In this system, credentials consist of an email address and a password. |
| Password Hash | A one-way encrypted representation of a password. Plain text passwords are never stored — only their BCrypt hashed equivalents. |
| Dummy Data | Pre-inserted test records used during development to simulate real users without requiring actual registration. Loaded via `data.sql`. |
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
| String | A data type that holds a sequence of characters. Used for fields such as name, email, role, and university_id. |
| Long | A data type that holds large whole numbers. Used as the auto-incremented primary key for the User entity. |
| Int | A data type that holds whole numbers. |
| Hash | An algorithm that converts a password into a fixed-length string that cannot be reversed. BCrypt is the hashing algorithm used in this project.|
| Void | Indicates that a function/method does not return any value. |
| Optional | A Java container that may or may not hold a value. Used as the return type of `findByEmail()` to safely handle missing users. |
| Bean | An object managed and injected by the Spring application context. Examples include `PasswordEncoder`, `SecurityFilterChain`, and `UserDetailsServ`. |
| Entity | A Java class annotated with `@Entity` that maps directly to a database table. `User` is the entity in this project. |
| Entity | A Java class annotated with `@Entity` that maps directly to a database table. `User` is the entity in this project. |
| Repository | A Spring Data interface providing database access methods for an entity. `UserRepository` extends `JpaRepository` to provide CRUD operations. |
| Controller | A Spring class that handles incoming HTTP requests and returns a view name or JSON response. This project has `AuthController`, `DashboardController`, and `AuthApiController`. |
| Filter Chain | An ordered sequence of security filters applied to every HTTP request by Spring Security to enforce authentication and authorization rules. |
| Constructor Injection | A dependency injection pattern where dependencies are passed through the class constructor rather than field assignment. |
| Principal | A Java object representing the currently authenticated user. Used in controllers to retrieve the logged-in user's email via `principal.getName()`. |
| JSESSIONID | The name of the HTTP session cookie that identifies an authenticated session. Deleted on logout via `deleteCookies("JSESSIONID")`. |

## Acronyms and Abbreviations

| Word / Term | Abbreviation / Meaning |
|---|---|
| ID / Id | Identifier |
| API | Application Programming Interface — a set of defined endpoints allowing two systems to communicate. |
| MVC | Model-View-Controller — the architectural pattern used for the server-side Mustache layer. |
| REST | Representational State Transfer — the architectural style used for the Angular-facing `/api/auth` endpoints. |
| JPA | Java Persistence API — the specification used to map Java objects to MariaDB tables via Hibernate. |
| ORM | Object-Relational Mapping — the technique of interacting with a database using Java objects instead of raw SQL. |
| DTO | Data Transfer Object — a simple object carrying data between layers. `LoginRequest` is a DTO. |
| CSRF | Cross-Site Request Forgery — an attack mitigated using `CookieCsrfTokenRepository` in Spring Security. |
| CORS | Cross-Origin Resource Sharing — configured to allow the Angular frontend at `localhost:4200` to call the backend at `localhost:8080`. |
| HTTP | HyperText Transfer Protocol — the communication protocol used between the browser, Angular, and Spring Boot. |
| JSON | JavaScript Object Notation — the data format used for communication between Angular and the REST API. |
| SQL | Structured Query Language — used in `data.sql` to insert dummy users into MariaDB on startup. |
| SPA | Single Page Application — the Angular frontend, which loads once and updates dynamically without full page reloads. |
| JWT | JSON Web Token — considered for stateless authentication but replaced by Spring Security session-based authentication. |