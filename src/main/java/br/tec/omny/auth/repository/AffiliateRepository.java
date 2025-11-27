package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Affiliate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateRepository extends JpaRepository<Affiliate, Integer> {
    
    Optional<Affiliate> findByAffiliateSlug(String affiliateSlug);
}

