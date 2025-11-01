package com.example.messenger.controller;

import com.example.messenger.dto.ReportListResp;
import com.example.messenger.dto.ReportResp;
import com.example.messenger.service.ReportService;
import com.example.messenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AdminController {
    private final UserService userService;
    private final ReportService reportService;

    public AdminController(UserService userService, ReportService reportService) {
        this.userService = userService;
        this.reportService = reportService;
    }

    @GetMapping("/admin")
    public ModelAndView panel(ModelAndView model) {
        model.setViewName("admin-panel");
        return model;
    }

    @PostMapping("/reports")
    public ResponseEntity<List<ReportListResp>> getReports() {
        return ResponseEntity.ok(reportService.getReportsList());
    }

    @GetMapping("/report/{id}")
    public ModelAndView reportPage(ModelAndView model, @PathVariable("id") Long reportId) {
        ReportResp rep = reportService.getRepById(reportId);
        model.setViewName("report-info");
        model.addObject("report", rep);
        return model;
    }

    @PostMapping("/accept-report")
    public ResponseEntity<String> acceptReport(@RequestParam("reportId") Long id) {
        reportService.acceptReport(id);
        return ResponseEntity.ok("User banned");
    }

    @PostMapping("/delete-report")
    public ResponseEntity<String> deleteReport(@RequestParam("reportId") Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok("Report deleted");
    }
}
