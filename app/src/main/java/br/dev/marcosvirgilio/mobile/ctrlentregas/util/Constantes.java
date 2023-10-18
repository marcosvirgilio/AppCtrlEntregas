package br.dev.marcosvirgilio.mobile.ctrlentregas.util;

public class Constantes {

    /*
    private static final String endPointConIdEstudantil = "/ctrlentregas/conqrcode.php";
    private static final String endPointConMatricula = "/ctrlentregas/conmatricula.php";
    private static final String endPointCadProtocolo = "/ctrlentregas/cadprotocolo.php";
    private static final String endPointConProtocolosDia = "/ctrlentregas/conprotocolo.php";
    private static final String servidor = "http://marcosvirgilio.dev.br";


    */
    //Ambiente IFSC

    private static final String endPointConIdEstudantil = "/html/conqrcode.php";
    private static final String endPointConMatricula = "/html/conmatricula.php";
    private static final String endPointCadProtocolo = "/html/cadprotocolo.php";
    private static final String endPointConProtocolosDia = "/html/conprotocolo.php";
    private static final String servidor = "http://191.36.28.44";



    public static String getEndPointConIdEstudantil() {return  endPointConIdEstudantil; }

    public static String getEndPointConMatricula (){
        return endPointConMatricula;
    }

    public static String getEndPointConProtocolosDia() {return endPointConProtocolosDia; }
    public static String getEndPointCadProtocolo() {
        return endPointCadProtocolo;
    }

    public static String getServidor() {
        return servidor;
    }
}
