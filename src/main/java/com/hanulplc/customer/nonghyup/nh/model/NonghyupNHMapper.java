package com.hanulplc.customer.nonghyup.nh.model;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NonghyupNHMapper {

    int findByCaseId(String registrationRequestNumber);

    void saveNewCase(Map<String, String> paramMap);

    void saveHRCommissionInfo(Map<String, String> paramMap);

    void saveNewFee(Map<String, String> paramMap);

    void saveNewEstate(List<Map<String, String>> paramMaps);

    void saveNewCustomer(Map<String, String> paramMap);

    void saveNewMoveCustomer(Map<String, String> paramMap);
}