package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  LocationGateway locationGateway;
  public CreateWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void create(Warehouse warehouse) {
    // TODO implement this method
// 1. Business Unit Code Verification
    // Ensure the warehouse code doesn't already exist
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WebApplicationException("Business Unit Code already exists: " + warehouse.businessUnitCode, 422);
    }

    // 2. Location Validation
    // Confirm the location is valid using the gateway from Task 1
    Location location = locationGateway.resolveByIdentifier(warehouse.location);

    // 3. Warehouse Creation Feasibility
    // Check if max warehouses for this location has been reached.
    // Using Panache count logic on the Store/Repository:
    long activeCount = warehouseStore.countByLocation(warehouse.location);
    if (activeCount >= location.maxNumberOfWarehouses) {
      throw new WebApplicationException("Maximum warehouse limit reached for location: " + location.identification, 400);
    }
    // 4. Capacity and Stock Validation
    // Validate capacity doesn't exceed the location's limit
    if (warehouse.capacity > location.maxCapacity) {
      throw new WebApplicationException("Capacity exceeds location maximum: " + location.maxCapacity, 400);
    }

    // Ensure it can handle the stock informed
    if (warehouse.stock > warehouse.capacity) {
      throw new WebApplicationException("Current stock exceeds warehouse capacity.", 400);
    }
    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
  }
}
