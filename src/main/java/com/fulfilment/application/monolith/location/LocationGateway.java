package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocationGateway implements LocationResolver {

    private static final List<Location> locations = new ArrayList<>();

    static {
        locations.add(new Location("ZWOLLE-001", 1, 40));
        locations.add(new Location("ZWOLLE-002", 2, 50));
        locations.add(new Location("AMSTERDAM-001", 5, 100));
        locations.add(new Location("AMSTERDAM-002", 3, 75));
        locations.add(new Location("TILBURG-001", 1, 40));
        locations.add(new Location("HELMOND-001", 1, 45));
        locations.add(new Location("EINDHOVEN-001", 2, 70));
        locations.add(new Location("VETSBY-001", 1, 90));
    }

    @Override
    public Location resolveByIdentifier(String identifier) {
        Optional<Location> location = locations.stream()
                .filter(loc -> loc.identification.equals(identifier))
                .findFirst();
        return location.orElseThrow(() -> new LocationNotFoundException("Location with identifier " + identifier + " not found."));
    }
}
