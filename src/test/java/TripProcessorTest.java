import org.junit.jupiter.api.Test;
import org.nixes.DataHelper;
import org.nixes.Tap;
import org.nixes.TripProcessor;
import org.nixes.TripStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TripProcessorTest {
    @Test
    public void testProcessTripsSingleTrip() {
        var taps = new ArrayList<Tap>();
        var tap1 = new Tap("1", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus1", "PAN1");
        var tap2 = new Tap("2", LocalDateTime.parse("01-01-2020 10:05:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop2", "Company1", "Bus1", "PAN1");
        taps.add(tap1);
        taps.add(tap2);

        var tripProcessor = new TripProcessor();
        var trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        var trip = trips.get(0);
        assertEquals(tap1.getDateTimeUTC(), trip.getStartedOnUTC());
        assertEquals(tap2.getDateTimeUTC(), trip.getFinishedOnUTC());
        assertEquals(300, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop2", trip.getToStopId());
        assertEquals(new BigDecimal("3.25"), trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals("Bus1", trip.getBusID());
        assertEquals("PAN1", trip.getPrimaryAccountNumber());
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
    }

    @Test
    public void testTapOffOnly() {
        var taps = new ArrayList<Tap>();
        var tap1 = new Tap("1", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop1", "Company1", "Bus1", "PAN1");
        taps.add(tap1);

        var tripProcessor = new TripProcessor();
        var trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        var trip = trips.get(0);
        assertEquals(tap1.getDateTimeUTC(), trip.getStartedOnUTC());
        assertEquals(tap1.getDateTimeUTC(), trip.getFinishedOnUTC());
        assertEquals(0, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop1", trip.getToStopId());
        assertEquals(new BigDecimal("7.30"), trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals("Bus1", trip.getBusID());
        assertEquals("PAN1", trip.getPrimaryAccountNumber());
        assertEquals(TripStatus.INCOMPLETE, trip.getStatus());
    }

    @Test
    public void testCancelledTrip() {
        var taps = new ArrayList<Tap>();
        var tap1 = new Tap("1", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus1", "PAN1");
        // same PAN and tap off at the same stop
        var tap2 = new Tap("2", LocalDateTime.parse("01-01-2020 10:05:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop1", "Company1", "Bus1", "PAN1");
        taps.add(tap1);
        taps.add(tap2);

        var tripProcessor = new TripProcessor();
        var trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        var trip = trips.get(0);
        assertEquals(tap1.getDateTimeUTC(), trip.getStartedOnUTC());
        assertEquals(tap2.getDateTimeUTC(), trip.getFinishedOnUTC());
        assertEquals(300, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop1", trip.getToStopId());
        assertEquals(BigDecimal.ZERO, trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
    }
    @Test
    public void testIncompleteTrip() {
        var taps = new ArrayList<Tap>();
        var tap1 = new Tap("1", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus1", "PAN1");
        // different PAN should not count as a tap off
        var tap2 = new Tap("2", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus1", "PAN2");
        taps.add(tap1);
        taps.add(tap2);

        var tripProcessor = new TripProcessor();
        var trips = tripProcessor.processTrips(taps);

        assertEquals(2, trips.size());
        var trip = trips.get(0);
        assertEquals(tap1.getDateTimeUTC(), trip.getStartedOnUTC());
        assertEquals(tap1.getDateTimeUTC(), trip.getFinishedOnUTC());
        assertEquals(0, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop1", trip.getToStopId());
        assertEquals(new BigDecimal("7.30"), trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals(TripStatus.INCOMPLETE, trip.getStatus());
    }
}
