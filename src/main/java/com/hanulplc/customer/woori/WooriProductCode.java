package com.hanulplc.customer.woori;

import java.util.Arrays;

public enum WooriProductCode {
    CODE_01("1", "아낌e(일반약정서)"),
    CODE_02("2", "비대면(생활자금)"),
    CODE_03("3", "비대면(구입자금)"),
    CODE_04("4", "비대면(임차반환)"),
    CODE_05("5", "비대면(타행대환)"),
    CODE_06("6", "임차말소등기"),
    CODE_07("7", "일반 전자등기"),
    CODE_08("8", "타행대환"),
    CODE_09("9", "구입자금"),
    CODE_10("10", "T보금자리론(안심전환대출)"),
    CODE_11("11", "U보금자리론(안심전환대출)"),
    CODE_12("12", "유용"),
    CODE_13("13", "추가 근저당권 설정"),
    CODE_14("14", "지상권설정"),
    CODE_15("15", "근저당권 변경(채권자)"),
    CODE_16("16", "근저당권 변경(감액)"),
    ;

    private final String code;
    private final String description;

    WooriProductCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static WooriProductCode of(String description) {
        return Arrays.stream(values())
            .filter(code -> code.description.equals(description))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("다음 상품구분 코드를 찾을 수 없음: " + description));
    }

    public String getCode() {
        return code;
    }
}
