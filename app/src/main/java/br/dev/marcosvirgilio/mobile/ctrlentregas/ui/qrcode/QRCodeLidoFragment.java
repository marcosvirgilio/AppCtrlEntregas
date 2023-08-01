package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import br.dev.marcosvirgilio.mobile.ctrlentregas.util.Constantes;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.Singleton;
import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;


public class QRCodeLidoFragment extends Fragment implements View.OnClickListener, Response.ErrorListener, Response.Listener {

    private EditText etCd;
    private EditText etNm;
    private Button btConfirmar;
    private Button btCancelar;
    private NavController navController;
    View view;

    //volley
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectReq;

    @Override
    public void
    onCreate( @Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_qr_code_lido, container, false);
        this.navController  = Singleton.getInstance().getNavController();
        this.etCd = view.findViewById(R.id.etCodigo);
        this.etNm = view.findViewById(R.id.etNome);
        this.btCancelar = view.findViewById(R.id.btCancelar);
        this.btConfirmar = view.findViewById(R.id.btConfirmar);
        //instanciando a fila de requests - caso o objeto seja o view
        this.requestQueue = Volley.newRequestQueue(view.getContext());
        //inicializando a fila de requests do SO
        this.requestQueue.start();
        //desabilitando botões
        this.ativarBotoes(false);
        //definindo listeners
        this.btCancelar.setOnClickListener(this);
        this.btConfirmar.setOnClickListener(this);
        //recuperando objeto aluno do singleton
        Aluno a = Singleton.getInstance().getAluno();
        if (a.getNome().equals("")) {
            //chamar REST consultar aluno aqui
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST, Constantes.getServidor() + Constantes.getEndPointConMatricula(),
                    a.toJsonObject(), this, this);
            requestQueue.add(jsonObjectReq);
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
            ConfirmarVoleyResponse response = new ConfirmarVoleyResponse(view);
            //chamar REST salvar aqui
            Aluno a = new Aluno();
            a.setMatricula(this.etCd.getText().toString());
            a.setNome(this.etNm.getText().toString());
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST,Constantes.getServidor() + Constantes.getEndPointCadProtocolo(),
                    a.toJsonObject(), response, response);
            requestQueue.add(jsonObjectReq);

        }
        if (view.getId() == R.id.btCancelar) {
            //chamando navegação
            navController.navigate(R.id.navigation_qrcode);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Singleton.getInstance().setMensagemErro(error.toString());
        Aluno ar = Singleton.getInstance().getAluno();
        //mostrando retorno da consulta REST
        this.etCd.setText(ar.getMatricula().toString());
        this.etNm.setText(ar.getNome().toString());
        Snackbar.make(view,error.toString(),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response) {
        try {
            String resposta = response.toString();
            //convertendo resposta strin to json
            JSONObject jor = new JSONObject(resposta);
            //guardar objeto aluno com nome no Singletom
            Aluno ar = new Aluno(jor);
            ar.setMatricula(jor.getString("matricula"));
            ar.setNome(jor.getString("nome"));
            Singleton.getInstance().setAluno(ar);
            //mostrando retorno da consulta REST
            this.etCd.setText(ar.getMatricula().toString());
            if (jor.getBoolean("sucesso")){
                this.etNm.setText(ar.getNome().toString());
            } else {
                this.etNm.setText(jor.getString("mensagem".toString()));
                //mostrar mensagem de erro na tela
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                Singleton.getInstance().setMensagemErro(jor.getString("mensagem"));

            }
        } catch (Exception e) {  e.printStackTrace(); }


    }
}