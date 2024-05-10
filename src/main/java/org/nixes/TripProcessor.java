package org.nixes;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class TripProcessor {
    private HashMap<Set<String>, BigDecimal> fareRules = new HashMap<>();
    private HashMap<String, BigDecimal> maxFares = new HashMap<>();

    public void setFareRules(HashMap<Set<String>, BigDecimal> fareRules) {
        this.fareRules = fareRules;
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

    public TripProcessor() {
        this.setFareRules(new HashMap<Set<String>, BigDecimal>( Map.of(
                Set.of("Stop1", "Stop2"), new BigDecimal("3.25"),
                Set.of("Stop2", "Stop3"), new BigDecimal("5.50"),
                Set.of("Stop1", "Stop3"), new BigDecimal("7.30")
        )));
    }

    private BigDecimal calculateIncompleteChargeAmount(Tap tap) {
        return maxFares.getOrDefault(tap.getStopId(), BigDecimal.ZERO);
    }

    private BigDecimal calculateChargeAmount(Tap tapOn, Tap tapOff) {
        if (isTripCancelled(tapOn, tapOff)) {
            return BigDecimal.ZERO;
        }

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
                var tapOn = incompleteTrips.get(tap.getPrimaryAccountNumber());
                if (tapOn != null) {
                    incompleteTrips.remove(tap.getPrimaryAccountNumber());
                    var chargeAmount = calculateChargeAmount(tapOn, tap);
                    var tripStatus = isTripCancelled(tapOn, tap) ? TripStatus.CANCELLED : TripStatus.COMPLETE;
                    var tripDuration = Duration.between(tapOn.getDateTimeUTC(), tap.getDateTimeUTC()).toSeconds();

                    // create a new tapOn
                    var newTrip = new Trip(
                            tapOn.getDateTimeUTC(),
                            tap.getDateTimeUTC(),
                            tripDuration,
                            tapOn.getStopId(),
                            tap.getStopId(),
                            chargeAmount,
                            tapOn.getCompanyId(),
                            tapOn.getBusId(),
                            tapOn.getPrimaryAccountNumber(),
                            tripStatus
                    );
                    processedTrips.add(newTrip);
                }
            }
        }

        // add any remaining incomplete trips to the processed trips list
        for (var tap: incompleteTrips.values()) {
            var trip = new Trip(
                    tap.getDateTimeUTC(),
                    tap.getDateTimeUTC(),
                    0,
                    tap.getStopId(),
                    tap.getStopId(),
                    calculateIncompleteChargeAmount(tap),
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
