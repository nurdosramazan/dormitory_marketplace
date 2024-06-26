package nu.senior_project.dormitory_marketplace.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GeneralPageableResponse<T> {
    private Integer totalNumber;
    private List<T> data;

    public GeneralPageableResponse(List<T> totalResults, int limit, int offset) {

        if (offset > totalResults.size()) {
            this.data = new ArrayList<>();
        } else if (offset + limit > totalResults.size()) {
            this.data = totalResults.subList(offset, totalResults.size());
        } else {
            this.data = totalResults.subList(offset, offset + limit);
        }

        this.totalNumber = totalResults.size();
    }
}
