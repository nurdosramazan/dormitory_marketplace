package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.image.UserImage;
import nu.senior_project.dormitory_marketplace.entity.paymentModel.Payout;

import java.util.List;

@Entity
@Table(schema = "marketplace", name = "simple_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "password")
    private String password;

    @Column(name = "is_store")
    private Boolean isStore;

    @OneToOne(mappedBy = "user")
    private UserImage image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = "marketplace",
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @OneToMany(mappedBy = "owner")
    private List<Post> posts;

    @OneToMany(mappedBy = "seller")
    private List<Sale> sales;

    @OneToMany(mappedBy = "buyer")
    private List<Sale> purchases;

    @OneToMany(mappedBy = "owner")
    private List<Post> jobs;

    @OneToMany(mappedBy = "seller")
    private List<Payout> payouts;
}
