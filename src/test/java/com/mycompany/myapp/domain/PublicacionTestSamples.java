package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PublicacionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Publicacion getPublicacionSample1() {
        return new Publicacion().id(1L).titulo("titulo1");
    }

    public static Publicacion getPublicacionSample2() {
        return new Publicacion().id(2L).titulo("titulo2");
    }

    public static Publicacion getPublicacionRandomSampleGenerator() {
        return new Publicacion().id(longCount.incrementAndGet()).titulo(UUID.randomUUID().toString());
    }
}
