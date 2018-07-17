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
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

@Component
public class QRService {

    /**
     * Generate qr code for specified tournament code
     *
     * @param code Tournament code to generate qr code for
     * @return Base64 encoded qr code with logo
     */
    public byte[] generateQRCode(String code) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // initialize BitMatrix with qrcode
        BitMatrix bitMatrix = qrCodeWriter.encode("dev.turnie.re/t/" + code, BarcodeFormat.QR_CODE, 500, 500, hints);
        MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
        // convert qrcode BitMatrix to BufferedImage
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
        // load logo into BufferedImage
        InputStream logoInputStream = new ClassPathResource("static/images/qrlogo.png").getInputStream();
        BufferedImage logoImage = ImageIO.read(logoInputStream);
        // calculate the delta height and width between QR code and logo
        int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
        int deltaWidth = qrImage.getWidth() - logoImage.getWidth();
        // initialize combined image
        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();
        // write QR code to new image at position 0/0
        g.drawImage(qrImage, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        // write logo into combine image at position (deltaWidth / 2) and
        // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
        // the same space for the logo to be centered
        g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
        // write image to ByteArrayOutputStream wrapped by base64 encoder
        ImageIO.write(combined, "png", Base64.getEncoder().wrap(byteArrayOutputStream));
        // return base64 string
        return byteArrayOutputStream.toByteArray();
    }
}

