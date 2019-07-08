package com.jty.performance.controller.kpi;

import com.jty.performance.domain.Employee;
import com.jty.performance.domain.dto.ImportDetailDto;
import com.jty.performance.exception.BusinessException;
import com.jty.performance.security.MyUserDetail;
import com.jty.performance.service.EmployeeService;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import com.jty.performance.util.ExcelEmployeeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yeting
 * @Date: 2019/6/4 14:07
 */
@RestController
@RequestMapping(value = "/kpi")
public class KpiEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 导出员工模板
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/exportEmployeeTemplate")
    public void exportTeacherTemplate(HttpServletResponse response) throws IOException {
        ExcelEmployeeUtil excelEmployeeUtil = new ExcelEmployeeUtil();
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("application/octet-stream");
        String finalFileName = new String("导入员工信息列表模板.xlsx".getBytes(), "UTF-8");
        response.addHeader("name", URLEncoder.encode(finalFileName, "UTF-8"));
        out.write(excelEmployeeUtil.exportEmployeeTemplate().toByteArray());
    }

    /**
     * 导入员工
     *
     * @param user
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/staffs/import/{departmentId}")
    public Result importEmployees(@AuthenticationPrincipal MyUserDetail user, @PathVariable Integer departmentId, @RequestBody MultipartFile file) throws IOException {
        ExcelEmployeeUtil excelEmployeeUtil = new ExcelEmployeeUtil();
        if (file == null) {
            throw new BusinessException(ResultCode.PARAM_IS_BLANK);
        }
        String fileName = file.getOriginalFilename();
        List<Employee> employeeList = new ArrayList<>();
        if (fileName != null) {
            String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (prefix.equals("xls")) {
                employeeList = excelEmployeeUtil.parseExcel2003(file.getInputStream());
            } else {
                employeeList = excelEmployeeUtil.parseExcel2007(file.getInputStream());
            }
        }
        ImportDetailDto importDetailDto = employeeService.importEmployeeByManagement(user.getUserId(), departmentId, employeeList);

        return Result.success(this.checkIfImportDetailDtoIsNull(importDetailDto));
    }

    private ImportDetailDto checkIfImportDetailDtoIsNull(ImportDetailDto importDetailDto) {
        if (importDetailDto.getSucceedCount() == null) {
            ImportDetailDto newImportDetailDto = new ImportDetailDto();
            importDetailDto.setErrorCount(0);
            importDetailDto.setSucceedCount(0);
            return newImportDetailDto;
        } else {
            return importDetailDto;
        }
    }
}
