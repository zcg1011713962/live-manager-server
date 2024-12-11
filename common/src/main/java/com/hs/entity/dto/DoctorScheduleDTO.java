package com.hs.entity.dto;

import lombok.Data;

@Data
public class DoctorScheduleDTO extends PageDTO{
    private String startDate;
    private String endDate;
    private String doctorName;
    private String department;

}
