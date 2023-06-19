package com.hanulplc.customer.bccard.page;

import com.codeborne.selenide.ElementsCollection;
import com.google.common.base.FinalizableReference;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.customer.bccard.model.BCCardScrapingDAO;
import com.hanulplc.customer.daegu.model.DaeguScrapingDAO;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.OptionalInt;
import java.util.Set;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class BCCardCaseListPage implements FinotekCaseListPage {
    private static final String URL = "https://cloud.nowrms.com/rgst/appl/list";

    @Override
    public FinotekCaseListPage scrapeCases() {
        Set<String> caseIds = scrapeCurrentAllCases();
        for (String caseId : caseIds) {
            OptionalInt count = BCCardScrapingDAO.findByCaseId(caseId);
            if (count.isPresent() && count.getAsInt() != 0) {
                log.info("해당 사건번호를 갖는 의뢰가 이미 존재합니다 = {}", caseId);
                continue;
            }

            scrapeCase(caseId);
        }
        return open(URL, this.getClass());
    }


    private Set<String> scrapeCurrentAllCases() {
        return null;
    }

    private void scrapeCase(String caseId) {
    }

    @Override
    public FinotekCaseListPage searchByConditions() {
        return null;
    }

    @Override
    public boolean hasNoCases() {
        return false;
    }
}

//public class BCCardCaseListPage implements FinotekCaseListPage {
//
//    private static final String LOADING_IMAGE = "img[src='/resources/images/loading.gif']";
//    private static final String CASES_IN_REGISTER_INFORMATION = "#progStepStatsDiv > table > tbody > tr > td:nth-child(3) > a";
//
//    @Override
//    public FinotekCaseListPage searchByConditions() {
//        // 검색 조건이 필요한 경우, 추가 바람
//        return this;
//    }
//
//    @Override
//    public boolean hasNoCases() {
//        return $(CASES_IN_REGISTER_INFORMATION).shouldBe(visible).has(text("0"));
//    }
//
//    @Override
//    public FinotekCaseListPage scrapeCases() {
//        return null;
//    }
//
//    @Override
//    public ElementsCollection searchCases() {
//        // 테스트 : 전체 의뢰
////        $("#progStepStatsDiv > table > tbody > tr > td:nth-child(1) > a").click();
//
//        // '정보등록' 상태인 의뢰들만 조회한다
//        int currentCount = Integer.parseInt($(CASES_IN_REGISTER_INFORMATION).text());
//        $(CASES_IN_REGISTER_INFORMATION).click();
//
//        // 로딩 이미지 대기
//        $(LOADING_IMAGE).shouldBe(visible).shouldBe(disappear);
//        return $$("#applList > tbody tr").shouldHave(size(currentCount));
//    }
//}
