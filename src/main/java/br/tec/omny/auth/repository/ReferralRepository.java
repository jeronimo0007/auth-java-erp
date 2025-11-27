package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Integer> {

    Optional<Referral> findByClientId(Integer clientId);
}

