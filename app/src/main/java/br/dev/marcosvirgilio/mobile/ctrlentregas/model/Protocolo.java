package br.dev.marcosvirgilio.mobile.ctrlentregas.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Protocolo {
    private String data;

    private int turno;
    private int quantidade;

    public Protocolo (JSONObject jp) {
        try {
            this.setData((String) jp.get("data"));
            this.setTurno(Integer.valueOf((String) jp.get("turno")));
            this.setQuantidade(Integer.valueOf((String) jp.get("qtd")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getTurno() {return turno;}

    public void setTurno(int turno) {
        this.turno = turno;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
