package br.dev.marcosvirgilio.mobile.ctrlentregas.util;

public class Constantes {
    private static final String endPointConIdEstudantil = "/ctrlentregas/conqrcode.php";
    private static final String endPointConMatricula = "/ctrlentregas/conmatricula.php";
    private static final String endPointCadProtocolo = "/ctrlentregas/cadprotocolo.php";

    private static final String servidor = "http://marcosvirgilio.dev.br";

    public static String getEndPointConIdEstudantil() {return  endPointConIdEstudantil; }

    public static String getEndPointConMatricula (){
        return endPointConMatricula;
    }

    public static String getEndPointCadProtocolo() {
        return endPointCadProtocolo;
    }

    public static String getServidor() {
        return servidor;
    }
}
