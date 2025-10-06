package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblprojects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 191)
    private String name;
    
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;
    
    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = 0;
    
    @Column(name = "clientid", nullable = false)
    private Integer clientId;
    
    @Column(name = "billing_type", nullable = false)
    private Integer billingType;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "deadline")
    private LocalDate deadline;
    
    @Column(name = "project_created", nullable = false)
    private LocalDate projectCreated;
    
    @Column(name = "date_finished")
    private LocalDateTime dateFinished;
    
    @Column(name = "progress", columnDefinition = "INT DEFAULT 0")
    private Integer progress = 0;
    
    @Column(name = "progress_from_tasks", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer progressFromTasks = 1;
    
    @Column(name = "project_cost", columnDefinition = "DECIMAL(15,2)")
    private BigDecimal projectCost;
    
    @Column(name = "project_rate_per_hour", columnDefinition = "DECIMAL(15,2)")
    private BigDecimal projectRatePerHour;
    
    @Column(name = "estimated_hours", columnDefinition = "DECIMAL(15,2)")
    private BigDecimal estimatedHours;
    
    @Column(name = "addedfrom", nullable = false)
    private Integer addedFrom;
    
    @Column(name = "contact_notification", columnDefinition = "INT DEFAULT 1")
    private Integer contactNotification = 1;
    
    @Column(name = "notify_contacts", columnDefinition = "MEDIUMTEXT")
    private String notifyContacts;
    
    @PrePersist
    protected void onCreate() {
        projectCreated = LocalDate.now();
    }
    
    // Construtor específico
    public Project(String name, String description, Integer clientId, Integer billingType, LocalDate startDate, Integer addedFrom) {
        this.name = name;
        this.description = description;
        this.clientId = clientId;
        this.billingType = billingType;
        this.startDate = startDate;
        this.addedFrom = addedFrom;
    }
}
