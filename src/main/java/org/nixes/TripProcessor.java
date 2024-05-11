package org.nixes;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class TripProcessor {
    private final TripPriceCalculator tripPriceCalculator;

    @Inject
    public TripProcessor(TripPriceCalculator tripPriceCalculator) {
        this.tripPriceCalculator = tripPriceCalculator;
    }


    protected static boolean isTripCancelled(Tap tapOn, Tap tapOff) {
        return tapOn.getStopId().equals(tapOff.getStopId());
    }

    protected Trip createIncompleteTrip(Tap tap) {
        var status = TripStatus.INCOMPLETE;
        return new Trip(
                tap.getDateTimeUTC(),
                tap.getDateTimeUTC(),
                0,
                tap.getStopId(),
                tap.getStopId(),
                this.tripPriceCalculator.calculateChargeAmount(tap, tap, status),
                tap.getCompanyId(),
                tap.getBusId(),
                tap.getPrimaryAccountNumber(),
                status
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
            if (tap.getIsTapOn()) {
                // check if there is already a tapOn for this account number
                var existingTap = incompleteTrips.get(tap.getPrimaryAccountNumber());
                if (existingTap != null) {
                    // if there is, then this tapOn is uncompletable
                    uncompletableTrips.add(existingTap);
                }

                incompleteTrips.put(tap.getPrimaryAccountNumber(), tap);
            } else {
                var tapOn = incompleteTrips.get(tap.getPrimaryAccountNumber());
                if (tapOn != null) {
                    incompleteTrips.remove(tap.getPrimaryAccountNumber());
                    var tripStatus = isTripCancelled(tapOn, tap) ? TripStatus.CANCELLED : TripStatus.COMPLETED;
                    var chargeAmount = this.tripPriceCalculator.calculateChargeAmount(tapOn, tap, tripStatus);
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

        processedTrips.sort(Comparator.comparing(Trip::getStartedOnUTC));

        return processedTrips;
    }

}
