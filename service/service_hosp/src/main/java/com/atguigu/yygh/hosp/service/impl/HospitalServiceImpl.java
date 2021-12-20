package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import sun.nio.cs.ext.MacHebrew;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    //添加数据
    @Override
    public void save(Map<String, Object> paramMap) {
        //把参数map集合转换成对象Hospital
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString, Hospital.class);

        // 判断是否存在相同数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);


        if (hospitalExist != null){//存在，进行修改更新
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {//不存在，进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }


    }

    //调用service方法实现根据医院编号查询
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }
    //医院列表，条件查询带分页
    @Override
    public Page<Hospital> selectHospitalPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建Pageable对象
        Pageable pageable = PageRequest.of(page-1, limit);
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //hospitalSetQueryVo转换Hospital对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //创建对象
        Example<Hospital> example = Example.of(hospital, matcher);
        Page<Hospital> all = hospitalRepository.findAll(example, pageable);
        return all;
    }
}
