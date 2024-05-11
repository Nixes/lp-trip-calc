package org.nixes;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

public class TripProcessCommand {
    public static void main(String[] args) throws IOException {
        var fileName = "taps.csv";
        var outputFileName = "trips.csv";
        System.out.println("Processing trips from file: " + fileName);
        var taps = DataHelper.loadTaps(fileName);
        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
        var trips = tripProcessor.processTrips(taps);






        System.out.println("Saving trips to file: " + outputFileName);
        DataHelper.saveTripsCsv(trips, outputFileName);
    }
}