package com.example.service.impl;

import com.example.DTO.EmployeeDTO;
import com.example.DTO.EmployeeEditPWDTO;
import com.example.DTO.EmployeePageQueryDTO;
import com.example.constant.UserConstant;
import com.example.context.BaseContext;
import com.example.entity.Employee;
import com.example.DTO.AdminLoginDTO;
import com.example.constant.MessageConstant;
import com.example.constant.StatusConstant;
import com.example.exception.BaseException;
import com.example.mapper.EmployeeMapper;
import com.example.result.PageResult;
import com.example.service.EmployeeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee login(AdminLoginDTO emp) throws Exception {
        String username = emp.getUsername();
        String password = emp.getPassword();

        Employee employee = employeeMapper.getByUsername(username);
        if (employee == null) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        log.info("password: {}", password);
        if (!password.equals(employee.getPassword())) {
            throw new BaseException(MessageConstant.PASSWORD_ERROR);
        }

        if (Objects.equals(employee.getStatus(), StatusConstant.DISABLE)) {
            throw new BaseException(MessageConstant.ACCOUNT_LOCKED);
        }

        return employee;
    }

    @Override
    public void addEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 拷贝对象属性
        BeanUtils.copyProperties(employeeDTO, employee);
        String password = DigestUtils.md5DigestAsHex(UserConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);
        employee.setStatus(StatusConstant.ENABLE);
        // 需要根据登陆的状态设置
        Long empId = BaseContext.getCurrentId();
        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.list(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Long empId = BaseContext.getCurrentId();
        Employee employee = Employee.builder()
                        .id(id)
                        .status(status)
                        .updateTime(LocalDateTime.now())
                        .updateUser(empId).build();
        employeeMapper.update(employee);
    }

    @Override
    public Employee getById(Long id) {
        Employee emp = employeeMapper.getById(id);
        return emp;
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        String sex = employee.getSex();
        if (sex.equals("1")) {
            employee.setSex("男");
        } else {
            employee.setSex("女");
        }
        employeeMapper.update(employee);
    }

    @Override
    public void editPassword(EmployeeEditPWDTO employeeEditPWDTO) {
        Employee employee = employeeMapper.getById(BaseContext.getCurrentId());

        if (employee == null) {
            throw new BaseException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        String oddPassword = DigestUtils.md5DigestAsHex(employeeEditPWDTO.getOldPassword().getBytes());
        if (!employee.getPassword().equals(oddPassword)) {
            throw new BaseException(MessageConstant.PASSWORD_EDIT_FAILED);
        }
        String newPassword = DigestUtils.md5DigestAsHex(employeeEditPWDTO.getNewPassword().getBytes());
        employee.setPassword(newPassword);
        employeeMapper.update(employee);
    }
}
