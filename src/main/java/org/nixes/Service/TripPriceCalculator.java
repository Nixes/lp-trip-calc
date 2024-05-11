package org.nixes.Service;

import com.google.inject.Inject;
import org.nixes.Model.Tap;
import org.nixes.Enum.TripStatus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for calculating the charge amount for a trip.
 */
public class TripPriceCalculator implements ITripPriceCalculator {
    private HashMap<Set<String>, BigDecimal> fareRules;
    private HashMap<String, BigDecimal> maxFares;

    @Inject
    public TripPriceCalculator() {
        this.setFareRules(new HashMap<Set<String>, BigDecimal>( Map.of(
                Set.of("Stop1", "Stop2"), new BigDecimal("3.25"),
                Set.of("Stop2", "Stop3"), new BigDecimal("5.50"),
                Set.of("Stop1", "Stop3"), new BigDecimal("7.30")
        )));
    }

    public void setFareRules(HashMap<Set<String>, BigDecimal> fareRules) {
        this.fareRules = fareRules;

        maxFares = new HashMap<>();
        // find the max fare for each stop so we can calculate the incomplete charge amount
        for (var entry: fareRules.entrySet()) {
            var stops = entry.getKey();
            var fare = entry.getValue();
            for (var stop: stops) {
                if (maxFares.containsKey(stop)) {
                    maxFares.put(stop, maxFares.get(stop).max(fare));
                } else {
                    maxFares.put(stop, fare);
                }
            }
        }
    }

    @Override
    public BigDecimal calculateChargeAmount(Tap tapOn, Tap tapOff, TripStatus status) {
        if (status == TripStatus.CANCELLED) {
            return BigDecimal.ZERO;
        } else if (status == TripStatus.INCOMPLETE) {
            return maxFares.getOrDefault(tapOn.getStopId(), BigDecimal.ZERO);
        }


        var currentSet = Set.of(tapOn.getStopId(), tapOff.getStopId());
        var fare = fareRules.get(currentSet);
        if (fare != null) {
            return fare;
        }

        // may not want to throw exception here, depends on discussions with the team
        throw new IllegalArgumentException("Fare not found for stops: " + currentSet);
    }
}
