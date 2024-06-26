package nu.senior_project.dormitory_marketplace.entity.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.Category;

@Entity
@Table(schema = "marketplace", name = "category_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryImage {

    @Id
    @Column(name = "category_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path")
    private String path;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;
}
