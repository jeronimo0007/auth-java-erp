package br.tec.omny.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteCreationMessage {
    
    private Integer siteId;
    private String contexto;
}
