package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import br.dev.marcosvirgilio.mobile.ctrlentregas.util.Constantes;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.SingletonNavigation;
import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.SingletonVolley;


public class QRCodeLidoFragment extends Fragment implements View.OnClickListener, Response.ErrorListener, Response.Listener {

    private TextView etCd;
    private TextView etNm;

    private TextView tvMensagem;
    private Button btConfirmar;
    private Button btCancelar;
    View view;
    //volley
    private JsonObjectRequest jsonObjectReq;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_qr_code_lido, container, false);
        this.etCd = (TextView) view.findViewById(R.id.etCodigo);
        this.etNm = (TextView)view.findViewById(R.id.etNome);
        this.btCancelar = view.findViewById(R.id.btCancelar);
        this.tvMensagem = (TextView) view.findViewById(R.id.mensagem);
        this.btConfirmar = view.findViewById(R.id.btConfirmar);
        this.btConfirmar.setVisibility(View.GONE);
        //Fila de requests do Singleton iniciada
        SingletonVolley.getInstance(getActivity().getApplicationContext()).getRequestQueue().start();
        //desabilitando botões
        this.ativarBotoes(false);
        //definindo listeners
        this.btCancelar.setOnClickListener(this);
        this.btConfirmar.setOnClickListener(this);
        //recuperando objeto aluno do singleton
        Aluno a = SingletonNavigation.getInstance().getAluno();
        if (a.getNome().equals("") && a.getMatricula().equals("")) {
            //chamar REST consultar aluno aqui
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST, Constantes.getServidor() + Constantes.getEndPointConIdEstudantil(),
                    a.toJsonObject(), this, this);
            //seguindo com o request pelo singleton
            SingletonVolley.getInstance(this.getContext()).addToRequestQueue(jsonObjectReq);
        }

        return this.view;
    }
    private void ativarBotoes(boolean estado) {
        this.btCancelar.setClickable(estado);
        this.btConfirmar.setClickable(estado);
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btConfirmar) {
            //objeto response
            QRCodeLidoClickBotaoResponse response = new QRCodeLidoClickBotaoResponse(view);
            //chamar REST salvar aqui
            Aluno a = new Aluno();
            a.setMatricula(this.etCd.getText().toString());
            a.setNome(this.etNm.getText().toString());
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST,Constantes.getServidor() + Constantes.getEndPointCadProtocolo(),
                    a.toJsonObject(), response, response);
            //econdendo botão para evitar 2 clicks
            this.btConfirmar.setVisibility(View.GONE);
            //seguindo com o request pelo singleton
            SingletonVolley.getInstance(this.getContext()).addToRequestQueue(jsonObjectReq);
        }
        if (view.getId() == R.id.btCancelar) {
            //chamando navegação
            SingletonNavigation.getInstance().getNavController().navigate(R.id.navigation_qrcode);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        SingletonNavigation.getInstance().setMensagemErro(error.toString());
        Aluno ar = SingletonNavigation.getInstance().getAluno();
        //mostrando retorno da consulta REST
        this.etCd.setText(ar.getMatricula().toString());
        this.etNm.setText(ar.getNome().toString());
        this.tvMensagem.setText(error.toString());
        Snackbar.make(view,error.toString(),Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(Object response) {

        try {
            this.btConfirmar.setVisibility(View.GONE);
            String resposta = response.toString();
            //convertendo resposta strin to json
            JSONObject jor = new JSONObject(resposta);
            //mostrando retorno da consulta REST
            this.etCd.setText(jor.getString("matricula").toString());
            this.etNm.setText(jor.getString("nome").toString());
            //guardar objeto aluno com nome no Singletom
            Aluno ar = new Aluno(jor);
            SingletonNavigation.getInstance().setAluno(ar);
            if (jor.getBoolean("sucesso")){
                this.btConfirmar.setVisibility(View.VISIBLE);
            } else {
                this.tvMensagem.setText(jor.getString("mensagem").toString());
                //mostrar mensagem de erro na tela
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                SingletonNavigation.getInstance().setMensagemErro(jor.getString("mensagem"));
            }

        } catch (Exception e) {  e.printStackTrace(); }


    }
}