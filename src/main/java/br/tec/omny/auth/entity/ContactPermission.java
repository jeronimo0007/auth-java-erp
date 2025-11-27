package br.tec.omny.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblcontact_permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "permission_id", nullable = false)
    private Integer permissionId;
    
    @Column(name = "userid", nullable = false)
    private Long userid;
}

