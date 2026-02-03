package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static javax.management.Query.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class CreateWarehouseUseCaseTest {
    @Mock
    WarehouseStore warehouseStore;

    @Mock
    LocationGateway locationGateway;

    @InjectMock
    CreateWarehouseUseCase createWarehouseUseCase;



    @Test
    void testCreateWarehouse_WhenLocationIsFull_ShouldThrow400() {
        // Given
        Warehouse newWarehouse = new Warehouse("MWH.001", "ZWOLLE-001", 100, 50);
        Location mockLocation = new Location("ZWOLLE-001", 1000, 2); // Max 2 warehouses

        when(locationGateway.resolveByIdentifier("ZWOLLE-001")).thenReturn(mockLocation);
        // Simulate that 2 warehouses already exist (location is full)
        when(warehouseStore.countByLocation("ZWOLLE-001")).thenReturn(2L);

        // When & Then
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            createWarehouseUseCase.create(newWarehouse);
        });

        assertEquals(400, exception.getResponse().getStatus());
        assertTrue(exception.getMessage().contains("Maximum warehouse limit reached"));
    }

    @Test
    void testCreateWarehouse_WhenCapacityExceedsLocationLimit_ShouldThrow400() {
        // Given
        // Warehouse tries to have 1500 capacity, but Location only allows 1000
        Warehouse hugeWarehouse = new Warehouse("MWH.002", "ZWOLLE-001", 1500, 0);
        Location mockLocation = new Location("ZWOLLE-001", 1000, 5);

        when(locationGateway.resolveByIdentifier("ZWOLLE-001")).thenReturn(mockLocation);
        when(warehouseStore.countByLocation("ZWOLLE-001")).thenReturn(1L);

        // When & Then
        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            createWarehouseUseCase.create(hugeWarehouse);
        });

        assertEquals(400, exception.getResponse().getStatus());
        assertTrue(exception.getMessage().contains("Capacity exceeds location maximum"));
    }

    @Test
    void testCreateWarehouse_Success() {
        // Given
        Warehouse validWarehouse = new Warehouse("MWH.003", "ZWOLLE-001", 500, 100);
        Location mockLocation = new Location("ZWOLLE-001", 1000, 5);

        when(locationGateway.resolveByIdentifier("ZWOLLE-001")).thenReturn(mockLocation);
        when(warehouseStore.countByLocation("ZWOLLE-001")).thenReturn(0L);
        when(warehouseStore.findByBusinessUnitCode("MWH.003")).thenReturn(null);

        // When
        assertDoesNotThrow(() -> createWarehouseUseCase.create(validWarehouse));

        // Then
        verify(warehouseStore, Mockito.times(1)).create(validWarehouse);
    }
}
