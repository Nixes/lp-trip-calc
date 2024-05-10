package org.nixes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TripProcessCommand {
    public static void main(String[] args) throws IOException {
        var fileName = "taps.csv";
        var taps = DataHelper.loadTaps(fileName);
        for (var tap : taps) {
            System.out.println("Tap id: " + tap.getId());
        }

        var trips = new ArrayList<Trip>();
        var testTrip = new Trip(
                LocalDateTime.now(),
                LocalDateTime.now(),
                100,
                "Stop1",
                "Stop2",
                new BigDecimal("3.25"),
                "Company1",
                "Bus37",
                "TESTPAN",
                TripStatus.COMPLETE
        );
        trips.add(testTrip);
        DataHelper.saveTripsCsv(trips, "trips.csv");
    }
}