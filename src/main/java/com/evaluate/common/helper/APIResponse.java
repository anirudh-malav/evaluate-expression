package com.evaluate.common.helper;

import com.evaluate.dto.response.*;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class APIResponse {

    public static ResponseEntity<Response> renderSuccess(Object data, int code, HttpStatus status) {
        Meta meta = successMeta(code);
        Response response = new Response();
        response.setMeta(meta);
        response.setData(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public static ResponseEntity<Response> renderFailure(String customErrorMessage, int code, HttpStatus status) {
        Meta meta = failureMeta(customErrorMessage, code);
        Response response = new Response();
        response.setMeta(meta);
        return new ResponseEntity<>(response, status);
    }

    private static Meta successMeta(int code) {
        Meta meta = new Meta();
        meta.setCode(code);
        //setDescriptionAndUICode(meta, code);
        meta.setRequestId(MDC.get("requestId"));
        meta.setStatus("success");
        return meta;
    }

    private static Meta failureMeta(String error, int code) {
        Meta meta = new Meta();
        meta.setCode(code);
        //setDescriptionAndUICode(meta, code);
        meta.setError(error);
        meta.setRequestId(MDC.get("requestId"));
        meta.setStatus("failure");
        return meta;
    }


}
