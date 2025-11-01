package com.example.messenger.repository;

import com.example.messenger.repository.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByIsAcceptedFalse();
}
