package com.twd.SpringSecurityJWT.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.twd.SpringSecurityJWT.entity.OurUsers;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {
    private int id;
    private String name;
    private String description;
    private LocalDateTime estimatedEndtime;
    private LocalDateTime endtime;
    private String status;
    private String priority;
    private int projectId;
    private String projectName; // Added to include the project's name
    private int userId;
    private String userName;    // Added to include the user's name
    private String userEmail;   // Added to include the user's email
    private String userRole;    // Added to include the user's role
    // New field to include comments
    private List<CommentDTO> comments;

}