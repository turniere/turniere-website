package de.dhbw.karlsruhe.turniere.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
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


    public static void generateQRCode(String code) {
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */
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
            BufferedImage logoImage = ImageIO.read(new File("../svenderboi_klein.png").toURI().toURL());

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
            File file = new File("/idkwhere/" + code + ".png");
            file.delete();
            ImageIO.write(combined, "png", file);
        } catch (WriterException e) {
            // android spezifisch - mach einfach nen System.out.print ^
            // LOG.error("WriterException occured", e);
        } catch (IOException e) {
            // LOG.error("IOException occured", e);
        }

        System.out.println("\n\nYou have successfully created QR Code.");
    }
}

