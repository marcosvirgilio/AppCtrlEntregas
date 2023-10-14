package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;

import br.dev.marcosvirgilio.mobile.ctrlentregas.MainActivity;
import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.Constantes;

public class QRCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView etId;
    private TextView etCd;
    private TextView etNm;
    private TextView tvMensagem;
    private Button btConfirmar;
    private Button btCancelar;

    //volley
    private JsonObjectRequest jsonObjectReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        // referencing and initializing
        // the button and textviews
        this.etId = (TextView)findViewById(R.id.textContent);
        this.etCd = (TextView)findViewById(R.id.etCodigo);
        this.etNm = (TextView)findViewById(R.id.etNome);
        this.btCancelar = (Button)findViewById(R.id.scanBtn);
        this.tvMensagem = (TextView)findViewById(R.id.mensagem);
        this.btConfirmar = (Button)findViewById(R.id.btConfirmar);
        // adding listener to the button
        btCancelar.setOnClickListener(this);
        btConfirmar.setOnClickListener(this);
        //limpar dados do aluno da tela
        limparDadosAlunoTela();
        //botão visível
        this.btConfirmar.setVisibility(View.VISIBLE);
        //econdendo botão confirmar por default
        this.btConfirmar.setVisibility(View.GONE);
        //action bar - botão voltar
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

    }

    //action bar - botão voltar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



    /*    MENU DE OPÇÔES DE EXEMPLO COM EVENTOS DE CLICK

     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean retorno = false;
        if (item.getItemId() == R.id.ConEntregas) {
            //Toast.makeText(getApplicationContext(),"Porra",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.putExtra("EXTRA_MESSAGE", "PORRA");
            startActivity(intent);
            retorno = true;
        }
        if (item.getItemId() ==  R.id.Sobre) {
            //
            retorno =  true;
        }
        return retorno;
    }
     */

    public void limparDadosAlunoTela() {
        //inicializar dados da tela
        this.etCd.setText("");
        this.etNm.setText("");
        this.etId.setText("");
        this.tvMensagem.setText("");
    }

    @Override
    public void onClick(View view) {
        /*BOTAO CONFIRMAR*/
        if (view.getId() == R.id.btConfirmar) {
            //chamar REST salvar aqui
            Aluno a = new Aluno();
            a.setMatricula(this.etCd.getText().toString());
            a.setNome(this.etNm.getText().toString());
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST, Constantes.getServidor() + Constantes.getEndPointCadProtocolo(),
                    a.toJsonObject(),response ->  {
                try {
                    if (response.getBoolean("sucesso")){
                        //mostrar msg de retorno do rest na tela
                        Snackbar.make(view,response.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                        this.iniciaLeitorQrCode();
                    } else {
                        //mostrar msg de erro do rest na tela
                        Snackbar.make(view,response.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            },errorResponse -> {
                try {
                    //mostrar msg de erro do rest na tela
                    Snackbar.make(view,errorResponse.toString(),Snackbar.LENGTH_LONG).show();
                } catch (Exception e) {  e.printStackTrace(); }
            });
            //econdendo botão para evitar 2 clicks
            this.btConfirmar.setVisibility(View.GONE);
            //seguindo com o request
            RequestQueue requestQueueConfirmar = Volley.newRequestQueue(getApplicationContext());
            requestQueueConfirmar.add(jsonObjectReq);
            requestQueueConfirmar.start();
            //SingletonVolley.getInstance(this.getContext()).addToRequestQueue(jsonObjectReq);
        }
        /*BOTAO CANCELAR*/
        if (view.getId() == R.id.scanBtn) {
            //Ler novamente ou nova carteira
            this.iniciaLeitorQrCode();
            //SingletonNavigation.getInstance().getNavController().navigate(R.id.navigation_qrcode);
        }

    }

    private void iniciaLeitorQrCode(){
        // we need to create the object
        // of IntentIntegrator class
        // which is the class of QR library
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Lendo QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelado", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                if (intentResult.getFormatName().toString().equalsIgnoreCase("QR_CODE")){
                    //fazer request
                    //objeto aluno
                    Aluno alunoRequest = new Aluno();
                    alunoRequest.setQrCode(intentResult.getContents());
                    alunoRequest.setMatricula("");
                    alunoRequest.setNome("");
                    //chamar REST consultar aluno aqui
                    jsonObjectReq = new JsonObjectRequest(
                            Request.Method.POST, Constantes.getServidor() + Constantes.getEndPointConIdEstudantil(), alunoRequest.toJsonObject(),
                            response -> {
                                try {
                                    if (response.getBoolean("sucesso")) {
                                        //mostra dados do request na tela
                                        this.etCd.setText(response.getString("matricula"));
                                        this.etNm.setText(response.getString("nome"));
                                        etId.setText(intentResult.getContents());
                                        //botão visível
                                        this.btConfirmar.setVisibility(View.VISIBLE);
                                        this.tvMensagem.setText("");
                                    } else {
                                        this.tvMensagem.setText(response.getString("mensagem"));
                                        //Snackbar.make(view, response.getString("mensagem"), Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }, errorResponse -> {
                        this.tvMensagem.setText(errorResponse.toString());
                        //Snackbar.make(view, errorResponse.toString(), Snackbar.LENGTH_LONG).show();
                    }
                    );
                    //seguindo com o request
                    RequestQueue requestQueueConQrCode = Volley.newRequestQueue(getApplicationContext());
                    requestQueueConQrCode.add(jsonObjectReq);
                    requestQueueConQrCode.start();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
