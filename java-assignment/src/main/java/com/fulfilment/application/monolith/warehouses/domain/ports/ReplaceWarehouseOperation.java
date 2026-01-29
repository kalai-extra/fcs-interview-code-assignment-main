package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.BusinessRuleException;

public interface ReplaceWarehouseOperation {


  void replace(String oldWarehouseId, Warehouse newWarehouse) throws BusinessRuleException;
}
