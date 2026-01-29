package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.FulFillmentAssignment;
import com.fulfilment.application.monolith.warehouses.domain.ports.FulfillmentStore;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

public class AssignWarehouseToStoreProductUseCase {
    @Inject
    FulfillmentStore assignmentStore; // Your persistence layer for these assignments

    @Transactional
    public void assign(String storeId, String productId, String warehouseId) {

        // Constraint 1: Max 2 Warehouses per Product per Store
        long warehousesForProductInStore = assignmentStore.count(storeId, productId);
        if (warehousesForProductInStore >= 2) {
            throw new WebApplicationException("Product already has 2 fulfillment warehouses for this store", 422);
        }

        // Constraint 2: Max 3 unique Warehouses per Store
        long uniqueWarehousesInStore = assignmentStore.countUniqueWarehousesForStore(storeId);
        // We only increment if this is a NEW warehouse for the store
        boolean isNewWarehouseForStore = !assignmentStore.isWarehouseAssignedToStore(storeId, warehouseId);
        if (isNewWarehouseForStore && uniqueWarehousesInStore >= 3) {
            throw new WebApplicationException("Store cannot be fulfilled by more than 3 different warehouses", 422);
        }

        // Constraint 3: Each Warehouse can store max 5 types of Products
        long productTypesInWarehouse = assignmentStore.countProductTypesInWarehouse(warehouseId);
        boolean isNewProductForWarehouse = !assignmentStore.isProductInWarehouse(warehouseId, productId);
        if (isNewProductForWarehouse && productTypesInWarehouse >= 5) {
            throw new WebApplicationException("Warehouse has reached its limit of 5 product types", 422);
        }

        // If all pass, persist
        assignmentStore.save(new FulFillmentAssignment(storeId, productId, warehouseId));
    }
}

