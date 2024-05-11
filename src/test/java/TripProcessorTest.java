import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import org.nixes.*;

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

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
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
    public void testProcessTripsSingleTripDescOrder() {
        var taps = new ArrayList<Tap>();
        var tap1 = new Tap("2", LocalDateTime.parse("01-01-2020 10:05:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop2", "Company1", "Bus1", "PAN1");
        var tap2 = new Tap("1", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus1", "PAN1");
        taps.add(tap1);
        taps.add(tap2);

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
        var trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        var trip = trips.get(0);
        assertEquals(tap2.getDateTimeUTC(), trip.getStartedOnUTC());
        assertEquals(tap1.getDateTimeUTC(), trip.getFinishedOnUTC());
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

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
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
    public void testDoubleTapOn() {
        var taps = new ArrayList<Tap>();
        var tap1 = new Tap("1", LocalDateTime.parse("01-01-2020 10:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus1", "PAN1");
        var tap2 = new Tap("2", LocalDateTime.parse("01-01-2020 10:05:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop2", "Company1", "Bus1", "PAN1");
        taps.add(tap1);
        taps.add(tap2);

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
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
        assertEquals("Bus1", trip.getBusID());
        assertEquals("PAN1", trip.getPrimaryAccountNumber());
        assertEquals(TripStatus.INCOMPLETE, trip.getStatus());

        trip = trips.get(1);
        assertEquals(tap2.getDateTimeUTC(), trip.getStartedOnUTC());
        assertEquals(tap2.getDateTimeUTC(), trip.getFinishedOnUTC());
        assertEquals(0, trip.getDurationSecs());
        assertEquals("Stop2", trip.getFromStopId());
        assertEquals("Stop2", trip.getToStopId());
        assertEquals(new BigDecimal("5.50"), trip.getChargeAmount());
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

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
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

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
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

    /**
     * Custom assertion method to assert that all properties of two trips are equal.
     */
    private static void assertTripEquals(Trip expected, Trip actual) {
        assertEquals(expected.getStartedOnUTC(), actual.getStartedOnUTC());
        assertEquals(expected.getFinishedOnUTC(), actual.getFinishedOnUTC());
        assertEquals(expected.getDurationSecs(), actual.getDurationSecs());
        assertEquals(expected.getFromStopId(), actual.getFromStopId());
        assertEquals(expected.getToStopId(), actual.getToStopId());
        assertEquals(expected.getChargeAmount(), actual.getChargeAmount());
        assertEquals(expected.getCompanyId(), actual.getCompanyId());
        assertEquals(expected.getBusID(), actual.getBusID());
        assertEquals(expected.getPrimaryAccountNumber(), actual.getPrimaryAccountNumber());
        assertEquals(expected.getStatus(), actual.getStatus());
    }

    @Test
    public void testCompleteSampleInput() {
        var taps = new ArrayList<Tap>();
        taps.add(new Tap("1", LocalDateTime.parse("22-01-2023 13:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus37", "5500005555555559"));
        taps.add(new Tap("2", LocalDateTime.parse("22-01-2023 13:05:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop2", "Company1", "Bus37", "5500005555555559"));
        taps.add(new Tap("3", LocalDateTime.parse("22-01-2023 09:20:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop3", "Company1", "Bus36", "4111111111111111"));
        taps.add(new Tap("4", LocalDateTime.parse("23-01-2023 08:00:00", DataHelper.DATE_TIME_FORMATTER), true, "Stop1", "Company1", "Bus37", "4111111111111111"));
        taps.add(new Tap("5", LocalDateTime.parse("23-01-2023 08:02:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop1", "Company1", "Bus37", "4111111111111111"));
        taps.add(new Tap("6", LocalDateTime.parse("24-01-2023 16:30:00", DataHelper.DATE_TIME_FORMATTER), false, "Stop2", "Company1", "Bus37", "5500005555555559"));

        var expectedTrips = new ArrayList<Trip>();
        expectedTrips.add(new Trip(LocalDateTime.parse("22-01-2023 09:20:00", DataHelper.DATE_TIME_FORMATTER), LocalDateTime.parse("22-01-2023 09:20:00", DataHelper.DATE_TIME_FORMATTER), 0, "Stop3", "Stop3", new BigDecimal("7.30"), "Company1", "Bus36", "4111111111111111", TripStatus.INCOMPLETE));
        expectedTrips.add(new Trip(LocalDateTime.parse("22-01-2023 13:00:00", DataHelper.DATE_TIME_FORMATTER), LocalDateTime.parse("22-01-2023 13:05:00", DataHelper.DATE_TIME_FORMATTER), 300, "Stop1", "Stop2", new BigDecimal("3.25"), "Company1", "Bus37", "5500005555555559", TripStatus.COMPLETED));
        expectedTrips.add(new Trip(LocalDateTime.parse("23-01-2023 08:00:00", DataHelper.DATE_TIME_FORMATTER), LocalDateTime.parse("23-01-2023 08:02:00", DataHelper.DATE_TIME_FORMATTER), 120, "Stop1", "Stop1", BigDecimal.ZERO, "Company1", "Bus37", "4111111111111111", TripStatus.CANCELLED));
        expectedTrips.add(new Trip(LocalDateTime.parse("24-01-2023 16:30:00", DataHelper.DATE_TIME_FORMATTER), LocalDateTime.parse("24-01-2023 16:30:00", DataHelper.DATE_TIME_FORMATTER), 0, "Stop2", "Stop2", new BigDecimal("5.50"), "Company1", "Bus37", "5500005555555559", TripStatus.INCOMPLETE));

        Injector injector = Guice.createInjector(new BasicModule());
        var tripProcessor = injector.getInstance(TripProcessor.class);
        var trips = tripProcessor.processTrips(taps);

        assertEquals(4, trips.size());
        for (int i = 0; i < expectedTrips.size(); i++) {
            assertTripEquals(expectedTrips.get(i), trips.get(i));
        }
    }
}
