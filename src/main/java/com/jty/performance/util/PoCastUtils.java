package com.jty.performance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Po类转化工具类
 *
 * @author Jason
 * @since 2018/12/18 13:02
 */
public class PoCastUtils {

    private static Logger logger = LoggerFactory.getLogger(PoCastUtils.class);

    public static<F,T> List<T> poCastToDto(Iterable<F> fromList,Class<T> tClass){
        if(fromList==null||tClass==null){
            return null;
        }
        List<T> fList=new ArrayList<>();
        fromList.forEach(r->{
            try {
                T model = tClass.newInstance();
                BeanUtils.copyProperties(r,model);
                fList.add(model);
            } catch (Exception e) {
                logger.info("实体转化出错");
            }
        });
        return fList;
    }

    /**
     *  转换分页数据
     * @author zhongwentao
     * @since 2018/12/29  9:17
     * @return org.springframework.data.domain.Page<T>
     **/
    public static<T,F> Page<T> poPageCastToDto(Page<F> page, Class<T> tClass){
        if(page==null || tClass==null){
            return null;
        }
        List<T> list=new ArrayList<>();
        page.getContent().forEach(r->{
            try {
                T t = tClass.newInstance();
                BeanUtils.copyProperties(r,t);
                list.add(t);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return new PageImpl<>(list,page.getPageable(),page.getTotalElements());
    }

    /**
     * 将集合转成以id为主键的对象
     * @param list 传入集合
     * @return Map
     */
    public static<T> Map<Integer,T> listToMap(Iterable<T> list){

        if(list==null){
            return null;
        }

        Map<Integer,T> map = new HashMap<>();

        list.forEach(r->{
            try {
                PropertyDescriptor tpd = BeanUtils.getPropertyDescriptor(r.getClass(), "id");
                //断言不为空，防止出警告
                assert tpd != null;
                Method readMethod = tpd.getReadMethod();
                Integer id = (Integer)readMethod.invoke(r);
                map.put(id,r);
            } catch (Exception e) {
                logger.info("Combination to map failed");
            }
        });
        return map;
    }
}
