package org.nixes.Service;

import org.nixes.Model.Tap;
import org.nixes.Enum.TripStatus;

import java.math.BigDecimal;

public interface ITripPriceCalculator {
    BigDecimal calculateChargeAmount(Tap tapOn, Tap tapOff, TripStatus status);
}
