package org.nixes;

import java.io.IOException;

public class TripProcessCommand {
    public static void main(String[] args) throws IOException {
        var fileName = "taps.csv";
        var outputFileName = "trips.csv";
        System.out.println("Processing trips from file: " + fileName);
        var taps = DataHelper.loadTaps(fileName);
        // would use a dependency injection framework to inject the TripProcessor in a bigger application context
        var tripProcessor = new TripProcessor();
        var trips = tripProcessor.processTrips(taps);
        System.out.println("Saving trips to file: " + outputFileName);
        DataHelper.saveTripsCsv(trips, outputFileName);
    }
}