package com.bankcore.api.model;

import com.bankcore.api.model.enums.CustomerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Java Persistance API Annotations (Database Mapping) This class should become a database table
@Table(name = "customers") // Naming conversation to handle database Hibernate
@EntityListeners(AuditingEntityListener.class) // Fill createdAt and updatedAt automatically

// Lombok Annotations (Auto Generated Code) POXML
@Getter
@Setter
@NoArgsConstructor // public customer (){} Empty constructor
@AllArgsConstructor // public Customer(String id, String firstName) Constructors with all fields
@Builder // Customer.builder().firstName("John").build() Fluent builder pattern

public class Customer {

    // ID FIELD
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // generates unique identifiers
    private String id;

    /*
     Validation Annotations

     @NotBlank = Application-level validation — checks BEFORE sending to database (gives nice error messages)
     @Column(nullable = false) = Database-level constraint — the database itself refuses NULL values (last line of defense)
    */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20)
    @Column(unique = true)
    private String phoneNumber;

    @Size(max = 20)
    @Column(unique = true)
    private String idNumber;

    @Column
    private LocalDate dateOfBirth;

    @Size(max = 200)
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 100)
    private String country;

    // The Enum Field
    @Enumerated(EnumType.STRING) // Store the enum as TEXT in the database (not numbers)
    @Column(nullable = false)
    @Builder.Default // Use builder as default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    /*
    Relationships customers mapping

    @OneToMany = One Customer has Many Accounts."
    Class customer owns the relationship
    CascadeType = delete all customers if i delete
    fetch = FetchType.LAZY = Don't load accounts until someone asks for them or
                           = Load all the basic information first fetch other data when requested
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;


    /*
    Audit Fields


     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime closedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}