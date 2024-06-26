package nu.senior_project.dormitory_marketplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.image.PostImage;
import nu.senior_project.dormitory_marketplace.enums.EProductType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.util.List;

@Entity
@Table(schema = "marketplace", name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Column(name = "post_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "created_date")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "modified_date")
    @UpdateTimestamp
    private Date modifiedDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostImage> images;

    @Enumerated(EnumType.STRING)
    private EProductType productType;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Rating> ratings;

    @Column(name = "average_rating")
    private Double averageRating;
}
