package com.zk.utools.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@Slf4j
public final class QRCodeUtil {

    private static final int WIDTH = 300;

    private static final int HEIGHT = 300;

    private static final String FORMAT = "png";

    private QRCodeUtil() {
    }

    public static String generateQRCodeWithBase64(String content) {
        return generateQRCodeWithBase64(content, WIDTH, HEIGHT);
    }

    public static String generateQRCodeWithBase64(String content, int width, int height) {
        StringBuilder imageEncodeStr = new StringBuilder("data:image/");
        imageEncodeStr.append(FORMAT).append(";base64,");
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ImageIO.write(bufferedImage, FORMAT, out);

            Base64 base64 = new Base64();
            imageEncodeStr.append(base64.encodeAsString(out.toByteArray()));
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                log.warn(e.getLocalizedMessage());
            }
        }
        return imageEncodeStr.toString();
    }

    public static void generateQRcodeToStream(String content, int width, int height, OutputStream outputStream) {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, outputStream);
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
        }
    }

    public static void generateQRcodeToStream(String content, OutputStream outputStream) {
        generateQRcodeToStream(content, WIDTH, HEIGHT, outputStream);
    }

}