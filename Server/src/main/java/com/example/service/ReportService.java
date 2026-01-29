package com.example.service;

import com.example.VO.OrderReportVO;
import com.example.VO.SalesTop10ReportVO;
import com.example.VO.TurnoverReportVO;
import com.example.VO.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;

public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

    void exportBusinessData(HttpServletResponse response);
}
