package com.jty.performance.security;

import com.jty.performance.domain.Employee;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.repository.DepartmentRepository;
import com.jty.performance.repository.EmployeeRepository;
import com.jty.performance.repository.auth.UserRepository;
import com.jty.performance.service.auth.AuthAuthorityService;
import com.jty.performance.support.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 处理登录校验逻辑
 *
 * @Author: yeting
 * @Date: 2019/5/14 19:31
 */
@Component
public class PerformanceUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthAuthorityService authAuthorityService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        MyUserDetail detail = new MyUserDetail();
        logger.info("登录用户名" + s);
        AuthUser authUser = userRepository.findByAccount(s).orElseThrow(() -> new BusinessException(ResultCode.USER_LOGIN_ERROR));
        detail.setUserId(authUser.getId());
        detail.setUsername(authUser.getAccount());
        detail.setEnabled(authUser.getEnabled());
        detail.setAuthorities(authAuthorityService.getAuthority(authUser.getId()));
        detail.setPassword(authUser.getPassword());
        Employee employee = employeeRepository.findByAuthUser(authUser).orElseThrow(() -> new BusinessException(ResultCode.USER_LOGIN_ERROR));
        detail.setDeptId(employee.getDepartment().getId());
        //根据用户名查找到用户信息
        return detail;
    }
}
