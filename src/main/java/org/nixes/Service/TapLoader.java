package org.nixes.Service;

import org.nixes.Model.Tap;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class is responsible for loading the taps from a CSV file.
 */
public class TapLoader {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public List<Tap> loadTaps(String fileName) throws FileNotFoundException {
        var file = new File(fileName);
        var scanner = new Scanner(file);
        var taps = new ArrayList<Tap>();

        var firstLine = true;
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            // skip header
            if (firstLine) {
                firstLine = false;
                continue;
            }
            var parts = Arrays.stream(line.split(",")).map(String::trim).toArray(String[]::new);
            // columns are ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN
            var tap = new Tap(parts[0], LocalDateTime.parse(parts[1], DATE_TIME_FORMATTER), parts[2].equals("ON"), parts[3], parts[4], parts[5], parts[6]);
            taps.add(tap);
        }

        scanner.close();
        return taps;
    }
}
