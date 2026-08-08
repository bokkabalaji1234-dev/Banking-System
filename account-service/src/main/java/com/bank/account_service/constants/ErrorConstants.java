package com.bank.account_service.constants;

public final class ErrorConstants {
    private ErrorConstants() {
        // Prevent object creation
    }

    public static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";

    public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";

    public static final String CUSTOMER_SERVICE_UNAVAILABLE =
            "CUSTOMER_SERVICE_UNAVAILABLE";

    public static final String CUSTOMER_SERVICE_ERROR =
            "CUSTOMER_SERVICE_ERROR";
    public static final String INSUFFICIENT_BALANCE=
            "INSUFFICIENT_BALANCE";
    public static final String INVALID_AMOUNT =
            "INVALID_AMOUNT";


}
