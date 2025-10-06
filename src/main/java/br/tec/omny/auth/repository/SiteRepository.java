package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    // findById já está disponível no JpaRepository

    List<Site> findByClientId(Integer clientId);

    List<Site> findByStatus(Integer status);

    List<Site> findByTipoSite(String tipoSite);

    Optional<Site> findByDominio(String dominio);
}
