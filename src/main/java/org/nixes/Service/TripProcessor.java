package org.nixes.Service;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.nixes.Model.Tap;
import org.nixes.Model.Trip;
import org.nixes.Enum.TripStatus;

import java.time.Duration;
import java.util.*;

/**
 * This class processes a list of taps and generates a list of trips.
 */
public class TripProcessor implements ITripProcessor {
    private final ITripPriceCalculator tripPriceCalculator;

    @Inject
    public TripProcessor(ITripPriceCalculator tripPriceCalculator) {
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
        var taps = sortTaps(inputTaps);
        var incompleteTrips = new HashMap<String, Tap>();
        var uncompletableTrips = new LinkedList<Tap>();
        var processedTrips = new LinkedList<Trip>();

        for (var tap: taps) {
            processTap(tap, incompleteTrips, uncompletableTrips, processedTrips);
        }

        addRemainingIncompleteTrips(incompleteTrips, processedTrips);
        addUncompletableTrips(uncompletableTrips, processedTrips);

        processedTrips.sort(Comparator.comparing(Trip::getStartedOnUTC));

        return processedTrips;
    }

    private ArrayList<Tap> sortTaps(List<Tap> inputTaps) {
        var taps = new ArrayList<>(inputTaps);
        taps.sort(Comparator.comparing(Tap::getDateTimeUTC));
        return taps;
    }

    private void processTap(Tap tap, HashMap<String, Tap> incompleteTrips, LinkedList<Tap> uncompletableTrips, LinkedList<Trip> processedTrips) {
        if (tap.getIsTapOn()) {
            processTapOn(tap, incompleteTrips, uncompletableTrips);
        } else {
            processTapOff(tap, incompleteTrips, uncompletableTrips, processedTrips);
        }
    }

    private void processTapOn(Tap tap, HashMap<String, Tap> incompleteTrips, LinkedList<Tap> uncompletableTrips) {
        var existingTap = incompleteTrips.get(tap.getPrimaryAccountNumber());
        if (existingTap != null) {
            uncompletableTrips.add(existingTap);
        }

        incompleteTrips.put(tap.getPrimaryAccountNumber(), tap);
    }

    private void processTapOff(Tap tap, HashMap<String, Tap> incompleteTrips, LinkedList<Tap> uncompletableTrips, LinkedList<Trip> processedTrips) {
        var tapOn = incompleteTrips.get(tap.getPrimaryAccountNumber());
        if (tapOn != null) {
            incompleteTrips.remove(tap.getPrimaryAccountNumber());
            var tripStatus = isTripCancelled(tapOn, tap) ? TripStatus.CANCELLED : TripStatus.COMPLETED;
            var chargeAmount = this.tripPriceCalculator.calculateChargeAmount(tapOn, tap, tripStatus);
            var tripDuration = Duration.between(tapOn.getDateTimeUTC(), tap.getDateTimeUTC()).toSeconds();

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

    private void addRemainingIncompleteTrips(HashMap<String, Tap> incompleteTrips, LinkedList<Trip> processedTrips) {
        for (var tap: incompleteTrips.values()) {
            processedTrips.add(createIncompleteTrip(tap));
        }
    }

    private void addUncompletableTrips(LinkedList<Tap> uncompletableTrips, LinkedList<Trip> processedTrips) {
        for (var tap: uncompletableTrips) {
            processedTrips.add(createIncompleteTrip(tap));
        }
    }
}
