package org.nixes;

import java.io.FileNotFoundException;

public class TripProcessCommand {
    public static void main(String[] args) throws FileNotFoundException {
        var fileName = "taps.csv";
        var taps = DataHelper.loadTaps(fileName);
        for (var tap: taps) {
            System.out.println("Tap id: "+tap.getId());
        }
    }
}