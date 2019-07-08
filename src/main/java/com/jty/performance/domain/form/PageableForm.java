package com.jty.performance.domain.form;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 分页查询基础数据Form
 *
 * @author Jason
 * @since 2018/12/22 15:29
 */
@Data
public class PageableForm {

    Integer pageNumber=0;

    Integer pageSize=20;

    String orderBy="id";

    String direction="asc";

    public Pageable getPageable(){
        if(pageNumber<1){
            pageNumber=1;
        }
        if(pageSize>5000||pageSize<1){
            pageSize=20;
        }
        if("desc".equalsIgnoreCase(direction)){
            return PageRequest.of(pageNumber-1, pageSize, Sort.by(Sort.Order.desc(orderBy)));
        }else  {
            return PageRequest.of(pageNumber-1, pageSize, Sort.by(Sort.Order.asc(orderBy)));
        }
    }

    public Integer getFrom(){
        if(pageNumber>1){
            return  (pageNumber-1)*pageSize;
        }
        else {
            return 0;
        }
    }

}
