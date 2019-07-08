package com.jty.performance.service.impl;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.Parameter;
import com.jty.performance.domain.PlanCycle;
import com.jty.performance.domain.Task;
import com.jty.performance.domain.dto.KpiPlanCycleDto;
import com.jty.performance.domain.dto.StaffNodeDto;
import com.jty.performance.domain.form.PlanCycleForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.DepartmentRepository;
import com.jty.performance.repository.ParameterRepository;
import com.jty.performance.repository.PlanCycleRepository;
import com.jty.performance.repository.TaskRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiIntegralService;
import com.jty.performance.service.KpiPlanCycleService;
import com.jty.performance.support.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: yeting
 * @Date: 2019/5/7 15:19
 */
@Service
public class KpiPlanCycleServiceImpl implements KpiPlanCycleService {

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private KpiIntegralService kpiIntegralService;

    @Autowired
    private ParameterRepository parameterRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public KpiPlanCycleDto getPlanCycleInfo(MyUserDetail user) {
        Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return planCycle.castToDto();
    }

    @Override
    public void updatePlanCycleTime(Integer planCycleId, PlanCycleForm form) {
        PlanCycle planCycle = planCycleRepository.findById(planCycleId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        planCycle.setEndTime(form.getPlanCycleEnd());
        planCycleRepository.save(planCycle);
    }

    @Override
    public KpiPlanCycleDto getBudgetScore(MyUserDetail user, Integer planCycleId) {
        List<Parameter> allParameters = parameterRepository.findAll();
        Parameter parameter = allParameters.get(0);
        PlanCycle planCycle = planCycleRepository.findById(planCycleId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        long day = planCycle.getEndTime().toEpochDay() - planCycle.getStartTime().toEpochDay() + 1;
        KpiPlanCycleDto kpiPlanCycleDto = new KpiPlanCycleDto();
        Integer planCycleEstimateScore = getEstimateScore(planCycle);
        kpiPlanCycleDto.setPlanCycleEstimateScore(planCycleEstimateScore);
        Integer staffNum = 0;
        List<StaffNodeDto> employeeNodes = kpiIntegralService.findEmployeeNodes(user);
        if (CollectionUtils.isNotEmpty(employeeNodes)) {
            for (StaffNodeDto staffNodeDto : employeeNodes) {
                if (staffNodeDto.getId().equals(planCycle.getDepartment().getId())) {
                    staffNum += staffNodeDto.getEmployeeList().size();
                    List<StaffNodeDto> children = staffNodeDto.getChildren();
                    for (StaffNodeDto staffNode : children) {
                        staffNum += staffNode.getEmployeeList().size();
                    }
                }
            }
        }
        kpiPlanCycleDto.setCurrentBudget((int) (parameter.getScoreArgument() * day * staffNum));
        return kpiPlanCycleDto;
    }

    /**
     * 获取当前考核期使用预计积分总数
     *
     * @param planCycle
     * @return
     */
    private Integer getEstimateScore(PlanCycle planCycle) {
        List<Task> allTaskByQuery = taskRepository.findTaskByTime(planCycle.getStartTime(), planCycle.getEndTime());
        Integer planCycleEstimateScore = 0;
        if (CollectionUtils.isNotEmpty(allTaskByQuery)) {
            for (Task task : allTaskByQuery) {
                planCycleEstimateScore += task.getEstimatedScore();
            }
        }
        return planCycleEstimateScore;
    }

    @Override
    public List<KpiPlanCycleDto> getPlanCyclePast(MyUserDetail user) {
        ArrayList<KpiPlanCycleDto> kpiPlanCycleDtos = new ArrayList<>();
        Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        List<PlanCycle> allPlanCycle = planCycleRepository.findByDepartmentOrderByCountAsc(parentDept);
        if (CollectionUtils.isNotEmpty(allPlanCycle)) {
            for (PlanCycle planCycle : allPlanCycle) {
                KpiPlanCycleDto kpiPlanCycleDto = planCycle.castToDto();
                kpiPlanCycleDtos.add(kpiPlanCycleDto);
            }
        }
        return kpiPlanCycleDtos;
    }

    @Override
    public KpiPlanCycleDto getPlanCycleInfoById(MyUserDetail user, Integer planCycleId) {
        if ("".equals(planCycleId + "") || planCycleId == null) {
            return getPlanCycleInfo(user);
        }
        PlanCycle planCycle = planCycleRepository.findById(planCycleId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return planCycle.castToDto();
    }

    @Override
    public List<KpiPlanCycleDto> getPlanCycleByDeptId(Integer deptId) {
        ArrayList<KpiPlanCycleDto> kpiPlanCycleDtos = new ArrayList<>();
        Department parentDept = new Department();
        parentDept.setId(deptId);
        List<PlanCycle> allPlanCycle = planCycleRepository.findByDepartmentOrderByCountAsc(parentDept);
        if (CollectionUtils.isNotEmpty(allPlanCycle)) {
            for (PlanCycle planCycle : allPlanCycle) {
                KpiPlanCycleDto kpiPlanCycleDto = planCycle.castToDto();
                kpiPlanCycleDtos.add(kpiPlanCycleDto);
            }
        }
        return kpiPlanCycleDtos;
    }
}
