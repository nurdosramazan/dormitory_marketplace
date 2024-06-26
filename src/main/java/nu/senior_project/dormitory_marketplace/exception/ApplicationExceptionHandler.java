package nu.senior_project.dormitory_marketplace.exception;

import nu.senior_project.dormitory_marketplace.dto.GeneralResponseModel;
import nu.senior_project.dormitory_marketplace.exception.auction.AuctionException;
import nu.senior_project.dormitory_marketplace.exception.bad_request.BadRequestException;
import nu.senior_project.dormitory_marketplace.exception.bad_request.FormValidationException;
import nu.senior_project.dormitory_marketplace.exception.bad_request.UnsuccessfulRegistrationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

@RestControllerAdvice
public class ApplicationExceptionHandler {


    @ExceptionHandler(value = {FormValidationException.class, UnsuccessfulRegistrationException.class})
    private ResponseEntity<GeneralResponseModel> handleBadRequest(BadRequestException exception) {
        GeneralResponseModel generalResponseModel = new GeneralResponseModel();

        if (exception instanceof FormValidationException) {
            BindingResult bindingResult = ((FormValidationException) exception).getBindingResult();

            generalResponseModel.setErrors(new ArrayList<>());

            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                GeneralResponseModel.ErrorDto errorDto = new GeneralResponseModel.ErrorDto();
                errorDto.setField(fieldError.getField());
                errorDto.setMessage(fieldError.getDefaultMessage());
                generalResponseModel.getErrors().add(errorDto);
            }
        }

        generalResponseModel.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(generalResponseModel);
    }

    @ExceptionHandler(InsufficientRightsException.class)
    private ResponseEntity<GeneralResponseModel> handleInsufficientRightsException(InsufficientRightsException exception) {
        String message = exception.getMessage();
        GeneralResponseModel generalResponseModel = new GeneralResponseModel();
        generalResponseModel.setMessage(message);
        return ResponseEntity.status(403).body(generalResponseModel);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<GeneralResponseModel> handleBadRequest(NotFoundException exception) {
        String message = exception.getMessage();
        GeneralResponseModel generalResponseModel = new GeneralResponseModel();
        generalResponseModel.setMessage(message);
        return ResponseEntity.status(404).body(generalResponseModel);
    }

    @ExceptionHandler(AuctionException.class)
    private ResponseEntity<GeneralResponseModel> handleAuctionException(AuctionException exception) {
        String message = exception.getMessage();
        GeneralResponseModel generalResponseModel = new GeneralResponseModel();
        generalResponseModel.setMessage(message);
        return ResponseEntity.status(400).body(generalResponseModel);
    }
}
