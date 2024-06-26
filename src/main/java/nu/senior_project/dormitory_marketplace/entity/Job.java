package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "marketplace", name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "modified_date")
    @UpdateTimestamp
    private Timestamp modifiedDate;

    @Column(name = "pay_per_unit")
    private Long payPerUnit;

    @ManyToOne
    @JoinColumn(name = "pay_unit_id", referencedColumnName = "id")
    private PayUnit payUnit;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "qualifications")
    private String qualifications;
}
