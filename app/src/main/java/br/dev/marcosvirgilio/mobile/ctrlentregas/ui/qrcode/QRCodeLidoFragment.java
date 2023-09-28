package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


public class QRCodeLidoFragment extends Fragment implements View.OnClickListener {

    private TextView etCd;
    private TextView etNm;

    private TextView tvMensagem;
    private Button btConfirmar;
    private Button btCancelar;
    View view;
    //volley
    private JsonObjectRequest jsonObjectReq;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.btConfirmar.setVisibility(View.GONE);
        //recuperando objeto aluno do singleton
        Aluno a = SingletonNavigation.getInstance().getAluno();
        if (a.getNome().equals("") && a.getMatricula().equals("") && !a.getQrCode().equals("")) {
            //chamar REST consultar aluno aqui
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.GET, Constantes.getServidor() + Constantes.getEndPointConIdEstudantil(),
                    a.toJsonObject(), response -> {
                        try {
                            btConfirmar.setVisibility(View.GONE);
                            String resposta = response.toString();
                            //convertendo resposta strin to json
                            JSONObject jor = new JSONObject(resposta);
                            //mostrando retorno da consulta REST
                            etCd.setText(jor.getString("matricula").toString());
                            etNm.setText(jor.getString("nome").toString());
                            //guardar objeto aluno com nome no Singletom
                            Aluno ar = new Aluno(jor);
                            SingletonNavigation.getInstance().setAluno(ar);
                            if (jor.getBoolean("sucesso")){
                                btConfirmar.setVisibility(View.VISIBLE);
                            } else {
                                tvMensagem.setText(jor.getString("mensagem").toString());
                                //mostrar mensagem de erro na tela
                                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                                SingletonNavigation.getInstance().setMensagemErro(jor.getString("mensagem"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, errorResponse -> {
                            SingletonNavigation.getInstance().setMensagemErro(errorResponse.toString());
                            Aluno ar = SingletonNavigation.getInstance().getAluno();
                            //mostrando retorno da consulta REST
                            etCd.setText(ar.getMatricula().toString());
                            etNm.setText(ar.getNome().toString());
                            tvMensagem.setText(errorResponse.toString());
                            Snackbar.make(view,errorResponse.toString(),Snackbar.LENGTH_LONG).show();
                    });
            //seguindo com o request pelo singleton
            SingletonVolley.getInstance(this.getContext()).addToRequestQueue(jsonObjectReq);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_qr_code_lido, container, false);
        this.etCd = (TextView) view.findViewById(R.id.etCodigo);
        this.etNm = (TextView)view.findViewById(R.id.etNome);
        this.btCancelar = (Button)view.findViewById(R.id.btCancelar);
        this.tvMensagem = (TextView) view.findViewById(R.id.mensagem);
        this.btConfirmar = (Button)view.findViewById(R.id.btConfirmar);
        this.btConfirmar.setOnClickListener(this);
        this.btCancelar.setOnClickListener(this);
        return this.view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btConfirmar) {

            //chamar REST salvar aqui
            Aluno a = new Aluno();
            a.setMatricula(this.etCd.getText().toString());
            a.setNome(this.etNm.getText().toString());
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST,Constantes.getServidor() + Constantes.getEndPointCadProtocolo(),
                    a.toJsonObject(),response ->  {
                            SingletonNavigation.getInstance().setMensagemErro(response.toString());
                            //mostrar mensagem de erro na tela
                            Snackbar.make(view,response.toString(),Snackbar.LENGTH_LONG).show();
                    },errorResponse -> {
                            try {
                                String resposta = errorResponse.toString();
                                //convertendo resposta strin to json
                                JSONObject jor = new JSONObject(resposta);
                                if (jor.getBoolean("sucesso")){
                                    //mostrar mensagem de erro na tela
                                    Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                                    //chamando navegação para tela de erro
                                    SingletonNavigation.getInstance().getNavController().navigate(R.id.navigation_qrcode);
                                } else {
                                    //mostrar mensagem de erro na tela
                                    Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();

                                }
                            } catch (Exception e) {  e.printStackTrace(); }

                    });
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


}