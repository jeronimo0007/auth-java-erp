package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    /**
     * Busca projetos por cliente
     * @param clientId ID do cliente
     * @return Lista de projetos
     */
    List<Project> findByClientId(Integer clientId);
    
    
    /**
     * Busca projetos por status
     * @param status Status do projeto
     * @return Lista de projetos
     */
    List<Project> findByStatus(Integer status);
}
