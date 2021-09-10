package htwb.ai.minh.userservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * A User - Entity
 * has a token variable
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @NotBlank
    @Column(name = "userid")
    private String userId;

    @NotBlank
    @Column(name = "password")
    @Size(max = 50)
    private String password;

    @Column(name = "firstname")
    @Size(max = 50)
    private String firstName;

    @Column(name = "lastname")
    @Size(max = 50)
    private String lastName;

}
