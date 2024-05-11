package org.nixes.Service;

import org.jetbrains.annotations.NotNull;
import org.nixes.Model.Tap;
import org.nixes.Model.Trip;

import java.util.List;

public interface ITripProcessor {

    List<Trip> processTrips(@NotNull List<Tap> inputTaps);
}
