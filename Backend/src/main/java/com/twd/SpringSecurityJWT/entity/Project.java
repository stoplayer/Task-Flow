package com.twd.SpringSecurityJWT.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    private int budget;

    private String description;

    private LocalDateTime estimatedEndtime;

    private LocalDateTime endtime;

    @Enumerated(EnumType.STRING)
    private Status status; // This will track the status of the project

    @ManyToMany
    @JoinTable(
            name = "project_team",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<OurUsers> team = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference  // Prevent recursion during serialization (for tasks)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // Prevent Hibernate proxy serialization issues
    private List<Tasks> tasks = new ArrayList<>();

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

