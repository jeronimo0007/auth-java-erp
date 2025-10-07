package br.tec.omny.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteCreationMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer siteId;
    private String contexto;
}
