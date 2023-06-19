package com.hanulplc.customer.bccard.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.*;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@Slf4j
public class BCCardCaseDetailPage {

    public void navigateToCaseDetailTab() {
        $("#progStepDiv > ul > li:nth-child(1)").shouldBe(visible).click();
        $("#applDto > div:nth-child(36)").shouldBe(visible);
        webdriver().shouldHave(urlContaining("/rgst/appl/"));
    }

    public Map<String, Object> scrapeRegistrationDetailInfo(String caseManagementNumber) {
        String caseId = $("#rgst_req_no").val();
        String managerName = $("#req_br_psn_nm").val(); // 담당자 이름
        String registrationType = $("#applDto > div:nth-child(37) > table > tbody > tr:nth-child(1) > td:nth-child(2)").text(); // 등기유형
        String registrationCauseDate = $("#rgst_caus_date").val(); // 등기원인일자
        String registrationDesiredDate = $("#rgst_req_date").val(); // 등기접수희망일자
        String bondMaxAmount = $("#bond_max_amt_2911").val(); // 채권액 (채권최고액)
        log.info("====== 담당자 정보 ======");
        log.info("의뢰 고유 ID = {}", caseId);
        log.info("담당자 이름 = {}", managerName);
        log.info("등기유형 = {}", registrationType);
        log.info("등기원인일자 = {}", registrationCauseDate);
        log.info("등기접수희망일자 = {}", registrationDesiredDate);
        log.info("채권최고액 = {}", bondMaxAmount);

        Map<String, Object> caseMap = new HashMap<>();
        caseMap.put("INVST_INST_MGMT_NUM", caseManagementNumber);
        caseMap.put("ACCNT_CD", "BCC"); // 거래처코드 (BCC: 비씨카드)
        caseMap.put("RGSTRN_REQ_NUM", caseId); // 의뢰 고유 id
        caseMap.put("BRANCH_CHRGR_NM", managerName); // 담당자
        caseMap.put("PROC_ST_CD", "10");
        caseMap.put("CASE_CD", "0");
        caseMap.put("CASE_ST_CD", "1");
        caseMap.put("TEAM_CD", "41");
        caseMap.put("CASE_RGSTR_NM", "AUTO");
        caseMap.put("CO_CASE_YN", "N");
        caseMap.put("RGSTRN_TYP_SUB_CD", "2001");
        caseMap.put("STLMNT_ON_OFF_CL_CD", "1"); // 1: ON, 2: OFF
        caseMap.put("VENDOR_REQ_YN", "N");
        caseMap.put("RGSTRN_APPLY_TYP_CD", "1");
        caseMap.put("RGSTRN_TYP_CD", "25"); // 25: 질권설정
        caseMap.put("RGSTRN_RSN_TYP_CD", "10"); // 10: 질권
        caseMap.put("RGSTRN_RSN_DT", registrationCauseDate); // 등기원인일자
        caseMap.put("RGSTRN_DESIRE_DT", registrationDesiredDate); // 등기접수희망일자
        caseMap.put("LOAN_EXEC_DT", registrationDesiredDate); // 기표일
        caseMap.put("CREDIT_MAX_AMT", (bondMaxAmount != null) ? bondMaxAmount.replace(",", "") : ""); // 기표일

        return caseMap;
    }

    public List<Map<String, Object>> scrapeRealEstateInfo(String caseManagementNumber) {
        // 부동산 정보가 몇 건 있는지 확인
        ElementsCollection estateElements = $$("#prptyList > tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("부동산 물건 수 = {}", estateElements.size());

        List<Map<String, Object>> estates = new ArrayList<>();
        int sequence = 1;
        for (SelenideElement estateElement : estateElements) {
            String realEstateType = estateElement.find("td:nth-child(1)").text();
            String realEstateUniqueNumber = estateElement.find("td:nth-child(2)").text();
            String realEstateAddress = estateElement.find("td:nth-child(3)").text();
            log.info("====== 부동산 정보 ======");
            log.info("부동산 구분 = {}", realEstateType);
            log.info("부동산 고유번호 = {}", realEstateUniqueNumber);
            log.info("부동산 주소 = {}", realEstateAddress);

            Map<String, Object> estateMap = new HashMap<>();
            estateMap.put("INVST_INST_MGMT_NUM", caseManagementNumber); // 관리번호
            estateMap.put("EST_SEQ_NUM", sequence++); // 시퀀스
            estateMap.put("GDS_TYP_CD_1", realEstateType.equals("집합건물") ? "1" : "2"); // 물건종류1 (1: 주택, 2: 주택 외)
            estateMap.put("GDS_TYP_CD_2", realEstateType.equals("집합건물") ? "1" : "5"); // 물건종류2 (1: 집합건물, 2: 건물+토지, 3: 건물, 4: 토지, 5: 기타)
            estateMap.put("EST_ORGN_NUM", realEstateUniqueNumber); // 부동산 고유번호
            estateMap.put("SIDO", realEstateAddress.split(" ")[0]); // 주소의 시/도
            estateMap.put("STRT_NEW_ADDR", realEstateAddress); // 나머지 주소

            estates.add(estateMap);
        }

        return estates;
    }

    public List<Map<String, Object>> scrapeObligatorInfo(String caseManagementNumber) {
        boolean isDebtor = $(By.name("btn_dbtr_yn")).isSelected();
        String obligatorName = $("#oblgList > tbody > tr > td:nth-child(2)").text();
        String obligatorResidentNumber = $("#oblgList > tbody > tr > td:nth-child(3)").text();
        String obligatorAddress = $("#oblgList > tbody > tr > td:nth-child(5)").getText();
        log.info("======= 등기의무자 정보 ======");
        log.info("채무자 여부 = {}", isDebtor);
        log.info("등기의무자 성명 = {}", obligatorName);
        log.info("등기의무자 (주민)등록번호 = {}", obligatorResidentNumber);
        log.info("등기의무자 주소 = {}", obligatorAddress);
        log.info("개인/법인 여부 = {}", isPersonalObligator(obligatorResidentNumber) ? "개인" : "법인"); // (주민)등록번호로 개인/법인 확인

        // 채무자, 설정자 둘 다 넣어야 함
        List<Map<String, Object>> customers = new ArrayList<>();
        for (int index = 1; index <= 2; index++) {
            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("INVST_INST_MGMT_NUM", caseManagementNumber); // 관리번호
            customerMap.put("CUST_SEQ_NUM", index); // 시퀀스
            customerMap.put("CUST_CL_CD", index); // 고객유형 (1: 채무자, 2: 설정자)
            customerMap.put("CUST_NM", obligatorName); // 이름
            customerMap.put("CUST_CTZ_NUM", obligatorResidentNumber.replace("-", "")); // (주민)등록번호
            customerMap.put("CUST_ADDR", obligatorAddress); // 주소
            customerMap.put("LOCAL_FORGNER_CD", "1"); // 내/외국인 (1: 내국인, 2: 외국인)
            customerMap.put("PSNL_CPRTN_CD", isPersonalObligator(obligatorResidentNumber) ? "1" : "2"); // 개인/법인 (1: 개인, 2: 법인)
            customerMap.put("SAME_STTLMNTR", isDebtor ? "on" : "off"); // 채무자 동일여부

            customers.add(customerMap);
        }

        return customers;
    }

    private boolean isPersonalObligator(String residentNumber) {
        /*
        법인등록번호는 총 13자리의 숫자로 구성되어 있다.
        ①②③④⑤⑥-⑦⑧⑨⑩⑪⑫⑬

        1 ~ 4번째 자리 : 등기관서번호(4자리)
            서울중앙지방법원 등기국: 1101
            서울동부지방법원 강동등기소: 2441
            인천지방밥원 김포등기소: 1244
            수원지방법원 용인등기소: 1345
            대전지방법원 등기과: 1601
            대전지방법원 세종등기소: 1647
            대구지방법원 예천등기소: 1755
            부산지방법원 부산진등기소: 1841
            광주지방법원 여수등기소: 2047
            제주지방법원 서귀포등기소: 2241
         */
        List<String> registryOfficeCodes
                = Arrays.asList("1101", "2441", "1244", "1345", "1601", "1647", "1755", "1841", "2047", "2241");

        return registryOfficeCodes
                .stream()
                .noneMatch(residentNumber::startsWith);
    }

    private boolean isEmpty(ElementsCollection cases) {
        return cases.size() == 1 && cases.first().text().equals("입력된 정보가 없습니다.");
    }

    public String getCaseId() {
        return $("#rgst_req_no").val();
    }
}
