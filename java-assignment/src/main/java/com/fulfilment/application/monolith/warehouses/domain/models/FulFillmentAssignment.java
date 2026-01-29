package com.fulfilment.application.monolith.warehouses.domain.models;

public class FulFillmentAssignment {

    public String storeId;
    public String productId;
    public String warehouseId;

    public FulFillmentAssignment(String storeId, String productId, String warehouseId) {
        this.storeId = storeId;
        this.productId = productId;
        this.warehouseId = warehouseId;
    }

    public FulFillmentAssignment(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getProductId() {
        return productId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
}
