package com.hanulplc.customer.woori.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.customer.woori.model.WooriScrapingDAO;
import com.hanulplc.util.CaseManagementNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class WooriCaseListPage implements FinotekCaseListPage {

    private static final String URL = "https://woori.nowrms.com/rgst/appl/list";
    private static final List<String> cachedCaseIds = new ArrayList<>();

    @Override
    public FinotekCaseListPage scrapeCases() {
        Set<String> caseIds = scrapeCurrentAllCases();
        
        // 캐싱되지 않은 사건번호들만 필터링
        List<String> targetCaseIds = caseIds.stream()
            .filter(caseId -> !cachedCaseIds.contains(caseId))
            .collect(Collectors.toList());
        log.info("신규 의뢰 id 목록 = {}", targetCaseIds);

        for (String caseId : targetCaseIds) {
            // 캐싱이 안 된 신규 의뢰라도 혹시 모르니 일단 DB에 있는지 확인
            OptionalInt count = WooriScrapingDAO.findByCaseId(caseId);
            if (count.isPresent() && count.getAsInt() != 0) {
                log.info("해당 사건번호를 갖는 의뢰가 이미 존재합니다 = {}", caseId);
                cachedCaseIds.add(caseId);
                continue;
            }

            scrapeCase(caseId);
            cachedCaseIds.add(caseId);
        }

        return open(URL, this.getClass());
    }

    private Set<String> scrapeCurrentAllCases() {
        Set<String> caseIds = new HashSet<>();

        ElementsCollection navigations = $$("#navi > p > span");
        if (navigations.isEmpty()) {
            caseIds.addAll(scrapeCurrentPageCaseIds());
            return caseIds;
        }

        int pageCount;
        SelenideElement navigateToLastPageButton = $("#navi > input:nth-child(3)");
        if (navigateToLastPageButton.exists()) {
            navigateToLastPageButton.click();
            navigateToLastPageButton.should(disappear);
            pageCount = Integer.parseInt($$("#navi > p > span").last().text());
            $("#navi > input:nth-child(1)").click();
            $("#navi > input:nth-child(1)").should(disappear);
        } else {
            pageCount = navigations.size();
        }

        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
            goToPage(pageNumber);
            caseIds.addAll(scrapeCurrentPageCaseIds());
        }

        return caseIds;
    }

    private List<String> scrapeCurrentPageCaseIds() {
        ElementsCollection currentPageCases = $$("#applList > tbody tr");
        return currentPageCases.stream()
            .filter(it -> {
                String 물건소재지주소 = it.find(By.tagName("td"), 2).text().replaceAll("(외|외 )(\\d|\\d{2})건", "");
                return !물건소재지주소.isEmpty();
            })
            .map(it -> it.find(By.tagName("td"), 6).getAttribute("id").replace("lgReqPsnNm_", ""))
            .collect(Collectors.toList());
    }

    private void goToPage(int pageNumber) {
        if (pageNumber == 1) {
            return;
        }

        $$("#navi > p > span").stream()
            .filter(it -> it.text().equals(String.valueOf(pageNumber)))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new)
            .click();

        $("img[src='/resources/images/loading.gif']").should(disappear).should(disappear);
    }

    private void scrapeCase(String caseId) {
        WooriCaseDetailPage caseDetailPage = open(String.format("https://woori.nowrms.com/rgst/appl/edit/%s", caseId), WooriCaseDetailPage.class);
        String caseManagementNumber = CaseManagementNumberGenerator.generate();

        // [1. 정보등록] 화면으로 이동
        caseDetailPage.navigateToFirstTab();

        List<Map<String, String>> 담보제공자_정보 = caseDetailPage.담보제공자_정보(caseManagementNumber);
        List<Map<String, String>> 부동산_정보 = caseDetailPage.부동산_정보(caseManagementNumber);
        Map<String, String> 등기_의뢰_정보 = caseDetailPage.등기_의뢰_정보(caseManagementNumber);
        Map<String, String> 등기_상세_정보 = caseDetailPage.등기_상세_정보();

        Map<String, String> 의뢰_기본_정보 = new HashMap<>();
        의뢰_기본_정보.putAll(등기_의뢰_정보);
        의뢰_기본_정보.putAll(등기_상세_정보);

        log.info("의뢰 기본 정보 = {}", 의뢰_기본_정보);
        log.info("담보제공자 정보 = {}", 담보제공자_정보);
        log.info("부동산 정보 = {}", 부동산_정보);

        WooriScrapingDAO.saveNewCase(의뢰_기본_정보, 담보제공자_정보, 부동산_정보);
        log.info("의뢰 [{}] 스크래핑 완료", caseId);
    }

    @Override
    public WooriCaseListPage searchByConditions() {
        $("#progStepStatsDiv > table > tbody > tr > td:nth-child(3) > a").click(); // '등기의뢰' 조회
        $("img[src='/resources/images/loading.gif']").should(disappear).should(disappear);
        return this;
    }

    @Override
    public boolean hasNoCases() {
        ElementsCollection cases = $$("#applList > tbody tr");
        return cases.size() == 1 && cases.first().text().contains("입력된 정보가 없습니다.");
    }
}
