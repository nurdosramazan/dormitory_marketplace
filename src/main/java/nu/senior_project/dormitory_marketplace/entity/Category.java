package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.entity.image.CategoryImage;

import java.util.List;

@Entity
@Table(schema = "marketplace", name = "category")
@Data
public class Category {

    @Column(name = "category_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToOne(mappedBy = "category")
    private CategoryImage image;
}
