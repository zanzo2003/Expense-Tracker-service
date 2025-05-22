package com.bhaskarshashwath.expense.tracker.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@AllArgsConstructor
@Builder
public class ApiResponseDTO {

    @NonNull
    private Boolean success;
    @NonNull
    private String message;
    private String error;
    private Object data;
}
