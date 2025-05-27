package com.farniks.qrgeneratorscanner;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class QrGeneratorScanner {

    private static JTextField urlFild;
    private static JLabel qrCodeLabel;
    private static BufferedImage qrCodeImage;

    private static void generateQRCode() {
        String url = urlFild.getText();
        if(url.isEmpty()){// Mensaje si no hay texto en el generador de QR
            JOptionPane.showMessageDialog(null,"Por favor, ingresa una URL");
            return;
        }

        try {//Trycatch Para generar el codigo QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // Se elige el formato de crearcion de codigo en este caso es QR
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE,300,300);

            qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);// La imagen creada en el formato QR se guarda en una imagen

            ImageIcon qrIcon = new ImageIcon(qrCodeImage);// Se guarda la imagen creada en QR code para enviar al label
            qrCodeLabel.setIcon(qrIcon);

        } catch (WriterException e) {// Si falla la eneracion del QR
            JOptionPane.showMessageDialog(null,"Error al crear el codigo QR");
            e.printStackTrace();
        }

    }
    private static void exportCode() {
        if(qrCodeImage == null){// Sale mensaje si le da al boton de exportar QR y no hay QR
            JOptionPane.showMessageDialog(null,"Primero genera un QR");
            return;
        }
        //Esto es para seleccionar donde vamos a guardar el QR
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guarda codigo QR");// Titulo de la ventana
        fileChooser.setSelectedFile(new File("CodigoQR.png"));//Es el nombre predeterminado para guardar el QR en formato .png

        int userSelection = fileChooser.showSaveDialog(null);

        if(userSelection == JFileChooser.APPROVE_OPTION){// Si le da clic en afectar guarda el archivo con ese nombre
            File fileToSave = fileChooser.getSelectedFile();

            try {
                ImageIO.write(qrCodeImage, "PNG",fileToSave);// Para guardar la imagen tomando encuenta fileToSave
                JOptionPane.showMessageDialog(null,"Codigo QR, Exportado correctamente");

            }catch (IOException e) {
                JOptionPane.showMessageDialog(null,"Error al exportar el codigo QR");
                e.printStackTrace();
            }
        }
    }

    private void scanQRCodeFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecciona una imagen PNG con código QR");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                Result qrResult = new MultiFormatReader().decode(bitmap);
                String decodedText = qrResult.getText();
                JOptionPane.showMessageDialog(null, "Contenido del QR: " + decodedText);
                urlFild.setText(decodedText);
                qrCodeLabel.setIcon(new ImageIcon(bufferedImage)); // Mostrar el QR leído

            } catch (IOException | NotFoundException e) {
                JOptionPane.showMessageDialog(null, "No se pudo leer el código QR de la imagen");
                e.printStackTrace();
            }
        }
    }


    public void generateQRCode(ActionEvent actionEvent) {
     
    }

    public void goLink(ActionEvent actionEvent) {

    }

    public void exportCode(ActionEvent actionEvent) {

    }

    public void scanQRCodeFromFileAction(ActionEvent actionEvent) {

    }
}
