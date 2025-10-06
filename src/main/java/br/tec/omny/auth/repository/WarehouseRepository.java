package br.tec.omny.auth.repository;

import br.tec.omny.auth.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    
    /**
     * Busca um warehouse pelo ID
     * @param warehouseId ID do warehouse
     * @return Optional contendo o warehouse se encontrado
     */
    Optional<Warehouse> findByWarehouseId(Integer warehouseId);
    
    /**
     * Busca um warehouse pelo código
     * @param warehouseCode Código do warehouse
     * @return Optional contendo o warehouse se encontrado
     */
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);
    
    /**
     * Busca um warehouse pelo CNPJ
     * @param cnpj CNPJ do warehouse
     * @return Optional contendo o warehouse se encontrado
     */
    Optional<Warehouse> findByCnpj(String cnpj);
    
    /**
     * Verifica se existe um warehouse com o ID informado
     * @param warehouseId ID a ser verificado
     * @return true se o ID existe, false caso contrário
     */
    boolean existsByWarehouseId(Integer warehouseId);
}
