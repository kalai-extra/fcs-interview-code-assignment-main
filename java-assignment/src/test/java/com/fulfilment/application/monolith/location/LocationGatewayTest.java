package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();
    String expectedIdentifier = "ZWOLLE-001";

    // when
    Location location = locationGateway.resolveByIdentifier(expectedIdentifier);

    // then
    assertNotNull(location, "Location should not be null");
    assertEquals(expectedIdentifier, location.identification); // Use the field name from your Location class

    // Additional check to verify the data from your list is actually loaded
    assertEquals(1000, location.maxCapacity, "Max capacity should match the list value");
    assertEquals(5, location.maxNumberOfWarehouses, "Max warehouses should match the list value");
  }

}
