package ru.argentoz;

public final class Base64 {

    public static final byte[] BASE_64_ALPHABET;
    public static final char[] LOOK_UP_BASE_64_ALPHABET;

    static {
        BASE_64_ALPHABET = new byte[128];
        LOOK_UP_BASE_64_ALPHABET = new char[64];

        int i;
        for(i = 0; i < 128; ++i)
            Base64.BASE_64_ALPHABET[i] = -1;
        for(i = 90; i >= 65; --i)
            Base64.BASE_64_ALPHABET[i] = (byte)(i - 65);
        for(i = 122; i >= 97; --i)
            Base64.BASE_64_ALPHABET[i] = (byte)(i - 97 + 26);
        for(i = 57; i >= 48; --i)
            Base64.BASE_64_ALPHABET[i] = (byte)(i - 48 + 52);

        Base64.BASE_64_ALPHABET[43] = 62;
        Base64.BASE_64_ALPHABET[47] = 63;

        for(i = 0; i <= 25; ++i)
            Base64.LOOK_UP_BASE_64_ALPHABET[i] = (char)(65 + i);

        i = 26;
        int j = 0;

        while(i <= 51) {
            Base64.LOOK_UP_BASE_64_ALPHABET[i] = (char)(97 + j);
            ++i;
            ++j;
        }

        i = 52;
        j = 0;

        while(i <= 61) {
            Base64.LOOK_UP_BASE_64_ALPHABET[i] = (char)(48 + j);
            ++i;
            ++j;
        }

        Base64.LOOK_UP_BASE_64_ALPHABET[62] = 43;
        Base64.LOOK_UP_BASE_64_ALPHABET[63] = 47;
    }

    public static String encode(byte[] binaryData) {
        byte val1;
        if(binaryData == null) return null;

        int lengthDataBits = binaryData.length * 8;
        if(lengthDataBits == 0) return "";

        int fewerThan24bits = lengthDataBits % 24,
                numberTriplets = lengthDataBits / 24,
                numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1 : numberTriplets,
                encodedIndex = 0, dataIndex = 0;
        char[] encodedData = new char[numberQuartet * 4];
        byte k, l, b1, b2, b3;

        for(int i = 0; i < numberTriplets; ++i) {
            b1 = binaryData[dataIndex++];
            b2 = binaryData[dataIndex++];
            b3 = binaryData[dataIndex++];

            l = (byte)(b2 & 0xF);
            k = (byte)(b1 & 3);

            byte val12 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0),
                    val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0),
                    val3 = (b3 & 0xFFFFFF80) == 0 ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xFC);

            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[val12];
            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[val2 | k << 4];
            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[l << 2 | val3];
            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[b3 & 0x3F];
        }

        if(fewerThan24bits == 8) {
            b1 = binaryData[dataIndex];
            k = (byte)(b1 & 3);
            val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);

            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[val1];
            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[k << 4];
            encodedData[encodedIndex++] = 61;
            encodedData[encodedIndex++] = 61;
        } else if(fewerThan24bits == 16) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];

            l = (byte)(b2 & 0xF);
            k = (byte)(b1 & 3);
            val1 = (b1 & 0xFFFFFF80) == 0 ? (byte)(b1 >> 2) : (byte)(b1 >> 2 ^ 0xC0);

            byte val2 = (b2 & 0xFFFFFF80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xF0);

            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[val1];
            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[val2 | k << 4];
            encodedData[encodedIndex++] = LOOK_UP_BASE_64_ALPHABET[l << 2];
            encodedData[encodedIndex++] = 61;
        }

        return new String(encodedData);
    }

}