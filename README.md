Authentication Microservice
Project Overview
This project is an Authentication Microservice built with Spring Boot, designed to handle user authentication, role-based authorization, and refresh token management. It provides a secure way to authenticate users, assign roles, and issue refresh tokens for maintaining user sessions. The microservice uses MySQL as the database and integrates with Spring Security for authentication and authorization.
Last Updated: May 25, 2025, 01:57 PM IST
Features

User Authentication: Authenticate users using username and password.
Role-Based Authorization: Assign roles to users for access control.
Refresh Token Management: Generate and manage refresh tokens with expiration and automatic cleanup.
Database Persistence: Store user data, roles, and tokens in a MySQL database.
Scheduled Cleanup: Automatically delete expired refresh tokens daily.

Technologies Used

Spring Boot: 3.4.5
Hibernate: 6.6.13
MySQL: Connector mysql-connector-j-9.3.0
Spring Security: For authentication and authorization
Java: 17 (assumed based on Spring Boot version)
Maven: Build tool

Architecture
Components

Client: A frontend application or another microservice that interacts with the auth microservice via REST endpoints.
Auth Microservice:
Controller Layer: REST endpoints (e.g., /login, /refresh-token).
Service Layer: Business logic (e.g., RefreshTokenService).
Repository Layer: Database access (e.g., UserRepository, RefreshTokenRepository).
Spring Security: Custom AuthenticationProvider for authentication.
Scheduled Task: Deletes expired refresh tokens daily.


MySQL Database: Stores users, roles, user_roles, and refresh_token tables.

Diagram
[Client] --> [Auth Microservice]
             |
             |--> [Controller: /login, /refresh-token]
             |--> [Service: RefreshTokenService (Handles Refresh Tokens)]
             |--> [Scheduled Task: Delete Expired Tokens]
             |--> [Repository: UserRepository, RefreshTokenRepository]
             |--> [Spring Security: AuthenticationProvider]
             |
            [MySQL Database: users, roles, user_roles, refresh_token (unique token, unique user_id)]

Database Schema
Tables

users:

user_id: BIGINT AUTO_INCREMENT PRIMARY KEY
username: VARCHAR(255) NOT NULL UNIQUE
password: VARCHAR(255) NOT NULL


roles:

role_id: BIGINT AUTO_INCREMENT PRIMARY KEY
name: VARCHAR(255) NOT NULL UNIQUE


user_roles (Join Table):

user_id: BIGINT NOT NULL (Foreign Key to users(user_id))
role_id: BIGINT NOT NULL (Foreign Key to roles(role_id))
Primary Key: (user_id, role_id)


refresh_token:

id: BIGINT AUTO_INCREMENT PRIMARY KEY
token: VARCHAR(255) NOT NULL UNIQUE
expiry_date: DATETIME
user_id: BIGINT NOT NULL UNIQUE (Foreign Key to users(user_id))



SQL Setup
To set up the database, execute the following SQL statements:
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT uk_refresh_token_user_id UNIQUE (user_id)
);

Setup Instructions
Prerequisites

Java: 17
Maven: 3.8+
MySQL: 8.0+
IDE: IntelliJ IDEA, Eclipse, or similar

Steps

Clone the Repository:
git clone <repository-url>
cd auth-microservice


Configure MySQL:

Create a database named auth_service_et:CREATE DATABASE auth_service_et;


Update the application.properties file with your MySQL credentials:spring.datasource.url=jdbc:mysql://localhost:3306/auth_service_et
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.open-in-view=false




Set Up the Database Schema:

Execute the SQL statements provided in the "Database Schema" section to create the tables.


Build and Run the Application:
mvn clean install
mvn spring-boot:run

The application will start on http://localhost:8080.

Verify the Setup:

Check the logs for any errors.
Ensure the application starts successfully and the schema is validated by Hibernate.



Usage
Endpoints

Login:

URL: /login (assumed endpoint)
Method: POST
Request Body:{
    "username": "user1",
    "password": "password123"
}


Response: (Assumed){
    "success": true,
    "message": "Login successful",
    "data": {
        "refreshToken": "uuid-value"
    }
}




Refresh Token:

URL: /refresh-token
Method: POST
Request Param: username=user1
Response:{
    "success": true,
    "message": "Token refreshed",
    "data": {
        "id": 1,
        "token": "new-uuid-value",
        "expiryDate": "2025-05-25T14:07:00Z",
        "userId": 1
    }
}





Token Management

Refresh Token Expiry: Tokens expire after 10 minutes (600,000 milliseconds). Adjust the expiry time in RefreshTokenService as needed:.expiryDate(Instant.now().plus(30, ChronoUnit.DAYS)) // For 30 days


Expired Token Cleanup: A scheduled task runs daily to delete expired tokens.

Code Overview
Entities

UserInfo:
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRoles> roles = new HashSet<>();
}


UserRoles:
@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class UserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(nullable = false)
    private String name;
}


RefreshToken:
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private Instant expiryDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserInfo userInfo;
}



Services

RefreshTokenService:@Service
public class RefreshTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository tokenRepository;

    public RefreshToken createRefreshToken(String username) {
        UserInfo userDetails = userRepository.findByUsername(username);
        if (userDetails == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }

        tokenRepository.findByUserInfo(userDetails)
                .ifPresent(tokenRepository::delete);

        try {
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .userInfo(userDetails)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(600000))
                    .build();
            return tokenRepository.save(newRefreshToken);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed to create refresh token: duplicate token detected", e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating refresh token", e);
        }
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void deleteExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(Instant.now());
    }
}



Repositories

UserRepository:
@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUsername(String username);
}


RefreshTokenRepository:
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserInfo(UserInfo userInfo);
    void deleteAllByExpiryDateBefore(Instant instant);
}



Troubleshooting

Duplicate Entry Error for refresh_token:

Ensure only one token exists per user by checking the refresh_token table:SELECT user_id, COUNT(*) FROM refresh_token GROUP BY user_id HAVING COUNT(*) > 1;


Delete duplicate entries if found:DELETE FROM refresh_token WHERE token = 'duplicate-token-value';




Schema Validation Errors:

Verify the database schema matches the entity mappings.
Set spring.jpa.hibernate.ddl-auto=validate to ensure Hibernate validates the schema without modifying it.


Spring Security Warnings:

If you see warnings about AuthenticationProvider or UserDetailsService, ensure your Spring Security configuration is correct.
To suppress warnings, adjust the logging level:logging.level.org.springframework.security=ERROR





Future Improvements

Access Token Support: Add support for issuing access tokens (e.g., JWTs) alongside refresh tokens.
API Gateway: Integrate an API Gateway for routing, rate limiting, and CORS handling.
Logging: Add detailed logging for authentication events and token management.
Migration Tool: Use Flyway or Liquibase for database schema migrations in production.

License
This project is licensed under the MIT License - see the LICENSE file for details.

