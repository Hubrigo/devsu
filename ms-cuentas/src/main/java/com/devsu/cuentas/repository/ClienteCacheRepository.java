package com.devsu.cuentas.repository;

import com.devsu.cuentas.entity.ClienteCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteCacheRepository extends JpaRepository<ClienteCache, Long> {
}
