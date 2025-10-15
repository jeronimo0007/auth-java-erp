package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    /**
     * Busca contato por email
     * @param email Email do contato
     * @return Optional com o contato encontrado
     */
    Optional<Contact> findByEmail(String email);
    
    /**
     * Verifica se existe um contato com o email informado
     * @param email Email a ser verificado
     * @return true se o email já existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca contatos por userId
     * @param userId ID do usuário
     * @return Lista de contatos do usuário
     */
    java.util.List<Contact> findByUserId(Long userId);
    
    /**
     * Busca contato primário por userId
     * @param userId ID do usuário
     * @param isPrimary Se é contato primário
     * @return Optional com o contato primário encontrado
     */
    Optional<Contact> findByUserIdAndIsPrimary(Long userId, Boolean isPrimary);
    
}

