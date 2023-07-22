package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.os.Bundle;

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

import br.dev.marcosvirgilio.mobile.ctrlentregas.Singleton;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_qr_code_lido, container, false);
        this.navController  = Singleton.getInstance().getNavController();
        this.etCd = view.findViewById(R.id.etCodigo);
        this.etNm = view.findViewById(R.id.etNome);
        this.btCancelar = view.findViewById(R.id.btCancelar);
        this.btConfirmar = view.findViewById(R.id.btConfirmar);

        this.btCancelar.setOnClickListener(this);
        this.btConfirmar.setOnClickListener(this);
        //mostrando valor do singleton
        Singleton singleton = Singleton.getInstance();
        this.etCd.setText(singleton.getAluno().getMatricula());
        this.etNm.setText(singleton.getAluno().getNome());

        //instanciando a fila de requests - caso o objeto seja o view
        this.requestQueue = Volley.newRequestQueue(view.getContext());
//inicializando a fila de requests do SO
        this.requestQueue.start();


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
                    Request.Method.POST,
                    "http://192.168.5.165/ctrlentregas/cadprotocolo.php",
                    a.toJsonObject(), this, this);
            requestQueue.add(jsonObjectReq);
        }
        if (view.getId() == R.id.btCancelar) {
            //chamando navegação
            navController.navigate(R.id.navigation_qrcode);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Object response) {
        try {
            String resposta = response.toString();
            //convertendo resposta strin to json
            JSONObject jor = new JSONObject(resposta);
            if (jor.getBoolean("sucesso")){
                //guardar objeto aluno com nome no Singletom
                Aluno ar = new Aluno(jor);
                ar.setMatricula(jor.getString("matricula"));
                ar.setNome(jor.getString("nome"));
                Singleton.getInstance().setAluno(ar);
                //mostrar mensagem de sucesso
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                NavController navController = Singleton.getInstance().getNavController();
                navController.navigate(R.id.navigation_qrcode);
            } else {
                //mostrar mensagem de erro na tela
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {  e.printStackTrace(); }


    }
}