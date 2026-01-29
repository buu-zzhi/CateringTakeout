package com.example.controller.admin;

import com.example.DTO.EmployeeDTO;
import com.example.DTO.EmployeeEditPWDTO;
import com.example.DTO.EmployeePageQueryDTO;
import com.example.VO.EmployeeLoginVO;
import com.example.entity.Employee;
import com.example.DTO.AdminLoginDTO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.constant.JwtClaimsConstant;
import com.example.properties.JwtProperties;
import com.example.service.EmployeeService;
import com.example.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody AdminLoginDTO emp) throws Exception {
        Employee e = employeeService.login(emp);
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, e.getId());
        String jwt = JwtUtils.generateJwt(claims, jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl());
        EmployeeLoginVO emplvo = EmployeeLoginVO.builder()
                .id(e.getId())
                .userName(e.getUsername())
                .name(e.getName())
                .token(jwt)
                .build();
        log.info(emplvo.toString());
        return Result.success(emplvo);
    }

    @ApiOperation(value="退出登录")
    @PostMapping("/logout")
    public Result logout() {
        return Result.success();
    }

    @PostMapping
    public Result addEmp(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.addEmp(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        return Result.success(emp);
    }

    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.update(employeeDTO);
        return Result.success();
    }

    @PutMapping("/editPassword")
    public Result editPassword(@RequestBody EmployeeEditPWDTO employeeEditPWDTO) {
        employeeService.editPassword(employeeEditPWDTO);
        return Result.success();
    }
}
