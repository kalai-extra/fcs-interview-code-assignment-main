package com.fulfilment.application.monolith.warehouses.adapters.database;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "fulfillment_assignment")
public class DbFulfillmentAssignment extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "store_id")
    public String storeId;

    @Column(name = "product_id")
    public String productId;

    @Column(name = "warehouse_id")
    public String warehouseId;
}