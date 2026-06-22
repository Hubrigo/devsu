package com.devsu.clientes.repository;

import com.devsu.clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteId(String clienteId);
    Optional<Cliente> findByIdentificacion(String identificacion);
    boolean existsByIdentificacion(String identificacion);
}
