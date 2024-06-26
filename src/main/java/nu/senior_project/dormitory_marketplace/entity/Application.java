package nu.senior_project.dormitory_marketplace.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.senior_project.dormitory_marketplace.enums.EApplicationStatus;

@Entity
@Table(schema = "marketplace", name = "application")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private User applicant;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private EApplicationStatus status;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;
}
