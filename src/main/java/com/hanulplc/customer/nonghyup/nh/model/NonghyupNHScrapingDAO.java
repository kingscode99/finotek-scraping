package com.hanulplc.customer.nonghyup.nh.model;

import com.hanulplc.util.MybatisSessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@Slf4j
public class NonghyupNHScrapingDAO {

    private NonghyupNHScrapingDAO() {
    }

    public static OptionalInt findByCaseId(String caseId) {
        try (SqlSession session = MybatisSessionFactory.newProdSession()) {
            NonghyupNHMapper mapper = session.getMapper(NonghyupNHMapper.class);
            return OptionalInt.of(mapper.findByCaseId(caseId));
        } catch (Exception e) {
            log.error("농협은행 의뢰 조회 중 예외 발생", e);
            return OptionalInt.empty();
        }
    }

    public static void saveNewCase(Map<String, String> caseMap,
                                   List<Map<String, String>> customers,
                                   List<Map<String, String>> estates) {

        SqlSession session = MybatisSessionFactory.newProdSession();
        NonghyupNHMapper mapper = session.getMapper(NonghyupNHMapper.class);

        try {
            // 의뢰 기본정보 입력
            mapper.saveNewCase(caseMap);
            mapper.saveHRCommissionInfo(caseMap);
            mapper.saveNewFee(caseMap);

            // 부동산_정보_입력
            mapper.saveNewEstate(estates);

            구입자금일_경우_이전_고객정보_입력(caseMap, customers, mapper);
            설정_고객정보_입력(caseMap, customers, mapper);

            session.commit();

        } catch (Exception e) {
            log.error("농협은행 의뢰 추가 중 예외 발생", e);
            session.rollback();

        } finally {
            session.close();
        }
    }

    private static void 설정_고객정보_입력(Map<String, String> caseMap, List<Map<String, String>> customers, NonghyupNHMapper mapper) {
        for (Map<String, String> customer : customers) {
            log.info("설정 고객정보 입력 = {}", customer);
            mapper.saveNewCustomer(customer);

            // '구입자금', '자담'일 경우, 채무자가 설정자이기도 함.
            if (customer.get("CUST_CL_CD").equals("1") &&
                caseMap.get("RGSTRN_TYP_CD").equals("10") && // 구입자금 여부 체크
                caseMap.get("RGSTRN_RSN_TYP_CD").equals("5")) { // 자담 여부 체크

                customer.put("CUST_CL_CD", "2"); // 채무자를 설정자로 변경
                customer.put("CUST_SEQ_NUM", "2");

                log.info("구입자금 채무자를 설정자로 추가합니다 = {}", customer);
                mapper.saveNewCustomer(customer);
            }
        }
    }

    private static void 구입자금일_경우_이전_고객정보_입력(Map<String, String> caseMap, List<Map<String, String>> customers, NonghyupNHMapper mapper) {
        boolean isPurchasingFundsCase = caseMap.get("RGSTRN_TYP_CD").equals("10"); // 구입자금 유무
        int moveCustomerSequence = 1;
        if (isPurchasingFundsCase) {
            String debtorName = "";

            // !!! '구입자금'의 경우, 이전 고객정보에 채무자든 설정자든 모두 '매수인'으로 저장해야 함 !!!
            // 채무자부터 테이블에 저장해야 해서 반복문으로 채무자부터 찾아서 INSERT 처리함
            for (Map<String, String> customer : customers) {
                debtorName = customer.get("CUST_NM");

                if (customer.get("CUST_CL_CD").equals("1")) { // 채무자
                    Map<String, String> moveCustomer = new HashMap<>();
                    moveCustomer.put("INVST_INST_MGMT_NUM", customer.get("INVST_INST_MGMT_NUM"));
                    moveCustomer.put("MOVE_CUST_CL_CD", "3"); // 매수인(3)
                    moveCustomer.put("MOVE_SEQ_NUM", String.valueOf(moveCustomerSequence++));
                    moveCustomer.put("MOVE_CUST_NM", customer.get("CUST_NM"));
                    moveCustomer.put("MOVE_LOCAL_FORGNER_CD", customer.get("LOCAL_FORGNER_CD"));
                    moveCustomer.put("MOVE_PSNL_CPRTN_CD", customer.get("PSNL_CPRTN_CD"));
                    moveCustomer.put("MOVE_SAME_STTLMNTR", customer.get("SAME_STTLMNTR"));
                    moveCustomer.put("REGDATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    mapper.saveNewMoveCustomer(moveCustomer);
                    break;
                }
            }

            // 채무자 입력이 끝나고 설정자 입력
            for (Map<String, String> customer : customers) {
                if (customer.get("CUST_CL_CD").equals("2")) {
                    if (debtorName.equals(customer.get("CUST_NM"))) { // 위의 채무자 이름과 동일할 경우 생략
                        continue;
                    }

                    Map<String, String> moveCustomer = new HashMap<>();
                    moveCustomer.put("INVST_INST_MGMT_NUM", customer.get("INVST_INST_MGMT_NUM"));
                    moveCustomer.put("MOVE_CUST_CL_CD", "3"); // 매수인(3)
                    moveCustomer.put("MOVE_SEQ_NUM", String.valueOf(moveCustomerSequence++));
                    moveCustomer.put("MOVE_CUST_NM", customer.get("CUST_NM"));
                    moveCustomer.put("MOVE_CUST_CTZ_NUM", customer.get("CUST_CTZ_NUM"));
                    moveCustomer.put("MOVE_LOCAL_FORGNER_CD", customer.get("LOCAL_FORGNER_CD"));
                    moveCustomer.put("MOVE_PSNL_CPRTN_CD", customer.get("PSNL_CPRTN_CD"));
                    moveCustomer.put("MOVE_SAME_STTLMNTR", customer.get("SAME_STTLMNTR"));
                    moveCustomer.put("REGDATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    mapper.saveNewMoveCustomer(moveCustomer);
                }
            }
        }
    }
}
