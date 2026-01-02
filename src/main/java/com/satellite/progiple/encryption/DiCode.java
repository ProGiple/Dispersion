package com.satellite.progiple.encryption;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class DiCode implements ICoder {
    private static final int[][] ranges = {
            {10, 32}, {0, 3}, {0, 2}, {0, 3}, {1, 6}, {0, 3}, {5, 127}
    };

    private final byte[] seed = new byte[7];
    public DiCode(String seed) {
        this.seed[6] = Byte.parseByte(seed.substring(0, 2));
        for (int i = 5; i > 0; i--) this.seed[i] = (byte) Character.getNumericValue(seed.charAt(2 + (5 - i)));
        this.seed[0] = Byte.parseByte(seed.substring(7));
    }

    @Override
    public String encode(String value) {
        byte[] input = value.getBytes(StandardCharsets.UTF_8);
        byte[] transformed = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            int c = input[i];

            // seed[6] → увеличить символ
            c += seed[6];

            // seed[5] → уменьшить символ
            c -= seed[5];

            // seed[4] → сдвиг влево текущего символа
            c <<= seed[4];

            // seed[1] → сдвиг вправо каждого символа
            c >>= seed[1];

            // seed[2] → XOR
            c ^= seed[2];

            // seed[3] → глобальный сдвиг вправо всего текста
            c = (c >> seed[3]);

            // seed[0] → ограничение диапазона
            c = 32 + (c % seed[0]);

            transformed[i] = (byte) c;
        }

        return Base64.getEncoder().encodeToString(transformed);
    }

    @Override
    public String decode(String value) {
        byte[] decoded = Base64.getDecoder().decode(value);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public static String gen() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        StringBuilder sb = new StringBuilder();
        for (int[] r : ranges) {
            sb.append(random.nextInt(r[0], r[1]));
        }
        return sb.toString();
    }
}
