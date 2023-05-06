package com.past.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用分页查询返回实体
 */
@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1045545547716760614L;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页显示数目
     */
    private int pageSize;

    /**
     * 当前页的数据数量
     */
    private int size;

    /**
     * 总记录数
     */
    private int total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 动态返回数据
     */
    private List<T> data;

    /**
     * 第一页
     */
    private int firstPage;

    /**
     * 上一页
     */
    private int prePage;

    /**
     * 下一页
     */
    private int nextPage;

    /**
     * 是否为第一页
     */
    private boolean isFirstPage;

    /**
     * 是否为最后一页
     */
    private boolean isLastPage;

    /**
     * 是否有上一页
     */
    private boolean hasPreviousPage;

    /**
     * 是否有下一页
     */
    private boolean hasNextPage;



    /**
     * 有参构造方法
     * 通过stream流的skip和limit操作，动态跳过和截取一定长度的源集合，实现分页效果
     * xml中不需写带有limit的查询语句
     * @param pageRequest 内含pageNum、pageSize的分页请求实体
     * @param source 待分页的源集合
     */
    public PageResult(PageRequest<?> pageRequest, List<T> source) {

        // 第一页
        this.firstPage = 1;

        // 当前页码
        this.pageNum = pageRequest.getPageNum();

        // 每页显示数目
        this.pageSize = pageRequest.getPageSize();

        // 总记录数
        this.total = source.size();

        // 总页数 即尾页的页码
        this.pages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;

        // 上一页
        if (pageNum == 1) {
            this.prePage = 1;
        }
        if (pageNum > 1 && pageNum <= pages){
            this.prePage = pageNum - 1;
        }

        // 下一页
        if (pageNum == pages) {
            this.nextPage = pages;
        }
        if (pageNum >= 1 && pageNum < pages) {
            this.nextPage = pageNum + 1;
        }

        // 是否是首页
        this.isFirstPage = pageNum == firstPage;

        // 是否是尾页
        this.isLastPage = pageNum == pages;

        // 是否有上一页
        this.hasPreviousPage = pageNum > 1 && pageNum <= pages;

        // 是否有下一页
        this.hasNextPage = pageNum >= 1 && pageNum < pages;

        // 当前页的数据数量
        if (source.size() % pageSize == 0) { // 如果总数量是每页显示数量的整数倍，那么每页数据量都一致
            this.size = pageSize;
        }
        else { // 不能整除
            if (this.isLastPage){ // 判断当前页是否是尾页
                this.size = source.size() % pageSize; //是尾页，数量取模
            }
            else { // 不是尾页，数量=每页显示数量
                this.size = pageSize;
            }
        }

        // 当前页的动态返回数据
        if (source.size() % pageSize == 0) {
            this.data = source.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
        }
        else {
            if (this.isLastPage) {
                this.data = source.stream().skip((pages - 1) * pageSize).limit(source.size() % pageSize).collect(Collectors.toList());
            }
            else {
                this.data = source.stream().skip((pageNum - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
            }
        }

    }

}