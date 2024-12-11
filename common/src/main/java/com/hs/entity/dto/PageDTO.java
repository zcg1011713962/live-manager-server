package com.hs.entity.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PageDTO {
    private Long pageNum;
    private Long pageSize;
}
