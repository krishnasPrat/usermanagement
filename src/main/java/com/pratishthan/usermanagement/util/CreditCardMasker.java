package com.pratishthan.usermanagement.util;

public final class CreditCardMasker {

    private CreditCardMasker() {
    }

    public static String mask(String creditCardNo) {
        if (creditCardNo == null || creditCardNo.isBlank()) {
            return creditCardNo;
        }
        String trimmed = creditCardNo.trim();
        if (trimmed.length() <= 3) {
            return trimmed;
        }
        String first = trimmed.substring(0, 1);
        String lastTwo = trimmed.substring(trimmed.length() - 2);
        return first + "*".repeat(trimmed.length() - 3) + lastTwo;
    }
}
