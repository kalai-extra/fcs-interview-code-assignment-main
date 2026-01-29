package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    // TODO implement this method
//// 1. Business Rule: Check if it's already archived to avoid redundant processing
   if (warehouse.archivedAt != null) {
        return;
  }

//    // 2. Set the archived timestamp to the current time
//    // This transitions the Warehouse from "Active" to "Archived"
  warehouse.archivedAt = java.time.ZonedDateTime.now().toLocalDateTime();
    warehouseStore.update(warehouse);
  }
}
