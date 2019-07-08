package com.jty.performance.schedule;

import com.jty.performance.domain.*;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.EmployeeRepository;
import com.jty.performance.repository.IntegralHistoryListRepository;
import com.jty.performance.repository.LogQualificationRepository;
import com.jty.performance.repository.PlanCycleRepository;
import com.jty.performance.service.KpiPlanCycleService;
import com.jty.performance.service.KpiScoreBoardService;
import com.jty.performance.service.impl.KpiScoreBoardServiceImpl;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @Author: yeting
 * @Date: 2019/5/13 17:32
 */
@Component
@Configuration
@EnableScheduling
public class PerformanceScheduleTask {

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private IntegralHistoryListRepository integralHistoryListRepository;

    @Autowired
    private LogQualificationRepository logQualificationRepository;

    /**
     * 考核期定时任务
     */
    @Scheduled(cron = "0 */1 * * * ?")
    private void planCycleScheduleTask() {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        List<PlanCycle> planCycleList = planCycleRepository.findByPlanCycleState(1);
        for (PlanCycle planCycle : planCycleList) {
            if (localDate.isEqual(planCycle.getEndTime())) {
                synchronized (planCycle) {
                    initializePlanCycle(localDate, planCycle);
                }
            }
        }
    }


    private void initializePlanCycle(LocalDate localDate, PlanCycle planCycle) {
        Map<Integer, Integer> employeeScoreRank = getEmployeeScoreRank(planCycle.getDepartment().getId());
        List<Employee> allEmployees = employeeRepository.findByParentDepartment(planCycle.getDepartment().getId());
        for (Employee employee : allEmployees) {
            //个人历史排名情况
            IntegralHistoryList integralHistoryList = new IntegralHistoryList();
            integralHistoryList.setPlanCycleId(planCycle.getId());
            integralHistoryList.setEmployee(employee);
            integralHistoryList.setRank(employeeScoreRank.get(employee.getId()));
            integralHistoryList.setScore(employee.getCurrentScore());
            integralHistoryList.setDepartment(employee.getDepartment());
            integralHistoryListRepository.save(integralHistoryList);
            //历史贡献情况
            LogQualification logQualification = new LogQualification();
            logQualification.setPlanCycle(planCycle);
            logQualification.setEmployee(employee);
            IntegralModifyType integralModifyType = new IntegralModifyType();
            integralModifyType.setId(3);
            logQualification.setIntegralModifyType(integralModifyType);
            logQualification.setQualifModifyNum(employee.getCurrentScore());
            logQualification.setDepartment(employee.getDepartment());
            logQualification.setQualifModifyReason(planCycle.getPlanCycleName() + "积分归档");
            logQualificationRepository.save(logQualification);
            //个人历史贡献值累加
            employee.setRecordScore(employee.getRecordScore() + employee.getCurrentScore());
            employee.setCurrentScore(0);
        }
        employeeRepository.saveAll(allEmployees);
        //考核期状态设置
        planCycle.setPlanCycleState(0);
        planCycleRepository.save(planCycle);
        //新建考核期
        PlanCycle newPlanCycle = new PlanCycle();
        LocalDate startLocalDate = localDate.plusDays(1);
        LocalDate endLocalDate = localDate.plusMonths(1);
        newPlanCycle.setStartTime(startLocalDate);
        newPlanCycle.setEndTime(endLocalDate);
        newPlanCycle.setPlanCycleName("第" + (planCycle.getCount() + 1) + "考核期");
        newPlanCycle.setPlanCycleState(1);
        newPlanCycle.setPlanCycleUseScore(0);
        newPlanCycle.setCount(planCycle.getCount() + 1);
        newPlanCycle.setDepartment(planCycle.getDepartment());
        planCycleRepository.save(newPlanCycle);
    }

    /**
     * 获取当前积分排名
     *
     * @return
     */
    private Map<Integer, Integer> getEmployeeScoreRank(Integer partentId) {
        HashMap<Integer, Integer> map = new HashMap<>();
        List<Employee> orderByCurrentScore = employeeRepository.findOrderByCurrentScore(partentId);
        Integer rank = 1;
        for (Employee employee : orderByCurrentScore) {
            map.put(employee.getId(), rank++);
        }
        return map;
    }

}
