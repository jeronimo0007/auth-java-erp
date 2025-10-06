package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    // Métodos customizados podem ser adicionados aqui se necessário
    
}

