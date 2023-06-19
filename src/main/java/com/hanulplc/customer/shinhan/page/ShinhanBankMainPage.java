package com.hanulplc.customer.shinhan.page;

import com.hanulplc.common.FinotekCaseListPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ShinhanBankMainPage {

    private static final String SHINHAN_LOGO = "#main_header > div.logo > a > img";

    public FinotekCaseListPage goToCaseListPage() {
        $(SHINHAN_LOGO).shouldBe(visible);
        actions().pause(Duration.ofSeconds(5)).perform(); // 데이터 로딩 대기 시간 부여
        executeJavaScript("goList('B10')"); // '정보검수' 클릭
        return page(ShinhanBankCaseListPage.class);
    }
}
