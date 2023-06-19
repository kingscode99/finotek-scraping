package com.hanulplc.customer.shinhan.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;

@Slf4j
public class ShinhanBankCaseDetailPage {

    public void navigateToFirstTab() {
        // =======================================================
        // 이미 진행된 건에 대하여 '1. 등기 정보 검수' 탭으로 이동
        actions().pause(Duration.ofSeconds(5)).perform();
        $("#contents > div.tab_box > a.tab01_off").click();
//        $("#contents > div.tab_box > a.tab01_on").shouldBe(visible);
        // =======================================================
    }

    public Map<String, String> 등기_의뢰_정보(String caseManagementNumber) {
        String 의뢰번호 = $("#contents > div:nth-child(4) > table > tbody > tr > td:nth-child(1)").getValue();
        String 지점명 = $("#BR_NM").text();
        String 담당자명 = $("#BR_MNGR_NM").text();
        String 담당자_연락처 = $("#BR_TEL").text();

        log.info("지점명 = {}", 지점명);
        log.info("담당자명 = {}", 담당자명);
        log.info("담당자 연락처 = {}", 담당자_연락처);

        Map<String, String> caseBasicInfo = new HashMap<>();
        caseBasicInfo.put("ACCNT_CD", "SHB");
        caseBasicInfo.put("RGSTRN_REQ_NUM", 의뢰번호);
        caseBasicInfo.put("BRANCH_NM", 지점명);
        caseBasicInfo.put("BRANCH_CHRGR_NM", 담당자명);
        caseBasicInfo.put("BRANCH_CHRGR_TEL_NUM", 담당자_연락처);
        caseBasicInfo.put("CASE_CD", "0");
        caseBasicInfo.put("PROC_ST_CD", "10");
        caseBasicInfo.put("CASE_ST_CD", "1");
        caseBasicInfo.put("TEAM_CD", "41");
        caseBasicInfo.put("CASE_RGSTR_NM", "AUTO");
        caseBasicInfo.put("INVST_INST_MGMT_NUM", caseManagementNumber);

        return caseBasicInfo;
    }

    public Map<String, String> 등기_상세_정보() {
        String 등기유형 = $("#YOR_D_AND_U > tr:nth-child(1) > td > input").getValue();
        String 등기원인_연월일 = $("#REG_DRDT").getValue();
        String 등기희망일자 = $("#REG_HOPE_DT").getValue();
        String 채권최고액 = $("#CRET_BOND_MAX_AMT").getValue();
        String 공사확약정보 = $("#CRET_RNK_NO").getValue();

        log.info("등기유형 = {}", 등기유형.replace(" ", ""));
        log.info("등기원인 연월일 = {}", 등기원인_연월일);
        log.info("등기희망일자 = {}", 등기희망일자);
        log.info("채권최고액 = {}", 채권최고액);
        log.info("공사확약정보 = {}", 공사확약정보);

        Map<String, String> caseDetailInfo = new HashMap<>();
        caseDetailInfo.put("RGSTRN_TYP_CD", "20");
        caseDetailInfo.put("RGSTRN_TYP_SUB_CD", "2001");
        caseDetailInfo.put("CO_CASE_YN", "N");
        caseDetailInfo.put("RGSTRN_RSN_DT", 등기원인_연월일);
        caseDetailInfo.put("LOAN_EXEC_DT", 등기희망일자);
        caseDetailInfo.put("CREDIT_MAX_AMT", 채권최고액.replace(",", ""));
        caseDetailInfo.put("RGSTRN_RSN_TYP_CD", "5");
        caseDetailInfo.put("RGSTRN_RANK", 공사확약정보);

        return caseDetailInfo;
    }

    public List<Map<String, String>> 담보제공자_정보_new(String caseManagementNumber) {
        // 담보제공자와 채무자의 동일 여부를 구분하기 위해 채무자 정보를 먼저 스크래핑 해야 함
        Map<String, String> 채무자_정보 = 채무자_정보_new();
        String 채무자_성명 = 채무자_정보.get("채무자_성명");
        String 채무자_주소 = 채무자_정보.get("채무자_주소");
        String 채무자_상세주소 = 채무자_정보.get("채무자_상세주소");
        String 채무자_전체_주소 = 채무자_주소 + " " + 채무자_상세주소;

        ElementsCollection securityProviders = $$("#dmb_info > div.default_board").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("담보제공자 수 = {}명", securityProviders.size());

        // 담보물건 정보도 여기에 있음
        List<Map<String, String>> realEstateInfos = new ArrayList<>();

        List<Map<String, String>> securityProviderInfos = new ArrayList<>();
        for (SelenideElement securityProvider : securityProviders) {
            ElementsCollection securityProviderInfo = securityProvider.findAll("table > tbody > tr:nth-child(2) > td > div > table > tbody tr");
            String 담보제공자_성명 = securityProviderInfo.get(0).find("td > input:first-child").getValue();
            String 담보제공자_주민등록번호_앞자리 = securityProviderInfo.get(1).find("td > input:nth-child(1)").getValue();
            String 담보제공자_주민등록번호_뒷자리 = securityProviderInfo.get(1).find("td > input:nth-child(2)").getValue();
            String 담보제공자_주소 = securityProviderInfo.get(2).find("td > #DMBJG_MN_ADR1").getValue();
            String 담보제공자_상세주소 = securityProviderInfo.get(2).find("td > #DMBJG_MN_DTL_ADR1").getValue();
            boolean 채무자_동일_여부 = 채무자_성명.equals(담보제공자_성명);

            log.info("채무자 동일 여부 = {}", 채무자_동일_여부);
            log.info("담보제공자 성명 = {}", 담보제공자_성명);
            log.info("담보제공자 주민등록번호 앞자리 = {}", 담보제공자_주민등록번호_앞자리);
            log.info("담보제공자 주민등록번호 뒷자리 = {}", 담보제공자_주민등록번호_뒷자리);
            log.info("담보제공자 주소 = {}", 담보제공자_주소);
            log.info("담보제공자 상세주소 = {}", 담보제공자_상세주소);

            String 담보제공자_주민등록번호 = 담보제공자_주민등록번호_앞자리 + 담보제공자_주민등록번호_뒷자리;
            String 담보제공자_전체_주소 = 담보제공자_주소 + " " + 담보제공자_상세주소;

            Map<String, String> 채무자 = new HashMap<>();
            Map<String, String> 설정자 = new HashMap<>();

            if (securityProviders.size() > 1) { // 담보제공자 정보에 2명 이상이 존재하는 경우
                if (채무자_동일_여부) { // 현재 담보제공자가 채무자와 동일한 경우, 채무자로 저장
                    log.info("담보제공자 2명 이상, 채무자와 동일함");

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1"); // 고객유형 : 채무자(1)
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_전체_주소);

                    securityProviderInfos.add(채무자);

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
                    설정자.put("CUST_ADDR", 담보제공자_전체_주소);

                    securityProviderInfos.add(설정자);
                }

            } else { // 담보제공자 정보에 1명만 존재하는 경우
                if (채무자_동일_여부) { // 현재 담보제공자가 채무자와 동일한 경우, 채무자와 설정자 모두 동일하게 저장
                    log.info("담보제공자 1명, 채무자와 동일함");

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1");
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_전체_주소);

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2");
                    설정자.put("CUST_SEQ_NUM", "2");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "on");
                    설정자.put("CUST_ADDR", 담보제공자_전체_주소);

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
                    채무자.put("CUST_ADDR", 채무자_전체_주소);

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2");
                    설정자.put("CUST_SEQ_NUM", "1");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);  // 주민등록번호
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "off");
                    설정자.put("CUST_ADDR", 담보제공자_전체_주소);
                }

                securityProviderInfos.add(채무자);
                securityProviderInfos.add(설정자);
            }
        }

        return securityProviderInfos;
    }

    public List<Map<String, String>> 담보제공자_정보(String caseManagementNumber) {
        // 담보제공자와 채무자의 동일 여부를 구분하기 위해 채무자 정보를 먼저 스크래핑 해야 함
        Map<String, String> 채무자_정보 = 채무자_정보();
        String 채무자_성명 = 채무자_정보.get("채무자_성명");
        String 채무자_주소 = 채무자_정보.get("채무자_주소");
        String 채무자_상세주소 = 채무자_정보.get("채무자_상세주소");
        String 채무자_전체_주소 = 채무자_주소 + " " + 채무자_상세주소;

        ElementsCollection securityProviders = $$("#oblgList tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("담보제공자 수 = {}명", securityProviders.size());

        List<Map<String, String>> securityProvidersInfos = new ArrayList<>();
        for (SelenideElement securityProvider : securityProviders) {
            boolean 채무자_동일_여부 = securityProvider.find(By.tagName("td"), 0)
                .find("input[type='checkbox']")
                .isSelected();

            securityProvider.click();
            switchTo().frame($("iframe.popupIframe"));

            String 담보제공자_성명 = $("#oblg_nm").getValue();
            String 담보제공자_주민등록번호_앞자리 = $("#oblg_corp_no1").getValue();
            String 담보제공자_주민등록번호_뒷자리 = $("#oblg_corp_no2").getValue();
            String 담보제공자_주소 = $("#oblg_basic_addr").getValue();
            String 담보제공자_상세주소 = $("#oblg_dtl_addr").getValue();

            $("body > div.txt_cen > input.btn_cancle").click();
            switchTo().defaultContent();

            log.info("채무자 동일 여부 = {}", 채무자_동일_여부);
            log.info("담보제공자 성명 = {}", 담보제공자_성명);
            log.info("담보제공자 주민등록번호 앞자리 = {}", 담보제공자_주민등록번호_앞자리);
            log.info("담보제공자 주민등록번호 뒷자리 = {}", 담보제공자_주민등록번호_뒷자리);
            log.info("담보제공자 주소 = {}", 담보제공자_주소);
            log.info("담보제공자 상세주소 = {}", 담보제공자_상세주소);

            String 담보제공자_주민등록번호 = 담보제공자_주민등록번호_앞자리 + 담보제공자_주민등록번호_뒷자리;
            String 담보제공자_전체_주소 = 담보제공자_주소 + " " + 담보제공자_상세주소;

            Map<String, String> 채무자 = new HashMap<>();
            Map<String, String> 설정자 = new HashMap<>();

            if (securityProviders.size() > 1) { // 담보제공자 정보에 2명 이상이 존재하는 경우
                if (채무자_동일_여부) { // 현재 담보제공자가 채무자와 동일한 경우, 채무자로 저장
                    log.info("담보제공자 2명 이상, 채무자와 동일함");

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1"); // 고객유형 : 채무자(1)
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_전체_주소);

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
                    설정자.put("CUST_ADDR", 담보제공자_전체_주소);

                    securityProvidersInfos.add(설정자);
                }

            } else { // 담보제공자 정보에 1명만 존재하는 경우
                if (채무자_동일_여부) { // 현재 담보제공자가 채무자와 동일한 경우, 채무자와 설정자 모두 동일하게 저장
                    log.info("담보제공자 1명, 채무자와 동일함");

                    채무자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    채무자.put("CUST_NM", 채무자_성명);
                    채무자.put("CUST_CL_CD", "1");
                    채무자.put("CUST_SEQ_NUM", "1");
                    채무자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    채무자.put("LOCAL_FORGNER_CD", "1");
                    채무자.put("PSNL_CPRTN_CD", "1");
                    채무자.put("SAME_STTLMNTR", "off");
                    채무자.put("CUST_ADDR", 채무자_전체_주소);

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2");
                    설정자.put("CUST_SEQ_NUM", "2");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "on");
                    설정자.put("CUST_ADDR", 담보제공자_전체_주소);

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
                    채무자.put("CUST_ADDR", 채무자_전체_주소);

                    설정자.put("INVST_INST_MGMT_NUM", caseManagementNumber);
                    설정자.put("CUST_NM", 담보제공자_성명);
                    설정자.put("CUST_CL_CD", "2");
                    설정자.put("CUST_SEQ_NUM", "1");
                    설정자.put("CUST_CTZ_NUM", 담보제공자_주민등록번호);  // 주민등록번호
                    설정자.put("LOCAL_FORGNER_CD", "1");
                    설정자.put("PSNL_CPRTN_CD", "1");
                    설정자.put("SAME_STTLMNTR", "off");
                    설정자.put("CUST_ADDR", 담보제공자_전체_주소);
                }

                securityProvidersInfos.add(채무자);
                securityProvidersInfos.add(설정자);
            }
        }

        return securityProvidersInfos;
    }

    public Map<String, String> 채무자_정보_new() {
        String 채무자_성명 = $("#DBTR_CUSNM").getValue();
        String 채무자_주소 = $("#DBTR_ADR").getValue();
        String 채무자_상세주소 = $("#DBTR_DTL_ADR").getValue();

        log.info("채무자 성명 = {}", 채무자_성명);
        log.info("채무자 주소 = {}", 채무자_주소);
        log.info("채무자 상세주소 = {}", 채무자_상세주소);

        Map<String, String> debtorInfo = new HashMap<>();
        debtorInfo.put("채무자_성명", 채무자_성명);
        debtorInfo.put("채무자_주소", 채무자_주소);
        debtorInfo.put("채무자_상세주소", 채무자_상세주소);

        return debtorInfo;
    }

    public Map<String, String> 채무자_정보() {
        ElementsCollection debtors = $$("#dbtrList tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("채무자 수 = {}명", debtors.size());

        if (debtors.size() > 1) {
            log.warn("채무자 수가 2명 이상입니다. (로직 추가 필요)");
            throw new RuntimeException();
        }

        SelenideElement debtor = debtors.get(0);
        debtor.click();
        switchTo().frame($("iframe.popupIframe"));

        String 채무자_성명 = $("#dbtr_nm").getValue();
        String 채무자_주소 = $("#dbtrDto > table > tbody > tr:nth-child(2) > td > input:nth-child(2)").getValue();
        String 채무자_상세주소 = $("#dbtr_dtl_addr").getValue();

        $("body > div.txt_cen > input").click();
        switchTo().defaultContent();

        log.info("채무자 성명 = {}", 채무자_성명);
        log.info("채무자 주소 = {}", 채무자_주소);
        log.info("채무자 상세주소 = {}", 채무자_상세주소);

        Map<String, String> debtorInfo = new HashMap<>();
        debtorInfo.put("채무자_성명", 채무자_성명);
        debtorInfo.put("채무자_주소", 채무자_주소);
        debtorInfo.put("채무자_상세주소", 채무자_상세주소);

        return debtorInfo;
    }

    public List<Map<String, String>> 부동산_정보_new(String caseManagementNumber) {
        actions().pause(Duration.ofSeconds(5)).perform();

//        $("#AnySign4PCLoadingImg").shouldBe(visible).shouldNotBe(visible, Duration.ofSeconds(10));
//        $("#dmb_info").shouldBe(visible, Duration.ofSeconds(20));

        String 부동산_구분 = $("#dmb_info > div > table > tbody > tr:nth-child(1) > td > div > table > tbody > tr:nth-child(2) > td:nth-child(2) > input").getValue();
        String 부동산_고유번호 = $(By.xpath("/html/body/div[4]/div/div[2]/div[2]/form/div/div/table/tbody/tr[1]/td/div/table/tbody/tr[1]/td/input[2]")).getValue();
        String 부동산_주소 = $("#dmb_info > div > table > tbody > tr:nth-child(1) > td > div > table > tbody > tr:nth-child(3) > td > input").getValue();
        String 관할등기소 = $("#dmb_info > div > table > tbody > tr:nth-child(1) > td > div > table > tbody > tr:nth-child(2) > td.lft.end > input").getValue();

        log.info("부동산 구분 = {}", 부동산_구분);
        log.info("부동산 고유번호 = {}", 부동산_고유번호);
        log.info("부동산 주소 = {}", 부동산_주소);
        log.info("관할등기소 = {}", 관할등기소);

        List<Map<String, String>> realEstatesInfos = new ArrayList<>();
        Map<String, String> realEstateInfo = new HashMap<>();

        if (부동산_구분.contains("건물")) {
            realEstateInfo.put("GDS_TYP_CD_1", "1");
            realEstateInfo.put("GDS_TYP_CD_2", 부동산_구분.equals("집합건물") ? "1" : "3");
        } else {
            realEstateInfo.put("GDS_TYP_CD_1", "2");
            realEstateInfo.put("GDS_TYP_CD_2", 부동산_구분.equals("토지") ? "4" : "5");
        }

        realEstateInfo.put("EST_SEQ_NUM", "1");
        realEstateInfo.put("EST_ORGN_NUM", 부동산_고유번호);
        realEstateInfo.put("STRT_NEW_ADDR", 부동산_주소);
        realEstateInfo.put("SIDO", (부동산_주소.length() == 0 ? "" : 부동산_주소.split(" ")[0]));
        realEstateInfo.put("LAND_LOT_CNT", "1");
        realEstateInfo.put("INVST_INST_MGMT_NUM", caseManagementNumber);

        realEstatesInfos.add(realEstateInfo);

        return realEstatesInfos;
    }

    public List<Map<String, String>> 부동산_정보(String caseManagementNumber) {
        List<Map<String, String>> realEstatesInfos = new ArrayList<>();

        ElementsCollection realEstates = $$("#prptyList tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        log.info("부동산 수 = {}", realEstates.size());

        int sequence = 1;
        for (SelenideElement realEstate : realEstates) {
            ElementsCollection elements = realEstate.findAll(By.tagName("td"));
            String 부동산_구분 = elements.get(1).getText();
            String 부동산_고유번호 = elements.get(2).getText();
            String 부동산_주소 = elements.get(6).getText();
            String 관할등기소 = elements.get(7).getText();

            log.info("부동산 구분 = {}", 부동산_구분);
            log.info("부동산 고유번호 = {}", 부동산_고유번호);
            log.info("부동산 주소 = {}", 부동산_주소);
            log.info("관할등기소 = {}", 관할등기소);

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
