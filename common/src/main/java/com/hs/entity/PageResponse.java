package com.hs.entity;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Data
@ToString
@Setter
public class PageResponse<T> {

    private List<T> data; // 当前页数据列表
    private long recordsTotal;      // 总记录数
    private long recordsFiltered;       // 每页记录数
    private int draw;      // 查询标识


    public PageResponse(List<T> data, long recordsTotal, long recordsFiltered, int draw) {
        this.data = data;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.draw = draw;
    }

    public PageResponse(List<T> data, long recordsTotal, long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
        this.recordsTotal = recordsTotal;
        this.data = data;
    }
}

