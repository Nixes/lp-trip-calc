package org.nixes.Service;

import org.nixes.Model.Trip;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for saving the trips to a CSV file.
 */
public class TripSaver {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void saveTripsCsv(List<Trip> trips, String fileName) throws IOException {
        // StartedOnUTC, FinishedOnUTC, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID,
        var header = "Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status";
        var lines = new ArrayList<String>();
        lines.add(header);
        for (var trip : trips) {
            // columns are StartedOnUTC, FinishedOnUTC, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN
            var line = String.format("%s, %s, %d, %s, %s, %s, %s, %s, %s, %s",
                    trip.getStartedOnUTC().format(DATE_TIME_FORMATTER),
                    trip.getFinishedOnUTC().format(DATE_TIME_FORMATTER),
                    trip.getDurationSecs(),
                    trip.getFromStopId(),
                    trip.getToStopId(),
                    trip.getChargeAmount().toString(),
                    trip.getCompanyId(),
                    trip.getBusID(),
                    trip.getPrimaryAccountNumber(),
                    trip.getStatus()
            );
            lines.add(line);
        }

        var file = new File(fileName);
        var writer = new java.io.FileWriter(file);
        for (var line : lines) {
            writer.write(line);
            writer.write("\n");
        }
        writer.close();
    }
}
