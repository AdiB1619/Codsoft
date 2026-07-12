package com.codsoft.sms.dto.request;

import com.codsoft.sms.entity.enums.StudentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request body for the {@code PATCH /api/v1/students/{id}/status} endpoint.
 *
 * <p>Intentionally narrow — carries only the new status value so the
 * client does not need to submit the entire student record for a status change.
 */
@Getter
@NoArgsConstructor
public final class StatusUpdateRequest {

    /**
     * The target enrollment status. Must be one of the
     * {@link StudentStatus} enum values; Jackson rejects unknown strings with
     * a 400 before the controller method is invoked.
     */
    @NotNull(message = "Status is required")
    private StudentStatus status;
}
