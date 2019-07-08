package com.jty.performance.service.impl;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.Employee;
import com.jty.performance.domain.LogStaffSign;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.form.auth.PasswordForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.DepartmentRepository;
import com.jty.performance.repository.EmployeeRepository;
import com.jty.performance.repository.LogStaffSignRepository;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.KpiAuthUserService;
import com.jty.performance.support.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/5/15 11:22
 */
@Service
public class KpiAuthUserServiceImpl implements KpiAuthUserService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogStaffSignRepository logStaffSignRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public UserDetails getUserInfo(MyUserDetail user) {
//        MyUserDetail myUserDetail = (MyUserDetail) user;
        Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Boolean result = checkSigned(user.getUserId());
        List<Employee> Employees = employeeRepository.findOrderByCurrentScore(department.getParentId());
        Integer rank = 0;
        for (Employee employee : Employees) {
            rank++;
            if (user.getUserId().equals(employee.getAuthUser().getId())) {
                user.setRank(rank);
                user.setQuarterIntegra(employee.getCurrentScore());
                user.setQualification(employee.getRecordScore());
                user.setUsername(employee.getEName());
                user.setDeptId(employee.getDepartment().getId());
                user.setIsSigned(result);
                break;
            } else {
                List<Employee> allEmployees = employeeRepository.findAll();
                for (Employee emp : allEmployees) {
                    if (user.getUserId().equals(emp.getAuthUser().getId())) {
                        user.setRank(0);
                        user.setQuarterIntegra(emp.getCurrentScore());
                        user.setQualification(emp.getRecordScore());
                        user.setUsername(emp.getEName());
                        user.setDeptId(employee.getDepartment().getId());
                        user.setIsSigned(result);
                        break;
                    }
                }

            }
        }
        return user;
    }

    /**
     * 判断用户是否签到
     *
     * @param userId
     * @return
     */
    private Boolean checkSigned(Integer userId) {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LogStaffSign logStaffSign = logStaffSignRepository.findByUserIdAndSignTime(userId, localDate).orElse(null);
        if (logStaffSign == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void updatePassword(Integer userId, PasswordForm passwordForm) {
        AuthUser authUser = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        if (passwordEncoder.matches(passwordForm.getOldPassword(), authUser.getPassword())) {
            authUser.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
            userRepository.save(authUser);
        } else {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
    }
}
