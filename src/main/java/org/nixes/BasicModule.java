package org.nixes;

import com.google.inject.AbstractModule;
import org.nixes.Service.*;

/**
 * Configures the dependency injection bindings for the application. Using Google Guice.
 */
public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ITripPriceCalculator.class).to(TripPriceCalculator.class);
        bind(ITripProcessor.class).to(TripProcessor.class);
        bind(TripSaver.class);
        bind(TapLoader.class);
    }
}
