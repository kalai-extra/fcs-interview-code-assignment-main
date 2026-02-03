package com.fulfilment.application.monolith.warehouses.domain.usecases;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.management.Query;
import java.time.LocalDateTime;

import static io.smallrye.common.constraint.Assert.assertTrue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArchiveWarehouseUseCaseTest {
    @Mock
    WarehouseStore warehouseStore;

    @InjectMocks
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @Test
    void testArchive_ShouldSetTimestampAndUpdate() {
        // Arrange
        Warehouse activeWh = new Warehouse("MWH.001", "ZWOLLE-001", 1000, 500);
        activeWh.archivedAt = null; // Ensure it starts as active

        // Act
        archiveWarehouseUseCase.archive(activeWh);

        // Assert
        assertNotNull(activeWh.archivedAt, "ArchivedAt timestamp should be set");
        verify(warehouseStore, Query.times(1)).update(activeWh);
    }

    @Test
    void testArchive_WhenAlreadyArchived_ShouldDoNothing() {
        // Arrange
        Warehouse alreadyArchivedWh = new Warehouse("MWH.001", "ZWOLLE-001", 1000, 500);
        LocalDateTime previousTimestamp = LocalDateTime.now().minusDays(1);
        alreadyArchivedWh.archivedAt = previousTimestamp;

        // Act
        archiveWarehouseUseCase.archive(alreadyArchivedWh);

        // Assert
        assertEquals(previousTimestamp, alreadyArchivedWh.archivedAt, "Timestamp should not change");
        verify(warehouseStore, never()).update(any());
    }

}
