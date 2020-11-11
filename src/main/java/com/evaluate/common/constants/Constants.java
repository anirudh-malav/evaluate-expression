package com.evaluate.common.constants;

public class Constants {

    public static class Controller {
        public static final String BASE_URL = "/api/evaluate";
        public static final String EVALUATE = "/expression/{expression}";
        public static final String GET_EXPRESSION_COUNT = "/user/{userEmail}";
    }

    public static class Header {
        public static final String USER_EMAIL = "user-email";
        public static final String USER_PASSWORD = "user-password";
    }

}
