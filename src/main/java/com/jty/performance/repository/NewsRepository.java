package com.jty.performance.repository;

import com.jty.performance.domain.news.News;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: yeting
 * @Date: 2019/8/13 19:05
 */
public interface NewsRepository extends JpaRepository<News, Integer> {
}
