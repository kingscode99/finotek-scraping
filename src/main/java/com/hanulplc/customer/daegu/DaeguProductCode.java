package com.hanulplc.customer.daegu;

import java.util.Arrays;

public enum DaeguProductCode {
    CONTACT("40", "대면"),
    UNTACT_NORMAL("41", "비대면-일반"),
    UNTACT_PURCHASING_FUNDS("42", "비대면-구입자금"),
    UNTACT_OTHER_BANK_REDEMPTION("43", "비대면-타행대환"),
    ;

    private final String code;
    private final String description;

    DaeguProductCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DaeguProductCode of(String description) {
        return Arrays.stream(values())
            .filter(code -> code.description.equals(description))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    public String getCode() {
        return code;
    }
}
