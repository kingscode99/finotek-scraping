package com.hanulplc.customer.nonghyup.mutual.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.customer.nonghyup.mutual.model.NonghyupMutualScrapingDAO;
import com.hanulplc.util.CaseManagementNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class NonghyupMutualCaseListPage implements FinotekCaseListPage {

    public static final String URL = "https://nhers.nonghyupit.com:8150/rgst/appl/list?prog_step=ST_10";

    @Override
    public FinotekCaseListPage searchByConditions() {
        // '기준일자'를 '등기의뢰일자'로 선택
        $("select[name='sc']").selectOption("등기의뢰일자");

        // TODO 테스트용 검색 조건
//        $("#cm9010").selectOption(0);
        
        // '검색' 버튼 클릭
        $("#searchForm > div > table > tbody > tr:nth-child(1) > td:nth-child(3) > input").click();

        return this;
    }

    @Override
    public boolean hasNoCases() {
        return WebDriverRunner.source().contains("해당 정보가 존재하지 않습니다.");
    }

    public void searchAll() {
        // 테스트용 전체 선택
        SelenideElement searchButton = $("#searchForm > div > table > tbody > tr:nth-child(1) > td:nth-child(3) > input");
        searchButton.shouldBe(visible);
        Select select = new Select($("#cm9010"));
        select.selectByIndex(0);
        searchButton.click();
    }

    @Override
    public FinotekCaseListPage scrapeCases() {
        Set<String> caseIds = scrapeCurrentAllCases();
        for (String caseId : caseIds) {
            OptionalInt count = NonghyupMutualScrapingDAO.findByCaseId(caseId);
            if (count.isPresent() && count.getAsInt() != 0) {
                log.info("해당 사건번호를 갖는 의뢰가 이미 존재합니다 = {}", caseId);
                continue;
            }

            scrapeCase(caseId);
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

        int pageCount = navigations.size();
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
                String 물건소재지주소 = it.find(By.tagName("td"), 5).text().replaceAll("(외|외 )(\\d|\\d{2})건", "");
                return !물건소재지주소.isEmpty();
            })
            .map(it -> it.find(By.tagName("td"), 10).getAttribute("id").replace("lgReqPsnNm_", ""))
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
        NonghyupMutualCaseDetailPage caseDetailPage = open(
            String.format("https://nhers.nonghyupit.com:8150/rgst/appl/edit/%s", caseId),
            NonghyupMutualCaseDetailPage.class
        );

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

        NonghyupMutualScrapingDAO.saveNewCase(의뢰_기본_정보, 담보제공자_정보, 부동산_정보);
        log.info("의뢰 [{}] 스크래핑 완료", caseId);
    }
}
