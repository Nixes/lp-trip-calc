package org.nixes;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class TripProcessor {
    private HashMap<Set<String>, BigDecimal> fareRules;

    public void setFareRules(HashMap<Set<String>, BigDecimal> fareRules) {
        this.fareRules = fareRules;
    }

    public TripProcessor() {
        fareRules = new HashMap<Set<String>, BigDecimal>( Map.of(
                Set.of("Stop1", "Stop2"), new BigDecimal("3.25"),
                Set.of("Stop2", "Stop3"), new BigDecimal("5.50"),
                Set.of("Stop1", "Stop3"), new BigDecimal("7.30")
        ));
    }
    private BigDecimal calculateChargeAmount(Tap tapOn, Tap tapOff) {
        var currentSet = Set.of(tapOn.getStopId(), tapOff.getStopId());
        var fare = fareRules.get(currentSet);
        if (fare != null) {
            return fare;
        }
        // assumes that the fare is 0 if the stops are not in the fareRules, may want to throw an exception instead
        return BigDecimal.ZERO;
    }

    private static boolean isTripCancelled(Tap tapOn, Tap tapOff) {
        return tapOn.getStopId().equals(tapOff.getStopId());
    }

    public ArrayList<Trip> processTrips(@NotNull List<Tap> taps) {
        var incompleteTrips = new HashMap<String, Tap>();
        var processedTrips = new ArrayList<Trip>();
        for (var tap: taps) {
            if (tap.getTapOn()) {
                incompleteTrips.put(tap.getPrimaryAccountNumber(), tap);
            } else {
                var previousTap = incompleteTrips.get(tap.getPrimaryAccountNumber());
                if (previousTap != null) {
                    incompleteTrips.remove(tap.getPrimaryAccountNumber());
                    var chargeAmount = calculateChargeAmount(previousTap, tap);
                    var tripStatus = isTripCancelled(previousTap, tap) ? TripStatus.CANCELLED : TripStatus.COMPLETE;
                    var tripDuration = Duration.between(previousTap.getDateTimeUTC(), tap.getDateTimeUTC()).toSeconds();

                    // create a new previousTap
                    var newTrip = new Trip(
                            previousTap.getDateTimeUTC(),
                            tap.getDateTimeUTC(),
                            tripDuration,
                            previousTap.getStopId(),
                            tap.getStopId(),
                            chargeAmount,
                            previousTap.getCompanyId(),
                            previousTap.getBusId(),
                            previousTap.getPrimaryAccountNumber(),
                            tripStatus
                    );
                    processedTrips.add(newTrip);
                }
            }
        }

        // add incomplete trips to the complete trips list
        for (var tap: incompleteTrips.values()) {
            var trip = new Trip(
                    tap.getDateTimeUTC(),
                    null,
                    0,
                    tap.getStopId(),
                    tap.getStopId(),
                    BigDecimal.ZERO,
                    tap.getCompanyId(),
                    tap.getBusId(),
                    tap.getPrimaryAccountNumber(),
                    TripStatus.INCOMPLETE
            );
            processedTrips.add(trip);
        }

        return processedTrips;
    }

}
