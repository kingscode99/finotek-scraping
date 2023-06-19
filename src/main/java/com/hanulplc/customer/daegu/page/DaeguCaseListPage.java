package com.hanulplc.customer.daegu.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.customer.daegu.model.DaeguScrapingDAO;
import com.hanulplc.util.CaseManagementNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class DaeguCaseListPage implements FinotekCaseListPage {

    public static final String URL = "https://daegu.nowrms.com/rgst/appl/list";

    @Override
    public FinotekCaseListPage scrapeCases() {
        Set<String> caseIds = scrapeCurrentAllCases();
        for (String caseId : caseIds) {
            OptionalInt count = DaeguScrapingDAO.findByCaseId(caseId);
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
                String 물건소재지주소 = it.find(By.tagName("td"), 4).text().replaceAll("(외|외 )(\\d|\\d{2})건", "");
                return !물건소재지주소.isEmpty();
            })
            .map(it -> it.find(By.tagName("td"), 7).getAttribute("id").replace("lgReqPsnNm_", ""))
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
        DaeguCaseDetailPage caseDetailPage = open(String.format("https://daegu.nowrms.com/rgst/appl/edit/%s", caseId), DaeguCaseDetailPage.class);
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

        DaeguScrapingDAO.saveNewCase(의뢰_기본_정보, 담보제공자_정보, 부동산_정보);
        log.info("의뢰 [{}] 스크래핑 완료", caseId);
    }

    @Override
    public FinotekCaseListPage searchByConditions() {
        String dateAfterMonths = LocalDate.now().plusMonths(3).format(DateTimeFormatter.ISO_LOCAL_DATE);
        executeJavaScript(String.format("arguments[0].value = '%s'", dateAfterMonths),
            $(By.xpath("/html/body/div[1]/div[2]/form/table/tbody/tr[1]/td[1]/input[2]")));
        $("#progStepStatsDiv > table > tbody > tr > td:nth-child(3) > a").click(); // '등기의뢰' 클릭
        $("img[src='/resources/images/loading.gif']").should(disappear).should(disappear);
        return this;
    }

    @Override
    public boolean hasNoCases() {
        return $("#progStepStatsDiv > table > tbody > tr > td:nth-child(3) > a").text().equals("0");
    }

    private SelenideElement getCurrentCaseCountElement() {
//        return $("#progStepStatsDiv > table > tbody > tr > td:nth-child(1)"); // 개발 테스트용 : 전체
//        return $("#progStepStatsDiv > table > tbody > tr > td:nth-child(4)"); // '등기정보검수'
        return $("#progStepStatsDiv > table > tbody > tr > td:nth-child(3)"); // '등기의뢰'
    }

    private int getCurrentCaseCount() {
        return Integer.parseInt(getCurrentCaseCountElement().text());
    }

    private int getCanceledCaseCount() {
        return Integer.parseInt($("#progStepStatsDiv > table > tbody > tr > td:nth-child(13) > a").text());
    }
}
