package com.rest.java.test.task.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends RepresentationModel<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstName")
    @NotBlank(message = "First Name is required")
    private String firstName;

    @Column(name = "lastName")
    @NotBlank(message = "Last Name is required")
    private String lastName;

    @Column(name = "email")
    @NotNull(message = "Email is required")
    @Email(message = "Please enter a valid email", regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z]+\\.[a-zA-Z.]{2,5}")
    private String email;

    @Column(name = "birthDate")
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth Date must be earlier than current date")
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "phoneNumber")
    @Pattern(regexp = "^\\+[0-9]{10,12}", message = "Write a correct phone number. Use numbers. Not more than 12.")
    private String phoneNumber;
}
