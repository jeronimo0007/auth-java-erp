package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    
    /**
     * Busca um staff pelo email
     * @param email Email do staff
     * @return Optional contendo o staff se encontrado
     */
    Optional<Staff> findByEmail(String email);
    
    /**
     * Verifica se existe um staff com o email informado
     * @param email Email a ser verificado
     * @return true se o email existe, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca um staff pelo email e status ativo
     * @param email Email do staff
     * @param active Status ativo (1 = ativo, 0 = inativo)
     * @return Optional contendo o staff se encontrado
     */
    Optional<Staff> findByEmailAndActive(String email, Integer active);
}
