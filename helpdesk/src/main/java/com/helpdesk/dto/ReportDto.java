package com.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long id;
    private String type; // "POST" or "COMMENT"
    private Long contentId;
    private String contentPreview;
    private String reportedBy;
    private String reason;
    private Date reportedAt;
    private boolean resolved;
    private String resolvedBy;
    private Date resolvedAt;
}