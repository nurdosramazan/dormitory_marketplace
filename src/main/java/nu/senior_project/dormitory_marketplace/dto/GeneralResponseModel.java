package nu.senior_project.dormitory_marketplace.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralResponseModel {
    private boolean success = false;
    private List<ErrorDto> errors;
    private String message;

    @Getter
    @Setter
    public static class ErrorDto {
        private String field;
        private String message;
    }
}
