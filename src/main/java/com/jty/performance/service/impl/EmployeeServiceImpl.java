package com.jty.performance.service.impl;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.Employee;
import com.jty.performance.domain.IntegralHistoryList;
import com.jty.performance.domain.PlanCycle;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.ImportDetailDto;
import com.jty.performance.domain.form.KpiEmployeeImportDetail;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.DepartmentRepository;
import com.jty.performance.repository.EmployeeRepository;
import com.jty.performance.repository.IntegralHistoryListRepository;
import com.jty.performance.repository.PlanCycleRepository;
import com.jty.performance.repository.auth.RoleRepository;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.service.EmployeeService;
import com.jty.performance.support.ResultCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EmployeeServiceImpl
 *
 * @Author: yeting
 * @Date: 2019/6/5 8:54
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private IntegralHistoryListRepository integralHistoryListRepository;

    @Override
    public ImportDetailDto importEmployeeByManagement(Integer userId, Integer departmentId, List<Employee> employeeList) {
        ImportDetailDto importDetailDto = new ImportDetailDto();
        if (CollectionUtils.isNotEmpty(employeeList)) {
            final int[] count = {2};
            final int[] succeedCount = {0};
            final int[] errorCount = {0};
            Map<Integer, Object> importDetails = new HashMap<>();
            for (Employee one : employeeList) {
                KpiEmployeeImportDetail employeeImportDetail = new KpiEmployeeImportDetail();
                one.setCurrentScore(0);
                one.setRecordScore(0);
                Department department = departmentRepository.findById(departmentId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
                one.setDepartment(department);
                BeanUtils.copyProperties(one, employeeImportDetail);
                try {
                    this.checkAddEmployee(one);
                    succeedCount[0]++;
                    employeeImportDetail.setSucceed(true);
                } catch (BusinessException exception) {
                    errorCount[0]++;
                    employeeImportDetail.setSucceed(false);
                    employeeImportDetail.setErrorMessage(exception.getCode().message());
                }
                count[0]++;
                importDetails.put(count[0], employeeImportDetail);
            }
            importDetailDto.setErrorCount(errorCount[0]);
            importDetailDto.setSucceedCount(succeedCount[0]);
            importDetailDto.setLogs(importDetails);
            return importDetailDto;
        } else {
            return importDetailDto;
        }
    }

    private void checkAddEmployee(Employee one) {
        AuthUser authUser = userRepository.findByAccount(one.getStaffCode()).orElse(null);
        if (authUser != null) {
            Employee employee = employeeRepository.findByAuthUser(authUser).orElse(null);
            if (employee != null) {
                throw new BusinessException(ResultCode.USER_HAS_EXISTED);
            }
        } else {
            authUser = new AuthUser();
            authUser.setAccount(one.getStaffCode());
            authUser.setEnabled(true);
            authUser.setPassword(passwordEncoder.encode("123456"));
            authUser.addRole(roleRepository.findById(2).orElse(null));
            userRepository.save(authUser);
        }
        one.setId(authUser.getId());
        one.setAuthUser(authUser);
        employeeRepository.save(one);
        PlanCycle planCycle = getPlanCycleInfo(one.getDepartment().getId());
        if (planCycle.getCount() > 1) {
            PlanCycle histPlanCycle = planCycleRepository.findByCountAndDepartment(planCycle.getCount() - 1, planCycle.getDepartment()).orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
            Integer rank = integralHistoryListRepository.findByPlanCycleIdAndRankMax(histPlanCycle.getId());
            IntegralHistoryList integralHistoryList = new IntegralHistoryList();
            integralHistoryList.setPlanCycleId(histPlanCycle.getId());
            integralHistoryList.setEmployee(one);
            integralHistoryList.setRank(rank++);
            integralHistoryList.setScore(0);
            integralHistoryList.setDepartment(one.getDepartment());
            integralHistoryListRepository.save(integralHistoryList);
        }
    }

    private PlanCycle getPlanCycleInfo(Integer deptId) {
        Department department = departmentRepository.findById(deptId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department parentDept = new Department();
        parentDept.setId(department.getParentId());
        PlanCycle planCycle = planCycleRepository.findByPlanCycleStateAndDepartment(1, parentDept).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        return planCycle;
    }
}
