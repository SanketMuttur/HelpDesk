package com.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String accountStatus;
    private Date createdAt;
    private Date lastLoginDate;
    private List<String> roles;
    private int postCount;
    private int commentCount;
    private String suspensionReason;
    private Date suspensionEndDate;
}