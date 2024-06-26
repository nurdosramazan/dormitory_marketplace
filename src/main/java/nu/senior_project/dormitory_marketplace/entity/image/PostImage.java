package nu.senior_project.dormitory_marketplace.entity.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.Post;

@Entity
@Table(schema = "marketplace", name = "post_image")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImage {

    @Id
    @Column(name = "post_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "path")
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @Column(name = "is_main")
    private Boolean isMain;

    @Override
    public String toString() {
        return "PostImage{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", isMain=" + isMain +
                '}';
    }
}
