package com.example.messenger.service;

import com.example.messenger.dto.ReportListResp;
import com.example.messenger.dto.ReportResp;
import com.example.messenger.repository.MessageRepository;
import com.example.messenger.repository.ReportRepository;
import com.example.messenger.repository.UserRepository;
import com.example.messenger.repository.model.Message;
import com.example.messenger.repository.model.Report;
import com.example.messenger.repository.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private ReportRepository reportRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void create(String reporterEmail, Long messageId) {
        User reporter = userRepository.findByEmail(reporterEmail);
        Message msg = messageRepository.findById(messageId).get();
        Report newReport = new Report();
        newReport.setReporter(reporter);
        newReport.setUser(msg.getSender());
        newReport.setMessage(msg);
        newReport.setAccepted(false);
        newReport.setEditedAt(LocalDateTime.now());
        reportRepository.save(newReport);
    }

    public void acceptReport(Long reportId) {
        Report report = reportRepository.findById(reportId).get();
        report.setAccepted(true);
        report.setEditedAt(LocalDateTime.now());
        User bannedUser = userRepository.findById(report.getUser().getId()).get();
        bannedUser.setActive(false);
        userRepository.save(bannedUser);
        messageRepository.deleteById(report.getMessage().getId());
        reportRepository.save(report);
    }

    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    public ReportResp getRepById(Long id) {
        Report rep = reportRepository.findById(id).get();
        List<Message> context = messageRepository.findContextMessages(rep.getMessage().getChatRoom(), rep.getMessage().getTimestamp().minusHours(3), rep.getMessage().getTimestamp());
        ReportResp resp = new ReportResp(rep.getId(), rep.getUser().getId(), rep.getUser().getUsername(), rep.getMessage().getId(), rep.getMessage().getContent(), context, rep.getReporter().getId());
        return resp;
    }

    public List<ReportListResp> getReportsList() {
        List<ReportListResp> reports = new ArrayList<>();
        reportRepository.findAllByIsAcceptedFalse().forEach(report -> {
            ReportListResp newRep = new ReportListResp(report.getId(), report.getUser().getId(), report.getUser().getUsername(), report.getMessage().getId(), report.getMessage().getContent(), report.getReporter().getId());
            reports.add(newRep);
        });
        return reports;
    }
}
