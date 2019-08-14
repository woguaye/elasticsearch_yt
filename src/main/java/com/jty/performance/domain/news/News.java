package com.jty.performance.domain.news;

import com.jty.performance.domain.SuperEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Date;

/**
 * @Author: yeting
 * @Date: 2019/8/13 8:34
 */
@Data
@Entity
@Table(name = "news")
public class News extends SuperEntity {

    private String title;

    private String keyWord;

    private String content;

    private String url;

    private Integer reply;

    private String source;

    private Date postdate;

}
