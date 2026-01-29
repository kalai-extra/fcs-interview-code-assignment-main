package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements com.fulfilment.application.monolith.warehouses.adapters.restapi.WarehouseResource {

  @Inject private WarehouseRepository warehouseRepository;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    // TODO Auto-generated method stub
    warehouseRepository.create(data);
    return data;
   // throw new UnsupportedOperationException("Unimplemented method 'createANewWarehouseUnit'");
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    return null;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(Long id) {
    return warehouseRepository.findById(id).toWarehouse();
    //throw new UnsupportedOperationException("Unimplemented method 'getAWarehouseUnitByID'");
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    // TODO Auto-generated method stub
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);

    if (warehouse == null) {
      throw new WebApplicationException("Cannot archive: Warehouse not found with ID: " + id, 404);
    }

    // Call the remove method which implements the "Archive" (soft delete) logic
    warehouseRepository.remove(warehouse);

   // throw new UnsupportedOperationException("Unimplemented method 'archiveAWarehouseUnitByID'");
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
          String businessUnitCode, @NotNull Warehouse data) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Unimplemented method 'replaceTheCurrentActiveWarehouse'");
  }

}

