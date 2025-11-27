package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.SiteImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteImageRepository extends JpaRepository<SiteImage, Integer> {

    List<SiteImage> findBySiteId(Integer siteId);
}