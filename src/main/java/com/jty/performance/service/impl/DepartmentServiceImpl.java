package com.jty.performance.service.impl;

import com.jty.performance.domain.Department;
import com.jty.performance.domain.PlanCycle;
import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.DepartmentTreeDto;
import com.jty.performance.domain.dto.KpiDepartmentDto;
import com.jty.performance.domain.dto.StaffNodeDto;
import com.jty.performance.domain.form.DepartmentForm;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.DepartmentRepository;
import com.jty.performance.repository.PlanCycleRepository;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.DepartmentService;
import com.jty.performance.support.ResultCode;
import com.jty.performance.util.PoCastUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/6/17 9:26
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PlanCycleRepository planCycleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public KpiDepartmentDto addDepartmentCore(DepartmentForm form) {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Department department = departmentRepository.findByParentIdAndDeptName(0, form.getDeptName()).orElse(null);
        if (department != null) {
            throw new BusinessException(ResultCode.DEPARTMENT_HAS_EXIST);
        }
        Department newDepartment = new Department();
        newDepartment.setDeptName(form.getDeptName());
        newDepartment.setParentId(0);
        Department saveDepartment = departmentRepository.save(newDepartment);

        //创建该中心的新考核期
        PlanCycle newPlanCycle = new PlanCycle();
        LocalDate endLocalDate = localDate.plusMonths(1);
        newPlanCycle.setStartTime(localDate);
        newPlanCycle.setEndTime(endLocalDate);
        newPlanCycle.setPlanCycleName("第1考核期");
        newPlanCycle.setPlanCycleState(1);
        newPlanCycle.setPlanCycleUseScore(0);
        newPlanCycle.setCount(1);
        newPlanCycle.setDepartment(saveDepartment);
        planCycleRepository.save(newPlanCycle);
        return saveDepartment.castToDto();
    }

    @Override
    public KpiDepartmentDto addDepartmentChild(Integer deptIdCore, DepartmentForm form) {
        Department department = departmentRepository.findByParentIdAndDeptName(deptIdCore, form.getDeptName()).orElse(null);
        if (department != null) {
            throw new BusinessException(ResultCode.DEPARTMENT_HAS_EXIST);
        }
        Department newDepartment = new Department();
        newDepartment.setDeptName(form.getDeptName());
        newDepartment.setParentId(deptIdCore);
        Department saveDepartment = departmentRepository.save(newDepartment);
        return saveDepartment.castToDto();
    }

    @Override
    public List<DepartmentTreeDto> getDepartmentsByAuth(MyUserDetail user) {
//        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
//        for (GrantedAuthority grantedAuthority : authorities) {
//            String authority = grantedAuthority.getAuthority();
//            if ("KPI_MANAGE_DEPARTMENT".equals(authority)) {
//                List<DepartmentTreeDto> list = PoCastUtils.poCastToDto(departmentRepository.findAllByParentId(user.getDeptId()), DepartmentTreeDto.class);
//                return list;
//            }
//        }
        AuthUser authUser = userRepository.findById(user.getUserId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        List<AuthRole> roles = authUser.getRoles();
        Boolean flag = false;
        if (CollectionUtils.isNotEmpty(roles)) {
            for (AuthRole authRole : roles) {
                if ("5".equals(authRole.getId() + "") || "3".equals(authRole.getId() + "")) {
                    flag = true;
                }
            }
        }
        if (flag) {
            List<DepartmentTreeDto> list = PoCastUtils.poCastToDto(departmentRepository.findAllByParentId(0), DepartmentTreeDto.class);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(DepartmentTreeDto -> {
                    DepartmentTreeDto.setChildren(this.findChildren(DepartmentTreeDto.getId()));
                });
            }
            return list;
        } else {
            Department department = departmentRepository.findById(user.getDeptId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            Department parentDept = departmentRepository.findById(department.getParentId()).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
            List<DepartmentTreeDto> list = new ArrayList<>();
            DepartmentTreeDto departmentTreeDto = new DepartmentTreeDto();
            departmentTreeDto.setDeptName(parentDept.getDeptName());
            departmentTreeDto.setId(parentDept.getId());
            departmentTreeDto.setParentId(parentDept.getParentId());
            List<DepartmentTreeDto> children = this.findChildren(parentDept.getId());
            departmentTreeDto.setChildren(children);
            list.add(departmentTreeDto);
            return list;
        }
    }

    @Override
    public KpiDepartmentDto alterDepartment(Integer deptId, DepartmentForm form) {
        Department department = departmentRepository.findById(deptId).orElseThrow(() -> new BusinessException(ResultCode.DATA_IS_WRONG));
        Department exitDept = departmentRepository.findByParentIdAndDeptName(department.getParentId(), form.getDeptName()).orElse(null);
        if (exitDept != null) {
            throw new BusinessException(ResultCode.DEPARTMENT_HAS_EXIST);
        }
        department.setDeptName(form.getDeptName());
        Department saveDept = departmentRepository.save(department);
        return saveDept.castToDto();
    }

    @Override
    public List<DepartmentTreeDto> getDepartmentCores() {
        List<DepartmentTreeDto> list = PoCastUtils.poCastToDto(departmentRepository.findAllByParentId(0), DepartmentTreeDto.class);
        return list;
    }

    private List<DepartmentTreeDto> findChildren(Integer parentId) {
        List<DepartmentTreeDto> list = PoCastUtils.poCastToDto(departmentRepository.findAllByParentId(parentId), DepartmentTreeDto.class);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(DepartmentTreeDto -> {
                DepartmentTreeDto.setChildren(this.findChildren(DepartmentTreeDto.getId()));
            });
        }
        return list;
    }
}
