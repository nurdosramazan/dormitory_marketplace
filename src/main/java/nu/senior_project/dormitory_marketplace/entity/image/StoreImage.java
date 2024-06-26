package nu.senior_project.dormitory_marketplace.entity.image;

import jakarta.persistence.*;
import lombok.Data;
import nu.senior_project.dormitory_marketplace.entity.Store;

@Entity
@Table(schema = "marketplace", name = "store_image")
@Data
public class StoreImage {
    @Id
    @Column(name = "store_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "path")
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", referencedColumnName = "store_id")
    private Store store;
}
