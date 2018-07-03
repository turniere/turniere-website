package de.dhbw.karlsruhe.turniere.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Component
public class QRService {

    /**
     * Generates QR Codes with our Logo embedded named <given code>.png
     *
     * @param code The Match Code and resulting Name for the QR Code
     */
    public void generateQRCode(String code) {
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            bitMatrix = writer.encode("www.joke-powered.de/t/" + code, BarcodeFormat.QR_CODE, 1000, 1000, hints);

            MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);

            // Load QR image
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

            // Load logo image
            File logo = new ClassPathResource("static/images/qrlogo.png").getFile();
            BufferedImage logoImage = ImageIO.read(logo);

            // Calculate the delta height and width between QR code and logo
            int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
            int deltaWidth = qrImage.getWidth() - logoImage.getWidth();

            // Initialize combined image
            BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();

            // Write QR code to new image at position 0/0
            g.drawImage(qrImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Write logo into combine image at position (deltaWidth / 2) and
            // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
            // the same space for the logo to be centered
            g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

            // Write combined image as PNG to OutputStream
            File file = new File("webengineeringdhbw/src/main/resources/static/qrcodes/" + code + ".png");
            file.delete();
            ImageIO.write(combined, "png", file);
        } catch (WriterException e) {
            System.out.print(e);
        } catch (IOException e) {
            System.out.print(e);
        }

        System.out.println("\n\nYou have successfully created QR Code for Match " + code);
    }
}

