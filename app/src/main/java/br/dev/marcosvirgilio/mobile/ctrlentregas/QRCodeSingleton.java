package br.dev.marcosvirgilio.mobile.ctrlentregas;

public class QRCodeSingleton {
    private static final QRCodeSingleton instance = new QRCodeSingleton();

    private String qrCode = "null";
    private QRCodeSingleton() {}

    public static QRCodeSingleton getInstance() {
        return instance;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
