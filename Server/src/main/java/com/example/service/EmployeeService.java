package com.example.service;


import com.example.DTO.EmployeeDTO;
import com.example.DTO.EmployeeEditPWDTO;
import com.example.DTO.EmployeePageQueryDTO;
import com.example.entity.Employee;
import com.example.DTO.AdminLoginDTO;
import com.example.result.PageResult;


public interface EmployeeService {
    public Employee login(AdminLoginDTO emp) throws Exception;

    void addEmp(EmployeeDTO employeeDTO);

    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void startOrStop(Integer status, Long id);

    Employee getById(Long id);

    void update(EmployeeDTO employeeDTO);

    void editPassword(EmployeeEditPWDTO employeeEditPWDTO);
}
