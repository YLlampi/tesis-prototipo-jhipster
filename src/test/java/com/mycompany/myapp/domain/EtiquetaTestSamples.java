package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EtiquetaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Etiqueta getEtiquetaSample1() {
        return new Etiqueta().id(1L).nombre("nombre1");
    }

    public static Etiqueta getEtiquetaSample2() {
        return new Etiqueta().id(2L).nombre("nombre2");
    }

    public static Etiqueta getEtiquetaRandomSampleGenerator() {
        return new Etiqueta().id(longCount.incrementAndGet()).nombre(UUID.randomUUID().toString());
    }
}
