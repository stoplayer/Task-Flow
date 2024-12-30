package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Tasks task;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OurUsers user;

    private LocalDateTime createdAt;

    // Constructors
    public Comment() {
    }

    public Comment(String content, Tasks task, OurUsers user) {
        this.content = content;
        this.task = task;
        this.user = user;
        this.createdAt = LocalDateTime.now(); // Automatically set the timestamp
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Tasks getTask() {
        return task;
    }

    public void setTask(Tasks task) {
        this.task = task;
    }

    public OurUsers getUser() {
        return user;
    }

    public void setUser(OurUsers user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
