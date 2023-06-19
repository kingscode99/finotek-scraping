package com.hanulplc.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 한울 관리번호 생성기
 */
public class CaseManagementNumberGenerator {

    private static final Random RANDOM = new Random();

    private CaseManagementNumberGenerator() {
    }

    public static String generate() {
        String currentLocalDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNumber = RANDOM.nextInt(900000) + 100000;
        return currentLocalDateTime + randomNumber;
    }

}
