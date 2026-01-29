package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    // TODO Auto-generated method stub
    DbWarehouse entity = mapToDb(warehouse);
    this.persist(entity);
  //  throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  public void update(Warehouse warehouse) {
    // TODO Auto-generated method stub
    DbWarehouse entity = find("businessUnitCode", warehouse.businessUnitCode).firstResult();

    if (entity != null) {
      // Update fields
      entity.capacity = warehouse.capacity;
      entity.stock = warehouse.stock;

      // No need for persist() if within a @Transactional method,
      // but this ensures it's tracked.
    }
    //throw new UnsupportedOperationException("Unimplemented method 'replace'");
  }

  @Override
  public void remove(Warehouse warehouse) {
    // TODO Auto-generated method stub
    delete("businessUnitCode", warehouse.businessUnitCode);
   // throw new UnsupportedOperationException("Unimplemented method 'remove'");
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    // TODO Auto-generated method stub
    DbWarehouse entity = find("businessUnitCode", buCode).firstResult();

    if (entity == null) {
      return null;
    }

    // Map DB Entity -> Domain Model
    return mapToDomain(entity);
   // throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public Warehouse findByLocation(String location) {
    return this.findByLocation(location);
  }

  @Override
  public long countByLocation(String locationIdentifier) {
    // Counts only active (non-archived) warehouses for that location
    return count("location = ?1 and archivedAt is null", locationIdentifier);
  }
  // Helper methods to bridge the gap between DB and Domain
  private DbWarehouse mapToDb(Warehouse w) {
    DbWarehouse db = new DbWarehouse();
    db.businessUnitCode = w.businessUnitCode;
    db.location = w.location;
    db.capacity = w.capacity;
    db.stock = w.stock;
    return db;
  }

  private Warehouse mapToDomain(DbWarehouse db) {
    // Assuming a constructor or builder exists on your Warehouse domain class
    return new Warehouse(
            db.businessUnitCode,
            db.location,
            db.capacity,
            db.stock
    );
  }
}
