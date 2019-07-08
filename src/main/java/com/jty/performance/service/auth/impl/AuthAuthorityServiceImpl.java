package com.jty.performance.service.auth.impl;

import com.jty.performance.domain.auth.AuthAuthority;
import com.jty.performance.domain.auth.AuthRole;
import com.jty.performance.domain.auth.AuthUser;
import com.jty.performance.domain.dto.auth.AuthAuthorityDto;
import com.jty.performance.repository.auth.AuthorityRepository;
import com.jty.performance.service.auth.AuthAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限service实现类
 *
 * @author Jason
 * @since 2019/1/2 14:47
 */
@Service
public class AuthAuthorityServiceImpl implements AuthAuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public List<? extends GrantedAuthority> getAuthority(Integer userId) {
        List<AuthAuthority> authorities = authorityRepository.findAll(createByUserId(userId));
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorities.stream().forEach(authAuthority ->
                authorityList.add(new SimpleGrantedAuthority(authAuthority.getCode()))
        );
        return authorityList;
    }

    @Override
    public List<AuthAuthorityDto> getAuthorities() {
        List<AuthAuthority> listAll = authorityRepository.findAll();
        if(listAll.isEmpty()){
            return null;
        }
        List<AuthAuthorityDto> dtoList=new ArrayList<>();
        listAll.forEach(r->{
            dtoList.add(r.castToDto());
        });
        return dtoList;
    }


    private static Specification<AuthAuthority> createByUserId(Integer userId) {
        return new Specification<AuthAuthority>() {
            public Predicate toPredicate(Root<AuthAuthority> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicate = new ArrayList<>();
                Join<AuthAuthority, AuthRole> roleJoin = root.join("roles", JoinType.INNER);
                Join<AuthRole, AuthUser> userJoin = roleJoin.join("users", JoinType.INNER);
                predicate.add(cb.equal(userJoin.get("id"), userId));
                Predicate[] pre = new Predicate[predicate.size()];
                return query.distinct(true).where(predicate.toArray(pre)).getRestriction();
            }
        };
    }
}
