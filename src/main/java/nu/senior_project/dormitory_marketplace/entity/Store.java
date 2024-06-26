package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.image.StoreImage;

import java.util.List;

@Entity
@Table(schema = "marketplace", name = "store")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {
    @Column(name = "store_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "store")
    private List<StoreImage> images;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name = "paypal_email")
    private String paypalEmail;
}
