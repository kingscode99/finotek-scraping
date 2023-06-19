package com.hanulplc.customer.bccard.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BCCardMapper {

    int findByCaseId(String registrationRequestNumber);

    void saveNewCase(Map<String, Object> paramMap);

    void saveHRCommissionInfo(Map<String, Object> paramMap);

    void saveNewFee(Map<String, Object> paramMap);

    void saveNewEstate(List<Map<String, Object>> paramMaps);

    void saveNewCustomer(List<Map<String, Object>> paramMaps);
}
