package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.adapters.database.DbFulfillmentAssignment;
import com.fulfilment.application.monolith.warehouses.domain.models.FulFillmentAssignment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.count;
import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.persist;
import static java.nio.file.Files.find;

@ApplicationScoped
public class FulfillmentStore implements PanacheRepository<DbFulfillmentAssignment> {

    // Requirement 1: Count warehouses for a specific product in a specific store
    public long countWarehouses(String storeId, String productId) {
        return count("storeId = ?1 AND productId = ?2", storeId, productId);
    }

    // Requirement 2: Count unique warehouses assigned to a store
    public long countUniqueWarehousesForStore(String storeId) {
        return find("SELECT DISTINCT warehouseId FROM DbFulfillmentAssignment WHERE storeId = ?1", storeId)
                .stream().count();
    }

    // Requirement 3: Count unique product types in a warehouse
    public long countProductTypesInWarehouse(String warehouseId) {
        return find("SELECT DISTINCT productId FROM DbFulfillmentAssignment WHERE warehouseId = ?1", warehouseId)
                .stream().count();
    }

    // Check if a specific warehouse is already linked to a store
    public boolean isWarehouseAssignedToStore(String storeId, String warehouseId) {
        return count("storeId = ?1 AND warehouseId = ?2", storeId, warehouseId) > 0;
    }

    // Check if a specific product is already in a warehouse
    public boolean isProductInWarehouse(String warehouseId, String productId) {
        return count("warehouseId = ?1 AND productId = ?2", warehouseId, productId) > 0;
    }

    @Transactional
    public void save(FulFillmentAssignment domain) {
        DbFulfillmentAssignment entity = new DbFulfillmentAssignment();
        entity.storeId = domain.storeId;
        entity.productId = domain.productId;
        entity.warehouseId = domain.warehouseId;
        persist(entity);
    }
}