package org.nixes;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.nixes.Service.ITripProcessor;
import org.nixes.Service.TapLoader;
import org.nixes.Service.TripSaver;

import java.io.IOException;

/**
 * This class is the program that is executed to process taps from a CSV file into trips and save them to another CSV file.
 */
public class TripProcessCommand {
    public static void main(String[] args) throws IOException {
        var fileName = "taps.csv";
        var outputFileName = "trips.csv";
        System.out.println("Processing trips from file: " + fileName);
        Injector injector = Guice.createInjector(new BasicModule());
        var tapLoader = injector.getInstance(TapLoader.class);
        var taps = tapLoader.loadTaps(fileName);
        var tripProcessor = injector.getInstance(ITripProcessor.class);
        var trips = tripProcessor.processTrips(taps);

        System.out.println("Saving trips to file: " + outputFileName);
        var tripSaver = injector.getInstance(TripSaver.class);
        tripSaver.saveTripsCsv(trips, outputFileName);
    }
}