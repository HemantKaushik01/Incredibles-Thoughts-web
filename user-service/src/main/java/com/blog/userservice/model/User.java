package com.blog.userservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user") // Renamed table to avoid conflict with SQL 'USER' keyword
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;
    private String password; // will be stored encrypted

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    private Role role;
}
