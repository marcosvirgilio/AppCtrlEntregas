package br.dev.marcosvirgilio.mobile.ctrlentregas;

import androidx.navigation.NavController;

public class QRCodeSingleton {
    private static final QRCodeSingleton instance = new QRCodeSingleton();

    private String qrCode = "null";
    private NavController navController = null;
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

    public NavController getNavController() {
        return navController;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }
}
