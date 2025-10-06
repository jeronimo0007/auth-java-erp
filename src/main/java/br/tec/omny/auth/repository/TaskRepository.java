package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    
    /**
     * Busca tasks por projeto
     * @param relId ID do projeto
     * @param relType Tipo da relação (project)
     * @return Lista de tasks
     */
    List<Task> findByRelIdAndRelType(Integer relId, String relType);
    
    /**
     * Busca tasks por status
     * @param status Status da task
     * @return Lista de tasks
     */
    List<Task> findByStatus(Integer status);
    
    /**
     * Busca tasks por criador
     * @param addedFrom ID do criador
     * @return Lista de tasks
     */
    List<Task> findByAddedFrom(Integer addedFrom);
}
