package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void save(Map<String, Object> paramMap);

    //调用service方法实现根据医院编号查询
    Hospital getByHoscode(String hoscode);

    //医院列表，条件查询带分页
    Page<Hospital> selectHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    //医院详情信息
    Map<String, Object> getHospById(String id);

    String getHospName(String hoscode);

    //根据医院名称模糊查询
    List<Hospital> findByHosname(String hosname);
    //根据医院编号获取医院预约挂号详情
    Map<String, Object> item(String hoscode);
}
