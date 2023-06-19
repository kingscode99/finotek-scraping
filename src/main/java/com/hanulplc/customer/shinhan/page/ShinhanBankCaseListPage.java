package com.hanulplc.customer.shinhan.page;

import com.codeborne.selenide.ElementsCollection;
import com.hanulplc.common.FinotekCaseListPage;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Selenide.*;

public class ShinhanBankCaseListPage implements FinotekCaseListPage {

    @Override
    public FinotekCaseListPage searchByConditions() {
        actions().pause(Duration.ofSeconds(5)).perform(); // 데이터 로딩 대기 시간 부여

        // !! 테스트용 !!
        $("#gnb_menu > ul > li:nth-child(2) > a > img").click();
        actions().pause(Duration.ofSeconds(5)).perform(); // 데이터 로딩 대기 시간 부여

        return this;
    }

    @Override
    public boolean hasNoCases() {
        // !! 테스트용 !!
        return $$("#DataSet tr").shouldHave(sizeGreaterThanOrEqual(1))
            .first()
            .text()
            .contains("자료가 존재하지 않습니다.");

//        return $$("#ReceiveDataSet").shouldHave(sizeGreaterThanOrEqual(1))
//            .first()
//            .text()
//            .contains("자료가 존재하지 않습니다.");
    }

    @Override
    public ElementsCollection searchCases() {
        // !! 테스트용 !!
        return $$("#DataSet tr").shouldHave(sizeGreaterThanOrEqual(1));
//        return $$("#ReceiveDataSet tr").shouldHave(sizeGreaterThanOrEqual(1));
    }

    @Override
    public FinotekCaseListPage scrapeCases() {
        return null;
    }
}
