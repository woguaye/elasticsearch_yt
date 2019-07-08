package com.jty.performance.support;


import com.jty.performance.repository.LogIntegralRepository;
import com.jty.performance.repository.PlanCycleRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 组件注册
 *
 * @author jason
 * @since 2019/5/20
 */
@Component
public class DomainRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static LogIntegralRepository logIntegralRepository() {
        return applicationContext.getBean(LogIntegralRepository.class);
    }

    public static PlanCycleRepository planCycleRepository() {
        return applicationContext.getBean(PlanCycleRepository.class);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (DomainRegistry.applicationContext == null) {
            DomainRegistry.applicationContext = applicationContext;
        }
    }
}
