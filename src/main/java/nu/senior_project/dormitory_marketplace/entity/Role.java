package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.enums.ERole;
import java.util.List;

@Entity
@Data
@Table(schema = "marketplace", name = "role")
public class Role {

    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private ERole roleName;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;
}
