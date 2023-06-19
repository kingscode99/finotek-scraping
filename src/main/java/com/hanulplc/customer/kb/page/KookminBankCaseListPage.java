package com.hanulplc.customer.kb.page;

import com.codeborne.selenide.ElementsCollection;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.customer.kb.model.KookminBankScrapingDAO;
import com.hanulplc.util.CaseManagementNumberGenerator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class KookminBankCaseListPage implements FinotekCaseListPage {

    public static final String URL = "https://irosf.kbstar.com:80/rgst/appl/list";

    @Override
    public FinotekCaseListPage searchByConditions() {
        // '진행상태'를 '등기의뢰'로 선택
        $("#cm9010").selectOption("등기의뢰");
        
        // 테스트용 검색 조건
//        $("#cm9010").selectOption(0);

        // '검색' 버튼 클릭
        $("#container > form > table > tbody > tr:nth-child(1) > td:nth-child(3) > input").click();
        $("body > div.blockUI.blockMsg.blockPage > center > div > img").shouldBe(visible).shouldNotBe(visible);

        return this;
    }

    @Override
    public boolean hasNoCases() {
        return $("#applList > tbody > tr.noData").exists();
    }

    @Override
    public FinotekCaseListPage scrapeCases() {
        Set<String> caseIds = scrapeCurrentAllCases();
        for (String caseId : caseIds) {
            OptionalInt count = KookminBankScrapingDAO.findByCaseId(caseId);
            if (count.isPresent() && count.getAsInt() != 0) {
                log.info("해당 사건번호를 갖는 의뢰가 이미 존재합니다 = {}", caseId);
                continue;
            }

            log.info("사건번호 {} 의뢰 스크래핑 시작...", caseId);
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
        return $$("#applList > tbody tr").stream()
            .filter(it -> {
                String 물건소재지주소 = it.find(By.tagName("td"), 6).text().replaceAll("(외|외 )(\\d|\\d{2})건", "");
                return !물건소재지주소.isEmpty();
            })
            .map(it -> it.find(By.tagName("td"), 9).getAttribute("id").replace("lgReqPsnNm_", ""))
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
        KookminBankCaseDetailPage caseDetailPage = open(String.format("https://irosf.kbstar.com:80/rgst/appl/edit/%s", caseId), KookminBankCaseDetailPage.class);
        String caseManagementNumber = CaseManagementNumberGenerator.generate();

        // 은행코드: 대환유무가 '무'이면 KBB, '무'가 아니면 KBC
        String 대환유무 = caseDetailPage.대환유무();
        String bankCode = "무".equals(대환유무) ? "KBB" : "KBC";

        // [1. 정보등록] 화면으로 이동
        caseDetailPage.navigateToFirstTab();

        Map<String, String> 금융기관_정보 = caseDetailPage.금융기관_정보();
        List<Map<String, String>> 부동산_정보 = caseDetailPage.부동산_정보(caseManagementNumber);
        List<Map<String, String>> 담보제공자_정보 = caseDetailPage.담보제공자_정보(caseManagementNumber);

        // 자담, 3자담 여부를 확인하기 위해 '담보제공자 정보' 조회 다음에 실행되도록 순서 조정
        Map<String, String> 등기_의뢰_정보 = caseDetailPage.등기_의뢰_정보(caseManagementNumber, bankCode);
        Map<String, String> 등기_상세_정보 = caseDetailPage.등기_상세_정보();

        Map<String, String> 의뢰_기본_정보 = new HashMap<>();
        의뢰_기본_정보.putAll(등기_의뢰_정보);
        의뢰_기본_정보.putAll(등기_상세_정보);
        의뢰_기본_정보.putAll(금융기관_정보);

        KookminBankScrapingDAO.saveNewCase(의뢰_기본_정보, 담보제공자_정보, 부동산_정보);
        log.info("의뢰 [{}] 스크래핑 완료", caseId);
    }
}
