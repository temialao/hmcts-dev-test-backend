package uk.gov.hmcts.reform.dev.models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private TaskStatus status;
}
