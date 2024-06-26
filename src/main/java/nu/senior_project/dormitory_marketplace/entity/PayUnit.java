package nu.senior_project.dormitory_marketplace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(schema = "marketplace", name = "pay_unit")
public class PayUnit {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;
}
