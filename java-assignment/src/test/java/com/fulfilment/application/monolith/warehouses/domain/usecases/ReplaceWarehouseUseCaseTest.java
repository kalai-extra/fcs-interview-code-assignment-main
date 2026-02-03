package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static javax.management.Query.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ReplaceWarehouseUseCaseTest {
    @Mock
    WarehouseStore warehouseStore;

    @Mock
    LocationGateway locationGateway;

    @InjectMocks
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Test
    void testReplace_Success() throws BusinessRuleException {
        // Arrange
        String oldId = "MWH.OLD";
        Location loc = new Location("ZWOLLE-001", 2000, 5);

        Warehouse oldWh = new Warehouse(oldId, "ZWOLLE-001", 1000, 500);
        Warehouse newWh = new Warehouse("MWH.NEW", "ZWOLLE-001", 1200, 500);

        when(warehouseStore.findByBusinessUnitCode(oldId)).thenReturn(oldWh);
        when(locationGateway.resolveByIdentifier("ZWOLLE-001")).thenReturn(loc);

        // Act
        assertDoesNotThrow(() -> replaceWarehouseUseCase.replace(oldId, newWh));

        // Assert
        assertNotNull(oldWh.archivedAt, "Old warehouse should have an archived timestamp");
        verify(warehouseStore).update(oldWh);
        verify(warehouseStore).create(newWh);
    }

    @Test
    void testReplace_ThrowsException_WhenStockDoesNotMatch() {
        // Arrange
        String oldId = "MWH.OLD";
        Warehouse oldWh = new Warehouse(oldId, "ZWOLLE-001", 1000, 500);
        Warehouse newWh = new Warehouse("MWH.NEW", "ZWOLLE-001", 1200, 300); // Mismatching stock

        when(warehouseStore.findByBusinessUnitCode(oldId)).thenReturn(oldWh);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            replaceWarehouseUseCase.replace(oldId, newWh);
        });

        assertTrue(exception.getMessage().contains("Stock matching failed"));
    }

    @Test
    void testReplace_ThrowsException_WhenNewCapacityIsInsufficient() {
        // Arrange
        String oldId = "MWH.OLD";
        Warehouse oldWh = new Warehouse(oldId, "ZWOLLE-001", 1000, 800);
        Warehouse newWh = new Warehouse("MWH.NEW", "ZWOLLE-001", 500, 800); // Capacity (500) < Stock (800)

        when(warehouseStore.findByBusinessUnitCode(oldId)).thenReturn(oldWh);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            replaceWarehouseUseCase.replace(oldId, newWh);
        });

        assertTrue(exception.getMessage().contains("capacity is too small"));
    }
}