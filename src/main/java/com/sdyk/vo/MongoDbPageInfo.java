package com.sdyk.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taylor on 2018/3/10.
 */
public class MongoDbPageInfo implements Serializable{

    Long total;
    Integer pageNum;
    Integer pageSize;
    Integer pages;
//    List navigatepageNums;
    List list;

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total=total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

//    public List getNavigatepageNums() {
//        return navigatepageNums;
//    }
//
//    public void setNavigatepageNums(List navigatepageNums) {
//        this.navigatepageNums = navigatepageNums;
//    }
}
