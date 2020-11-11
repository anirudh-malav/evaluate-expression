package com.evaluate.controller;

import com.evaluate.common.annotations.ValidateRequest;
import com.evaluate.common.constants.Constants;
import com.evaluate.common.helper.APIResponse;
import com.evaluate.dto.request.EvaluateRequestDto;
import com.evaluate.dto.response.Response;
import com.evaluate.service.EvaluateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(Constants.Controller.BASE_URL)
public class EvaluationController {
    @Autowired
    EvaluateService evaluateService;

    @PostMapping(Constants.Controller.EVALUATE)
    @ValidateRequest
    public ResponseEntity<Response> getEvaluation(@RequestBody EvaluateRequestDto evaluateRequestDto, @RequestHeader (value = "user-email") String userEmail, @RequestHeader (value = "user-password") String userPassword) {
        return APIResponse.renderSuccess(evaluateService.getEvaluation(evaluateRequestDto), 200, HttpStatus.OK);
    }

    @GetMapping(Constants.Controller.GET_EXPRESSION_COUNT)
    @ValidateRequest
    public ResponseEntity<Response> getMaxOperatorForUser(@PathVariable("userEmail") String userEmail, @RequestHeader (value = "user-email") String Email, @RequestHeader (value = "user-password") String Password) {
        return APIResponse.renderSuccess(evaluateService.getUserInfo(userEmail), 200, HttpStatus.OK);
    }
}
