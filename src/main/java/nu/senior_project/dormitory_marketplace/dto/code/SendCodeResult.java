package nu.senior_project.dormitory_marketplace.dto.code;

import lombok.Data;
import nu.senior_project.dormitory_marketplace.dto.GeneralResponseModel;

@Data
public class SendCodeResult extends GeneralResponseModel {
    private String token;
}
