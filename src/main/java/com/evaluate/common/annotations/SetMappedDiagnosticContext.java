package com.evaluate.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.evaluate.common.annotations.SetMappedDiagnosticContext.ActionType.API_REQUEST;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SetMappedDiagnosticContext {
    ActionType actionType() default API_REQUEST;

    public enum ActionType {
        API_REQUEST
    }
}
