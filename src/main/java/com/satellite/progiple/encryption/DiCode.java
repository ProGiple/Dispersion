package com.satellite.progiple.encryption;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public class DiCode implements ICoder {
    private static final int[][] ranges = {
            {10, 32}, {0, 9}, {0, 2}, {0, 3}, {1, 6}, {0, 3}, {1, 7}, {5, 127}
    };

    private final byte[] seed = new byte[8];
    public DiCode(String seed) {
        this.seed[7] = Byte.parseByte(seed.substring(0, 2));
        int length = this.seed.length;
        for (int i = 2; i < length; i++) this.seed[length - i] = (byte) Character.getNumericValue(seed.charAt(i));
        this.seed[0] = Byte.parseByte(seed.substring(8));
        System.out.println(Arrays.toString(this.seed));
    }

    @Override
    public String encode(String value) {
        for (byte op = 0; op < seed[1]; op++) {
            byte[] input = value.getBytes(StandardCharsets.UTF_8);
            byte[] transformed = new byte[input.length];

            for (int i = 0; i < input.length; i++) {
                int c = input[i];

                // seed[7] → увеличить символ
                c += seed[7];

                // seed[6] → уменьшить символ
                c -= seed[6];

                // seed[5] → сдвиг влево текущего символа
                c <<= seed[5];

                // seed[2] → сдвиг вправо каждого символа
                c >>= seed[2];

                // seed[3] → XOR
                c ^= seed[3];

                // seed[4] → глобальный сдвиг вправо всего текста
                c = (c >> seed[4]);

                // seed[0] → ограничение диапазона
                c = 32 + (c % seed[0]);

                transformed[i] = (byte) c;
            }

            value = Base64.getEncoder().encodeToString(transformed);
        }

        return value;
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
