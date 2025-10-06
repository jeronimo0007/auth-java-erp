package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbltasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", columnDefinition = "LONGTEXT")
    private String name;
    
    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;
    
    @Column(name = "priority")
    private Integer priority;
    
    @Column(name = "dateadded", nullable = false)
    private LocalDateTime dateAdded;
    
    @Column(name = "startdate", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "duedate")
    private LocalDate dueDate;
    
    @Column(name = "datefinished")
    private LocalDateTime dateFinished;
    
    @Column(name = "addedfrom", nullable = false)
    private Integer addedFrom;
    
    @Column(name = "is_added_from_contact", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isAddedFromContact = false;
    
    @Column(name = "status", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = 0;
    
    @Column(name = "recurring_type", length = 10)
    private String recurringType;
    
    @Column(name = "repeat_every")
    private Integer repeatEvery;
    
    @Column(name = "recurring", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer recurring = 0;
    
    @Column(name = "is_recurring_from")
    private Integer isRecurringFrom;
    
    @Column(name = "cycles", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer cycles = 0;
    
    @Column(name = "total_cycles", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer totalCycles = 0;
    
    @Column(name = "custom_recurring", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean customRecurring = false;
    
    @Column(name = "last_recurring_date")
    private LocalDate lastRecurringDate;
    
    @Column(name = "rel_id")
    private Integer relId;
    
    @Column(name = "rel_type", length = 30)
    private String relType;
    
    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isPublic = false;
    
    @Column(name = "billable", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean billable = false;
    
    @Column(name = "billed", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean billed = false;
    
    @Column(name = "invoice_id", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer invoiceId = 0;
    
    @Column(name = "hourly_rate", nullable = false, columnDefinition = "DECIMAL(15,2) DEFAULT 0.00")
    private BigDecimal hourlyRate = BigDecimal.ZERO;
    
    @Column(name = "milestone", columnDefinition = "INT DEFAULT 0")
    private Integer milestone = 0;
    
    @Column(name = "kanban_order", columnDefinition = "INT DEFAULT 1")
    private Integer kanbanOrder = 1;
    
    @Column(name = "milestone_order", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer milestoneOrder = 0;
    
    @Column(name = "visible_to_client", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean visibleToClient = false;
    
    @Column(name = "deadline_notified", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer deadlineNotified = 0;
    
    @PrePersist
    protected void onCreate() {
        dateAdded = LocalDateTime.now();
    }
    
    // Construtor específico
    public Task(String name, String description, LocalDate startDate, Integer addedFrom, Integer relId, String relType) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.addedFrom = addedFrom;
        this.relId = relId;
        this.relType = relType;
    }
}
