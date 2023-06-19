package com.hanulplc.customer.daegu.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.hanulplc.customer.daegu.DaeguProductCode;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class DaeguCaseDetailPage {

    private boolean isSecurityProviderEqualsToMortgager = false;

    public void navigateToFirstTab() {
        // =======================================================
        // 이미 진행된 건에 대하여 '1. 등기 정보 검수' 탭으로 이동
        $("#progStepDiv > ul > li:nth-child(1)").click();
        $("#applDto > div:nth-child(45) > h3").shouldBe(visible);
        // =======================================================
    }

    public Map<String, String> 등기_의뢰_정보(String caseManagementNumber) {
        String 의뢰번호 = $("#rgst_req_no").getValue();
        String 금융기관명 = $("#fncl_inst_nm").getValue();
        String 지점명 = $("#eai_fncl_br_nm").getValue();
        String 담당자명 = $("#applDto > div:nth-child(45) > table > tbody > tr:nth-child(2) > td:nth-child(2) > input").getValue();
        String 담당자_연락처 = $("#applDto > div:nth-child(45) > table > tbody > tr:nth-child(2) > td:nth-child(4) > input").getValue();
        String 담당자_팩스번호 = $("#applDto > div:nth-child(45) > table > tbody > tr:nth-child(2) > td:nth-child(6) > input").getValue();

        log.info("금융기관명 = {}", 금융기관명);
        log.info("지점명 = {}", 지점명);
        log.info("담당자명 = {}", 담당자명);
        log.info("담당자 연락처 = {}", 담당자_연락처);
        log.info("담당자 팩스번호 = {}", 담당자_팩스번호);

        Map<String, String> caseBasicInfo = new HashMap<>();
        caseBasicInfo.put("ACCNT_CD", "DGB");
        caseBasicInfo.put("RGSTRN_REQ_NUM", 의뢰번호);
        caseBasicInfo.put("BRANCH_NM", 지점명);
        caseBasicInfo.put("BRANCH_CHRGR_NM", 담당자명);
        caseBasicInfo.put("BRANCH_CHRGR_TEL_NUM", 담당자_연락처);
        caseBasicInfo.put("BRANCH_CHRGR_FAX_NUM", 담당자_팩스번호);
        caseBasicInfo.put("CASE_CD", "0");
        caseBasicInfo.put("PROC_ST_CD", "10");
        caseBasicInfo.put("CASE_ST_CD", "1");
        caseBasicInfo.put("TEAM_CD", "41");
        caseBasicInfo.put("CASE_RGSTR_NM", "AUTO");
        caseBasicInfo.put("INVST_INST_MGMT_NUM", caseManagementNumber);

        return caseBasicInfo;
    }

    public Map<String, String> 등기_상세_정보() {
        String 전자등기구분 = $("#applDto > div:nth-child(46) > table > tbody > tr:nth-child(2) > td:nth-child(2) > input").getValue();
        String 등기유형 = $("#applDto > div:nth-child(46) > table > tbody > tr:nth-child(1) > td:nth-child(4) > input").getValue().replace(" ", "");
        String 등기원인 = $("#applDto > div:nth-child(46) > table > tbody > tr:nth-child(1) > td:nth-child(6) > input").getValue();
        String 등기원인_연월일 = $("#rgst_caus_date").getValue(); // 설정계약일자
        String 등기희망일자 = $("#rgst_caus_date").getValue();
        String 대출실행일자 = $("#loan_exe_date").getValue();
        String 채권최고액 = $("#applDto > div:nth-child(46) > table > tbody > tr.item_2810.hide > td > input.price.readonly").getValue();

        log.info("전자등기구분 = {}", 전자등기구분);
        log.info("등기유형 = {}", 등기유형.replace(" ", ""));
        log.info("등기원인 = {}", 등기원인);
        log.info("등기원인 연월일 = {}", 등기원인_연월일);
        log.info("등기희망일자 = {}", 등기희망일자);
        log.info("대출실행일자 = {}", 대출실행일자);
        log.info("채권최고액 = {}", 채권최고액);

        String registrationTypeCode = "20";
        if (등기유형.equals("근저당권말소")) {
            registrationTypeCode = "40";
        }
        if (전자등기구분.contains("구입자금")) {
            registrationTypeCode = "10";
        }

        Map<String, String> caseDetailInfo = new HashMap<>();
        caseDetailInfo.put("RGSTRN_TYP_CD", registrationTypeCode);
        caseDetailInfo.put("RGSTRN_TYP_DETAIL", DaeguProductCode.of(전자등기구분).getCode());
        caseDetailInfo.put("RGSTRN_TYP_SUB_CD", "2001");
        caseDetailInfo.put("CO_CASE_YN", "N");
        caseDetailInfo.put("RGSTRN_RSN_DT", 등기원인_연월일);
        caseDetailInfo.put("RGSTRN_DESIRE_DT", 등기희망일자);
        caseDetailInfo.put("LOAN_EXEC_DT", 대출실행일자);
        caseDetailInfo.put("CREDIT_MAX_AMT", 채권최고액.replace(",", ""));
        caseDetailInfo.put("RGSTRN_RSN_TYP_CD", isSecurityProviderEqualsToMortgager ? "5" : "6"); // 자담(5), 3자담(6)

        return caseDetailInfo;
    }

    public List<Map<String, String>> 담보제공자_정보(String caseManagementNumber) {
        // 담보제공자와 채무자의 동일 여부를 구분하기 위해 채무자 정보를 먼저 스크래핑 해야 함
        Map<String, String> 채무자_정보 = 채무자_정보();
        String 채무자_성명 = 채무자_정보.get("채무자_성명");
        String 채무자_주소 = 채무자_정보.get("채무자_주소");

        ElementsCollection securityProviders = $$("#oblgList tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("담보제공자 수 = {}명", securityProviders.size());

        List<Map<String, String>> securityProvidersInfos = new ArrayList<>();
        for (SelenideElement securityProvider : securityProviders) {
            String 채무자_동일_여부 = securityProvider.find(By.tagName("td"), 0).text();
            String 담보제공자_성명 = securityProvider.find(By.tagName("td"), 1).text();
            String 담보제공자_주소 = securityProvider.find(By.tagName("td"), 5).text();

            securityProvider.click();
            switchTo().frame($("iframe.popupIframe"));

            String 담보제공자_주민등록번호 = $("#oblg_corp_no_front").getValue() + $("#oblg_corp_no_rear").getValue();
            String 담보제공자_연락처 = $("#oblg_hpn_no1").getSelectedText() + "-"
                + $("#oblg_hpn_no2").val() + "-"
                + $("#oblg_hpn_no3").val();

            switchTo().defaultContent();
            $("body > div.popupDiv.hide > div > h3 > img").click();

            log.info("채무자 동일 여부 = {}", 채무자_동일_여부);
            log.info("담보제공자 성명 = {}", 담보제공자_성명);
            log.info("담보제공자 주민등록번호 = {}", 담보제공자_주민등록번호);
            log.info("담보제공자 연락처 = {}", 담보제공자_연락처);
            log.info("담보제공자 주소 = {}", 담보제공자_주소);

            Map<String, String> 채무자 = new HashMap<>();
            Map<String, String> 설정자 = new HashMap<>();

            if (securityProviders.size() > 1) { // 담보제공자 정보에 2명 이상이 존재하는 경우
                if (채무자_동일_여부.equals("동일")) { // 현재 담보제공자가 채무자와 동일한 경우, 채무자로 저장
                    log.info("담보제공자 2명 이상, 채무자와 동일함");
                    isSecurityProviderEqualsToMortgager = true;

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1"); // 고객유형 : 채무자(1)
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_주소);

                    securityProvidersInfos.add(채무자);

                } else { // 현재 담보제공자가 채무자와 동일하지 않은 경우, 설정자로 저장
                    log.info("담보제공자 2명 이상, 채무자와 동일하지 않음");

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2"); // 고객유형 : 설정자(2)
                    설정자.put("CUST_SEQ_NUM", "2");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "off");
                    설정자.put("CUST_ADDR", 담보제공자_주소);

                    securityProvidersInfos.add(설정자);
                }

            } else { // 담보제공자 정보에 1명만 존재하는 경우
                if (채무자_동일_여부.equals("동일")) { // 현재 담보제공자가 채무자와 동일한 경우, 채무자와 설정자 모두 동일하게 저장
                    log.info("담보제공자 1명, 채무자와 동일함");
                    isSecurityProviderEqualsToMortgager = true;

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1");
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_주소);

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2");
                    설정자.put("CUST_SEQ_NUM", "2");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "on");
                    설정자.put("CUST_ADDR", 담보제공자_주소);

                } else { // 현재 담보제공자가 채무자와 동일하지 않을 경우, 채무자는 빈 값 / 담보제공자는 설정자로 저장
                    log.info("담보제공자 1명, 채무자와 동일하지 않음");

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1");
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", "0000000000000");
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_주소);

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2");
                    설정자.put("CUST_SEQ_NUM", "1");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);  // 주민등록번호
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "off");
                    설정자.put("CUST_ADDR", 담보제공자_주소);
                }

                securityProvidersInfos.add(채무자);
                securityProvidersInfos.add(설정자);
            }
        }

        return securityProvidersInfos;
    }

    public Map<String, String> 채무자_정보() {
        ElementsCollection debtors = $$("#dbtrList tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("채무자 수 = {}명", debtors.size());

        if (debtors.size() > 1) {
            log.warn("채무자 수가 2명 이상입니다. (로직 추가 필요)");
            throw new RuntimeException();
        }

        SelenideElement debtorElement = debtors.first();
        String 채무자_성명 = debtorElement.find("td", 0).text();
        String 채무자_주소 = debtorElement.find("td", 1).text();

        log.info("채무자 성명 = {}", 채무자_성명);
        log.info("채무자 주소 = {}", 채무자_주소);

        Map<String, String> debtorInfo = new HashMap<>();
        debtorInfo.put("채무자_성명", 채무자_성명);
        debtorInfo.put("채무자_주소", 채무자_주소);

        return debtorInfo;
    }

    public List<Map<String, String>> 부동산_정보(String caseManagementNumber) {
        List<Map<String, String>> realEstatesInfos = new ArrayList<>();

        ElementsCollection realEstates = $$("#prptyList tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("부동산 수 = {}", realEstates.size());

        int sequence = 1;
        for (SelenideElement realEstate : realEstates) {
            ElementsCollection elements = realEstate.findAll(By.tagName("td"));
            String 부동산_구분 = elements.get(0).getText();
            String 부동산_고유번호 = elements.get(1).getText();
            String 관할등기소 = elements.get(3).getText();
            String 부동산_주소 = elements.get(4).getText();

            log.info("부동산 구분 = {}", 부동산_구분);
            log.info("부동산 고유번호 = {}", 부동산_고유번호);
            log.info("관할등기소 = {}", 관할등기소);
            log.info("부동산 주소 = {}", 부동산_주소);

            Map<String, String> realEstateInfo = new HashMap<>();

            if (부동산_구분.contains("건물")) {
                realEstateInfo.put("GDS_TYP_CD_1", "1");
                realEstateInfo.put("GDS_TYP_CD_2", 부동산_구분.equals("집합건물") ? "1" : "3");
            } else {
                realEstateInfo.put("GDS_TYP_CD_1", "2");
                realEstateInfo.put("GDS_TYP_CD_2", 부동산_구분.equals("토지") ? "4" : "5");
            }

            realEstateInfo.put("EST_SEQ_NUM", String.valueOf(sequence++));
            realEstateInfo.put("EST_ORGN_NUM", 부동산_고유번호);
            realEstateInfo.put("STRT_NEW_ADDR", 부동산_주소);
            realEstateInfo.put("SIDO", (부동산_주소.length() == 0 ? "" : 부동산_주소.split(" ")[0]));
            realEstateInfo.put("LAND_LOT_CNT", "1");
            realEstateInfo.put("INVST_INST_MGMT_NUM", caseManagementNumber);

            realEstatesInfos.add(realEstateInfo);
        }

        return realEstatesInfos;
    }

}
