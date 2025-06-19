package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ComentarioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Comentario getComentarioSample1() {
        return new Comentario().id(1L).texto("texto1");
    }

    public static Comentario getComentarioSample2() {
        return new Comentario().id(2L).texto("texto2");
    }

    public static Comentario getComentarioRandomSampleGenerator() {
        return new Comentario().id(longCount.incrementAndGet()).texto(UUID.randomUUID().toString());
    }
}
