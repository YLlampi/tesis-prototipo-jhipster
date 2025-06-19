package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AdjuntoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Adjunto getAdjuntoSample1() {
        return new Adjunto().id(1L).nombreArchivo("nombreArchivo1").tipoMime("tipoMime1");
    }

    public static Adjunto getAdjuntoSample2() {
        return new Adjunto().id(2L).nombreArchivo("nombreArchivo2").tipoMime("tipoMime2");
    }

    public static Adjunto getAdjuntoRandomSampleGenerator() {
        return new Adjunto()
            .id(longCount.incrementAndGet())
            .nombreArchivo(UUID.randomUUID().toString())
            .tipoMime(UUID.randomUUID().toString());
    }
}
