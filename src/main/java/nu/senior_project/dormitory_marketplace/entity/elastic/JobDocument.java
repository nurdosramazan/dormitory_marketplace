package nu.senior_project.dormitory_marketplace.entity.elastic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nu.senior_project.dormitory_marketplace.entity.Job;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "job_index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Long)
    private Long payPerUnit;

    @Field(type = FieldType.Long)
    private Long payUnitId;

    public JobDocument(Job job) {
        this.id = job.getId();
        this.name = job.getName();
        this.description = job.getDescription();
        this.payPerUnit = job.getPayPerUnit();
        this.payUnitId = job.getPayUnit().getId();
    }
}