package nu.senior_project.dormitory_marketplace.exception.bad_request;

import lombok.Data;

@Data
public class UnsuccessfulRegistrationException extends BadRequestException {
    private String message;
    public UnsuccessfulRegistrationException(String message) {
        this.message = message;
    }
}
