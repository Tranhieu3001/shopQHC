package com.example.shopQHC.controller.admin;

import com.example.shopQHC.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;

@Controller
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/admin/statistics")
    public String statistics(Model model) {

        int year = Year.now().getValue();

        model.addAttribute("totalCustomers", statisticsService.getTotalCustomers());
        model.addAttribute("topCustomers", statisticsService.getTopCustomersByOrderCount());
        model.addAttribute("topSpending", statisticsService.getTopCustomersBySpending());
        model.addAttribute("topProducts", statisticsService.getTopSellingProducts());
        model.addAttribute("totalRevenue", statisticsService.getTotalRevenue());
        model.addAttribute("monthlyRevenue", statisticsService.getMonthlyRevenue(year));

        return "admin/statistics";
    }
}