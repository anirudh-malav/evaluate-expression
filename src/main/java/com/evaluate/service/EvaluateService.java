package com.evaluate.service;

import com.evaluate.dto.request.EvaluateRequestDto;
import com.evaluate.dto.response.EvaluateResponseDto;
import com.evaluate.dto.response.UserResponseDTO;

public interface EvaluateService {
    EvaluateResponseDto getEvaluation(EvaluateRequestDto evaluateRequestDto);

    UserResponseDTO getUserInfo(String userEmail);

}
