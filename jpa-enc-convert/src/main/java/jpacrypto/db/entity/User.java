package jpacrypto.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;



@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "varchar")
    private String userId;

    @Convert(converter = UserDetailsEncConverter.class)
    @Column(name = "encrypted_data")
    private UserDetails userDetails;


}
