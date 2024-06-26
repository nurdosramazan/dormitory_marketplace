package nu.senior_project.dormitory_marketplace.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InsufficientRightsException extends RuntimeException {
    private String message;
}
