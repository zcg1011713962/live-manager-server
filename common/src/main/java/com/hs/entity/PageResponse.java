package com.hs.entity;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Data
@ToString
@Setter
public class PageResponse<T> {

    private List<T> records; // 当前页数据列表
    private long total;      // 总记录数
    private long size;       // 每页记录数
    private long current;    // 当前页码
    private long pages;      // 总页数

    public PageResponse(List<T> records, long total, long size, long current, long pages) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = pages;
    }

}

