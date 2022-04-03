package com.st4s1k.leagueteamcomp.utils;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class CompressionUtils {

    public static String compressB64(String text) {
        return new String(Base64.getEncoder().encode(compress(text)));
    }

    public static String decompressB64(String b64Compressed) {
        byte[] decompressedBArray = decompress(Base64.getDecoder().decode(b64Compressed));
        return new String(decompressedBArray, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public static byte[] compress(String text) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
            dos.write(text.getBytes());
        }
        return os.toByteArray();
    }

    @SneakyThrows
    public static byte[] decompress(byte[] compressedTxt) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (OutputStream ios = new InflaterOutputStream(os)) {
            ios.write(compressedTxt);
        }
        return os.toByteArray();
    }

}
