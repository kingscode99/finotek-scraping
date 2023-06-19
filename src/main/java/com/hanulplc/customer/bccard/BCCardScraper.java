package com.hanulplc.customer.bccard;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.common.FinotekLegacyScraper;
import com.hanulplc.customer.bccard.model.BCCardScrapingDAO;
import com.hanulplc.customer.bccard.page.BCCardCaseDetailPage;
import com.hanulplc.customer.bccard.page.BCCardCaseListPage;
import com.hanulplc.customer.bccard.page.BCCardLoginPage;
import com.hanulplc.util.CaseManagementNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class BCCardScraper extends FinotekCaseScraper{
    private static final String USER_ID = "hanulbc08";
        private static final String USER_PASSWORD = "hanulbc8!!";
    @Override
    protected FinotekCaseListPage login() {
        BCCardLoginPage loginPage = open(BCCardLoginPage.URL, BCCardLoginPage.class);
        return loginPage.login(USER_ID, USER_PASSWORD);
    }
}
//@Slf4j
//public class BCCardScraper extends FinotekCaseScraper implements FinotekLegacyScraper {
//
//    private static final String URL = "https://cloud.nowrms.com/login";
//    private static final String URL_CASE_LIST_PAGE = "https://cloud.nowrms.com/rgst/appl/list";
//    private static final String USER_ID = "hanulbc08";
//    private static final String USER_PASSWORD = "hanulbc8!!";
//
//    @Override
//    public void scrapeNewCases(ElementsCollection caseElements) {
//        // 의뢰를 하나씩 클릭하여 상세 페이지로 이동 후 스크래핑 시작
//        int totalSize = caseElements.size();
//
//        for (int i = 0; i < totalSize; i++) {
//            BCCardCaseListPage caseListPage = page(BCCardCaseListPage.class);
//            caseElements = caseListPage.searchCases();
//
//            log.info("현재 의뢰 수 = {}", totalSize);
//            SelenideElement currentCaseElement = caseElements.get(i);
//
//            // 물건소재지가 존재하지 않으면 스크래핑 대상에서 제외
//            String realEstateAddress = currentCaseElement.find(By.tagName("td"), 4).text();
//            if (realEstateAddress.isEmpty()) {
//                log.info("물건소재지가 존재하지 않으므로 스크래핑 대상에서 제외합니다.");
//                continue;
//            }
//
//            currentCaseElement.click(); // 의뢰를 선택
//            BCCardCaseDetailPage caseDetailPage = page(BCCardCaseDetailPage.class);
//
//            // 의뢰 고유 ID를 조회하여 기존에 등록된 건이 있으면 다음 건으로 이동
//            String caseId = caseDetailPage.getCaseId();
//            int count = BCCardScrapingDAO.findByCaseId(caseId);
//            if (count > 0) {
//                log.info("해당 사건번호를 갖는 의뢰가 이미 존재합니다 = {}", caseId);
//                back();
//                continue;
//            }
//
//            // [1. 정보등록] 화면으로 이동
//            caseDetailPage.navigateToCaseDetailTab();
//
//            // 관리번호 생성
//            String caseManagementNumber = CaseManagementNumberGenerator.generate();
//            Map<String, Object> caseMap = caseDetailPage.scrapeRegistrationDetailInfo(caseManagementNumber); // 등기 상세 정보
//            List<Map<String, Object>> customers = caseDetailPage.scrapeObligatorInfo(caseManagementNumber); // 등기의무자 정보
//            List<Map<String, Object>> estates = caseDetailPage.scrapeRealEstateInfo(caseManagementNumber); // 부동산 정보
//
//            // 데이터베이스에 의뢰 저장
//            BCCardScrapingDAO.saveNewCase(caseMap, customers, estates);
//
//            // 목록으로 돌아가기
//            back();
//        }
//
//        log.info("스크래핑 완료. 신청서 목록 페이지로 이동합니다...");
//        open(URL_CASE_LIST_PAGE);
//    }
//
//    @Override
//    protected FinotekCaseListPage login() {
//        BCCardLoginPage loginPage = open(URL, BCCardLoginPage.class);
//        return loginPage.login(USER_ID, USER_PASSWORD);
//    }
//
//    @Override
//    protected void waitForSeconds(int milliseconds) {
//        super.waitForSeconds(30_000);
//    }
//}
