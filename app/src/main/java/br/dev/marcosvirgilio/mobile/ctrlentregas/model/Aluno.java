package br.dev.marcosvirgilio.mobile.ctrlentregas.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Aluno {
    private String matricula;
    private String qrcode;
    private String nome;

    //Metodo retorna o objeto com dados no formato JSON
    public JSONObject toJsonObject() {
        JSONObject json = new JSONObject();
        try {
            json.put("qrcode", this.qrcode);
            json.put("matricula", this.matricula);
            json.put("nome", this.nome);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }



    public Aluno (JSONObject jp) {
        try {
            this.setQrCode((String) jp.get("qrcode"));
            this.setMatricula((String) jp.get("matricula"));
            this.setNome((String) jp.get("nome"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setQrCode(String qrcode) {
        this.qrcode = qrcode;
    }

    //CONSTRUTOR - Inicializa os atributos para gerar Objeto Json
    public Aluno () {
        this.setMatricula("");
        this.setQrCode("");
        this.setNome("");

    }

    public String getQrCode(){ return this.qrcode;}

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
