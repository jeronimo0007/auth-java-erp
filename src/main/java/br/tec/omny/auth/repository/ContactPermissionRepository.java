package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.ContactPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPermissionRepository extends JpaRepository<ContactPermission, Integer> {
}

