package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationGatewayTest {

    @ParameterizedTest
    @ValueSource(strings = {"ZWOLLE-001", "ZWOLLE-002", "AMSTERDAM-001",
            "AMSTERDAM-002", "TILBURG-001", "HELMOND-001", "HELMOND-001", "EINDHOVEN-001", "VETSBY-001"})
    public void testWhenResolveExistingLocationShouldReturn(String identifier) {
        // given
        LocationGateway locationGateway = new LocationGateway();

        // when
        Location location = locationGateway.resolveByIdentifier(identifier);

        // then
        assertEquals(identifier, location.identification);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UNKNOWN-001", "NOT-EXIST", "INVALID-ID"})
    public void testWhenResolveNonExistingLocationShouldThrowException(String identifier) {
        // given
        LocationGateway locationGateway = new LocationGateway();

        // when & then
        LocationNotFoundException thrown = assertThrows(LocationNotFoundException.class, () -> {
            locationGateway.resolveByIdentifier(identifier);
        });

        // then
        assertEquals("Location with identifier " + identifier + " not found.", thrown.getMessage());
    }
}
