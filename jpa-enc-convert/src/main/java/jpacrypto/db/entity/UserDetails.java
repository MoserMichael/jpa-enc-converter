package jpacrypto.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;



@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_details")
@Entity
@Data
public class UserDetails {

    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "varchar")
    @JsonProperty("user_id")
    private String userId;

    @Column(name = "first_name", columnDefinition = "varchar")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "varchar")
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "email", nullable = false, columnDefinition = "varchar")
    @JsonProperty("email")
    private String email;

    @Column(name = "phone", columnDefinition = "varchar")
    @JsonProperty("phone")
    private String phone;
}
