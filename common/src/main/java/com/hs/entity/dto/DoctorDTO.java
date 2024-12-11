package com.hs.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DoctorDTO extends PageDTO{
    // 科室
    private String department;
    // 职位
    private String position;
    // 医生简介
    private String biography;
    // 擅长领域
    private String specialization;
    // 可预约时间段
    private String availableSlots;
}
