package org.nixes;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class TripProcessor {
    private HashMap<Set<String>, BigDecimal> fareRules;
    private HashMap<String, BigDecimal> maxFares;

    public TripProcessor() {
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

    protected BigDecimal calculateIncompleteChargeAmount(Tap tap) {
        return maxFares.getOrDefault(tap.getStopId(), BigDecimal.ZERO);
    }

    protected BigDecimal calculateChargeAmount(Tap tapOn, Tap tapOff) {
        if (isTripCancelled(tapOn, tapOff)) {
            return BigDecimal.ZERO;
        }

        var currentSet = Set.of(tapOn.getStopId(), tapOff.getStopId());
        var fare = fareRules.get(currentSet);
        if (fare != null) {
            return fare;
        }

        // may not want to throw exception here, depends on discussions with the team
        throw new IllegalArgumentException("Fare not found for stops: " + currentSet);
    }

    protected static boolean isTripCancelled(Tap tapOn, Tap tapOff) {
        return tapOn.getStopId().equals(tapOff.getStopId());
    }

    protected Trip createIncompleteTrip(Tap tap) {
        return new Trip(
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
    }

    public List<Trip> processTrips(@NotNull List<Tap> inputTaps) {
        // make a copy of the input list just to be safe we don't modify the original when sorting
        var taps = new ArrayList<>(inputTaps);
        // sort the taps by time ascending
        taps.sort(Comparator.comparing(Tap::getDateTimeUTC));

        var incompleteTrips = new HashMap<String, Tap>();
        var uncompletableTrips = new LinkedList<Tap>();
        var processedTrips = new LinkedList<Trip>();
        for (var tap: taps) {
            if (tap.getTapOn()) {
                // check if there is already a tapOn for this account number
                var existingTap = incompleteTrips.get(tap.getPrimaryAccountNumber());
                if (existingTap != null) {
                    // if there is, then this tapOn is uncompletable
                    uncompletableTrips.add(tap);
                }
                incompleteTrips.put(tap.getPrimaryAccountNumber(), tap);
            } else {
                var tapOn = incompleteTrips.get(tap.getPrimaryAccountNumber());
                if (tapOn != null) {
                    incompleteTrips.remove(tap.getPrimaryAccountNumber());
                    var chargeAmount = calculateChargeAmount(tapOn, tap);
                    var tripStatus = isTripCancelled(tapOn, tap) ? TripStatus.CANCELLED : TripStatus.COMPLETED;
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
                } else {
                    uncompletableTrips.add(tap);
                }
            }
        }

        // add any remaining incomplete trips to the processed trips list
        for (var tap: incompleteTrips.values()) {
            processedTrips.add(createIncompleteTrip(tap));
        }

        for (var tap: uncompletableTrips) {
            processedTrips.add(createIncompleteTrip(tap));
        }

        return processedTrips;
    }

}
