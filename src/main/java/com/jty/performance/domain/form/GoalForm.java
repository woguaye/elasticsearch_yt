package com.jty.performance.domain.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

/**
 * GoalForm
 *
 * @Author: yeting
 * @Date: 2019/6/27 9:34
 */
@Data
public class GoalForm extends PageableForm {

    /**
     * 目标名称
     */
    private String goalName;

    /**
     * 目标开始时间
     */
    private LocalDate goalStart;

    /**
     * 目标预计结束时间
     */
    private LocalDate goalGuessEnd;

    /**
     * 目标描述
     */
    @Length(max = 200, message = "内容不能超过200个字符")
    private String description;

    /**
     * 目标的责任人结构
     */
    private List<EmployeeAccForm> employeeAcceptList;

    /**
     * 部门ID
     */
    private Integer deptId;


}
