package com.hanulplc.customer.bccard.model;

import com.hanulplc.util.MybatisSessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@Slf4j
public class BCCardScrapingDAO {

    private BCCardScrapingDAO() {
    }

    public static OptionalInt findByCaseId(String caseId) {
        try (SqlSession session = MybatisSessionFactory.newDevSession()) {
            BCCardMapper mapper = session.getMapper(BCCardMapper.class);
            return OptionalInt.of(mapper.findByCaseId(caseId));
        } catch (Exception e) {
            log.error("의뢰 조회 중 예외 발생", e);
        }
        return OptionalInt.empty();
    }

    public static void saveNewCase(Map<String, Object> caseMap,
                                   List<Map<String, Object>> customers,
                                   List<Map<String, Object>> estates) {

        SqlSession session = MybatisSessionFactory.newDevSession();
        BCCardMapper mapper = session.getMapper(BCCardMapper.class);

        try {
            // 의뢰 기본 정보
            mapper.saveNewCase(caseMap);
            mapper.saveNewFee(caseMap);
            mapper.saveHRCommissionInfo(caseMap);

            // 등기의무자 정보
            mapper.saveNewCustomer(customers);

            // 부동산 정보
            mapper.saveNewEstate(estates);

            session.commit();

        } catch (Exception e) {
            log.error("의뢰 데이터 추가 중 예외 발생", e);
            session.rollback();

        } finally {
            session.close();
        }
    }
}
