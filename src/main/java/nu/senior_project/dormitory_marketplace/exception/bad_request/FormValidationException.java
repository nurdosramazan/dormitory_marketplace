package nu.senior_project.dormitory_marketplace.exception.bad_request;

import org.springframework.validation.BindingResult;

import java.text.Format;

public class FormValidationException extends BadRequestException {
    private BindingResult bindingResult;
    public FormValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

}
