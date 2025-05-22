package com.bhaskarshashwath.expense.tracker.util;


import com.bhaskarshashwath.expense.tracker.model.response.ApiResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ControllerHelper {

    public ApiResponseDTO createSuccessResponse(String message){
        return ApiResponseDTO.builder()
                .success(true)
                .error(null)
                .message(message)
                .data(null)
                .build();
    }

    public ApiResponseDTO createSuccessResponse(String message, Object data){
        return ApiResponseDTO.builder()
                .success(true)
                .error(null)
                .message(message)
                .data(data)
                .build();
    }

    public ApiResponseDTO createErrorResponse(String message, String error){
        return ApiResponseDTO.builder()
                .success(false)
                .error(error)
                .message(message)
                .data(null)
                .build();

    }
}
