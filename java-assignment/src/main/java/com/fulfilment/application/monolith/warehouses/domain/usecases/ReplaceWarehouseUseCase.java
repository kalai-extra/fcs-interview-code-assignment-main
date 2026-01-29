package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
private final LocationGateway locationGateway;
  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationGateway locationGateway) {
    this.warehouseStore = warehouseStore;
      this.locationGateway = locationGateway;
  }

  @Override
  public void replace(String oldWarehouseId, Warehouse newWarehouse) throws BusinessRuleException {
    // TODO implement this method
// 1. Find the existing warehouse
    Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(oldWarehouseId);
    if (oldWarehouse == null) {
      throw new WebApplicationException("Warehouse not found", 404);
    }

    // 2. Resolve Location using the Task 1 method
    Location location = locationGateway.resolveByIdentifier(oldWarehouse.location);

    // 3. Validation: Stock Matching
    // Confirm that the stock of the new warehouse matches the stock of the previous warehouse.
    if (newWarehouse.stock != oldWarehouse.stock) {
      throw new BusinessRuleException("Stock matching failed: New warehouse stock must equal old warehouse stock.");
    }

    // 4. Validation: Capacity Accommodation
    // Ensure the new warehouse's capacity can accommodate the stock from the warehouse being replaced.
    if (newWarehouse.capacity < oldWarehouse.stock) {
      throw new BusinessRuleException("New capacity is too small for existing stock.");
    }
    // 5. Validation: Location Max Capacity
    if (newWarehouse.capacity > location.maxCapacity) {
      throw new BusinessRuleException("New capacity exceeds location limits.");
    }

    // 6. Perform the Replacement
    // Usually involves archiving the old and persisting the new
    warehouseStore.create(newWarehouse);
    warehouseStore.update(oldWarehouse);


  }
}
