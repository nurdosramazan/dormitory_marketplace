package nu.senior_project.dormitory_marketplace.entity.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.User;

@Entity
@Table(schema = "marketplace", name = "user_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserImage {
    @Id
    @Column(name = "user_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "path")
    private String path;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
