package com.farniks.qrgeneratorscanner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;


public class QrGeneratorScanner {

    public TextField linkTextField;
    public ImageView qrIcon;
    public Label contentQRLabel;;
    public Button generatorQRButton, scanerQRButton, exportQRButton, goLinkButton, copyLinkButton;

    private static BufferedImage qrCodeImage;

    public void generateQRCode(ActionEvent actionEvent) {
        String url = linkTextField.getText();
        if(url.isEmpty()){// Mensaje si no hay texto en el generador de QR
            showAlert(Alert.AlertType.WARNING, "Sin contenido", "Por favor, ingresa una URL");
            return;
        }

        try {//Trycatch Para generar el codigo QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // Se elige el formato de crearcion de codigo en este caso es QR
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,300,300);

            qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);// La imagen creada en el formato QR se guarda en una imagen

            Image fxImage = convertToFxImage(qrCodeImage);

            qrIcon.setImage(fxImage);

        } catch (WriterException e) {// Si falla la eneracion del QR
            showAlert(Alert.AlertType.ERROR, "Error", "Error al crear el codigo QR");
            e.printStackTrace();
        }

    }

    private Image convertToFxImage(BufferedImage qrCodeImage) {
        // Creamos un flujo de salida en memoria para guardar temporalmente los bytes de la imagen PNG
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            // Escribimos la imagen BufferedImage en el flujo de salida, en formato PNG
            ImageIO.write(qrCodeImage, "png", os);

            // Creamos un flujo de entrada con los datos en memoria (los bytes de la imagen)
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

            // Creamos y devolvemos una imagen JavaFX utilizando el flujo de entrada
            return new Image(is);

        } catch (IOException e) {
            // Si ocurre un error al escribir o leer la imagen, lo mostramos en consola
            e.printStackTrace();
            // Y devolvemos null indicando que la conversión falló
            return null;
        }
    }


    public void exportCode(ActionEvent actionEvent) {
        if (qrCodeImage == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Primero genera un QR");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar código QR");
        fileChooser.setInitialFileName("CodigoQR.png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG", "*.png"));

        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            try {
                ImageIO.write(qrCodeImage, "PNG", file);
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Código QR exportado correctamente");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo guardar el archivo");
                e.printStackTrace();
            }
        }
    }

    public void scanQRCodeFromFileAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona una imagen PNG con código QR");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG", "*.png"));

        File file = fileChooser.showOpenDialog(getStage());

        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result qrResult = new MultiFormatReader().decode(bitmap);

                String decodedText = qrResult.getText();
                contentQRLabel.setText(decodedText);
                linkTextField.setText(decodedText);
                qrIcon.setImage(convertToFxImage(bufferedImage));

            } catch (IOException | NotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo leer el código QR");
                e.printStackTrace();
            }
        }
    }


    public void goLink(ActionEvent actionEvent) {
        String content = contentQRLabel.getText().trim();

        // Verifica si el contenido comienza con "http://" o "https://"
        if (!content.startsWith("http://") && !content.startsWith("https://")) {
            showAlert(Alert.AlertType.WARNING, "Enlace inválido", "El contenido no es un enlace válido.");
            return;
        }

        // Intenta abrir el enlace en el navegador
        try {
            Desktop.getDesktop().browse(new URI(content));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir el enlace.");
            e.printStackTrace();
        }
    }

    private Stage getStage() {
        return (Stage) linkTextField.getScene().getWindow();
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void copyLink(ActionEvent actionEvent) {
        String textToCopy = contentQRLabel.getText();

        if (textToCopy == null || textToCopy.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Sin contenido", "No hay contenido para copiar.");
            return;
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(textToCopy);
        clipboard.setContent(content);

        showAlert(Alert.AlertType.INFORMATION, "Copiado", "Contenido copiado al portapapeles.");
    }
}
