package com.job.portal.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The User class is our Entity. 
 * Analogy: A blueprint that maps a database table row to a Java object. 
 * If the database table is a spreadsheet, this class defines the columns of that spreadsheet in Java.
 *
 * @Entity: Tells JPA/Hibernate that this class represents a database table.
 * @Table(name = "USERS"): Tells Hibernate that the actual table in our database is named "USERS".
 * @Getter/@Setter: Lombok annotations that automatically generate the getUserId(), setEmail(), etc.
 * @NoArgsConstructor: Lombok annotation that generates the default no-argument constructor.
 */
@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
public class User {

    /**
     * The unique identifier for each user.
     * 
     * @Id: Marks this field as the Primary Key.
     * @GeneratedValue: Tells Spring Boot how to generate values for new records.
     * @SequenceGenerator: Connects us to the sequence "USERS_SEQ" in Oracle, matching the monolith.
     * @Column(name = "USER_ID"): Maps this property to the USER_ID column in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    @SequenceGenerator(name = "users_seq_gen", sequenceName = "USERS_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long userId;

    /**
     * The user's email address, used as their login username.
     * @Column(name = "EMAIL"): Maps this property to the EMAIL column.
     */
    @Column(name = "EMAIL", nullable = false, unique = true, length = 200)
    private String email;

    /**
     * The user's BCrypt-hashed password.
     * @Column(name = "PASSWORD_HASH"): Maps this property to the PASSWORD_HASH column.
     */
    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    /**
     * The user's role (STUDENT, EMPLOYER, ADMIN).
     * @Column(name = "ROLE"): Maps this property to the ROLE column.
     * We map this as a String to easily match the database column.
     */
    @Column(name = "ROLE", nullable = false, length = 30)
    private String role;

    /**
     * Flag indicating if the user's account is active.
     * @Column(name = "IS_ACTIVE"): Maps this property to the IS_ACTIVE column.
     * In Oracle, this is stored as a NUMBER(1,0) (1 for true, 0 for false).
     */
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active;
}
