package nu.senior_project.dormitory_marketplace.entity.elastic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.image.PostImage;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Document(indexName = "post_index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Integer)
    private Integer price;

    @Field(type=FieldType.Nested, store = true)
    private List<FakeString> imageUrls;

    @Field(type=FieldType.Integer)
    private Long categoryId;

    @Field(type=FieldType.Text)
    private String categoryName;

    @Field(type=FieldType.Text)
    private String ownerUsername;

    @Field(type=FieldType.Text)
    private String ownerFirstName;

    @Field(type=FieldType.Text)
    private String ownerSecondName;

    @Field(type=FieldType.Long)
    private Long ownerId;

    @Field(type=FieldType.Text)
    private String rating;

    public PostDocument(Post post) {
        this.categoryId = post.getCategory().getId();
        this.id = post.getId().toString();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.price = post.getPrice();
        this.categoryName = post.getCategory().getName();
        this.ownerUsername = post.getOwner().getUsername();
        this.ownerFirstName = post.getOwner().getFirstname();
        this.ownerSecondName = post.getOwner().getSecondName();
        this.ownerId = post.getOwner().getId();
        this.rating = post.getAverageRating() == null ? "0" : post.getAverageRating().toString();
        if (post.getImages() != null) {
            List<String> urls = post.getImages().stream()
                    .map(PostImage::getPath)
                    .toList();
            this.imageUrls = new ArrayList<>();
            for (String url : urls) {
                imageUrls.add(new FakeString(url));
            }
        } else {
            this.imageUrls = Collections.emptyList();
        }
    }

    public class FakeString {
        private String url;

        public FakeString(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
