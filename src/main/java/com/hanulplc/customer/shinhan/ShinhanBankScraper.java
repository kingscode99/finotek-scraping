package com.hanulplc.customer.shinhan;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.common.FinotekLegacyScraper;
import com.hanulplc.customer.shinhan.model.ShinhanBankScrapingDAO;
import com.hanulplc.customer.shinhan.page.ShinhanBankCaseDetailPage;
import com.hanulplc.customer.shinhan.page.ShinhanBankCaseListPage;
import com.hanulplc.customer.shinhan.page.ShinhanBankLoginPage;
import com.hanulplc.customer.shinhan.page.ShinhanBankMainPage;
import com.hanulplc.util.CaseManagementNumberGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class ShinhanBankScraper extends FinotekCaseScraper implements FinotekLegacyScraper {

    private static final String CERTIFICATE_PASSWORD = "hanul/0514";

    @Override
    protected FinotekCaseListPage login() {
        ShinhanBankLoginPage loginPage = open(ShinhanBankLoginPage.URL, ShinhanBankLoginPage.class);
        ShinhanBankMainPage mainPage = loginPage.login(CERTIFICATE_PASSWORD);
        return mainPage.goToCaseListPage();
    }

    @Override
    public void scrapeNewCases(ElementsCollection caseElements) {
        for (int i = 0; i < caseElements.size(); i++) {
            ShinhanBankCaseListPage caseListPage = page(ShinhanBankCaseListPage.class);
            caseElements = caseListPage.searchCases();

            SelenideElement currentCaseElement = caseElements.get(i);
//            String caseId = currentCaseElement.find(By.tagName("td"), 1).text();
//            String 담보물건지 = currentCaseElement.find(By.tagName("td"), 5).text().replaceAll("(외|외 )(\\d|\\d{2})건", "");

            String caseId = "12345";
            String 담보물건지 = "있음";

            log.info("case id = {}, 담보물건지 = {}", caseId, 담보물건지);

            OptionalInt count = ShinhanBankScrapingDAO.findByCaseId(caseId);
            if (count.isPresent() && count.getAsInt() != 0) {
                log.info("해당 사건번호를 갖는 의뢰가 이미 존재합니다 = {}", caseId);
                continue;
            }

            if (담보물건지.isEmpty()) {
                log.info("담보물건지 주소가 없으므로 스크래핑을 생략합니다 = {}", caseId);
                continue;
            }

            $("#DataSet > td:nth-child(3) > a").click();

//            currentCaseElement.find("td > a", 1).click();
            ShinhanBankCaseDetailPage caseDetailPage = page(ShinhanBankCaseDetailPage.class);
            String caseManagementNumber = CaseManagementNumberGenerator.generate();

            // [1. 정보등록] 화면으로 이동
            caseDetailPage.navigateToFirstTab();

            List<Map<String, String>> 부동산_정보 = caseDetailPage.부동산_정보_new(caseManagementNumber);
            List<Map<String, String>> 담보제공자_정보 = caseDetailPage.담보제공자_정보_new(caseManagementNumber);

//            List<Map<String, String>> 부동산_정보 = caseDetailPage.부동산_정보(caseManagementNumber);
//            List<Map<String, String>> 담보제공자_정보 = caseDetailPage.담보제공자_정보(caseManagementNumber);

            // 자담, 3자담 여부를 확인하기 위해 '담보제공자 정보' 조회 다음에 실행되도록 순서 조정
            Map<String, String> 등기_의뢰_정보 = caseDetailPage.등기_의뢰_정보(caseManagementNumber);
            Map<String, String> 등기_상세_정보 = caseDetailPage.등기_상세_정보();

            Map<String, String> 의뢰_기본_정보 = new HashMap<>();
            의뢰_기본_정보.putAll(등기_의뢰_정보);
            의뢰_기본_정보.putAll(등기_상세_정보);

            ShinhanBankScrapingDAO.saveNewCase(의뢰_기본_정보, 담보제공자_정보, 부동산_정보);
        }

        log.info("스크래핑 완료. 신청서 목록 페이지로 이동합니다...");
    }
}
