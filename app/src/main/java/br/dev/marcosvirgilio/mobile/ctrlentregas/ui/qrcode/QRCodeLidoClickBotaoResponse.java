package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.view.View;

import androidx.navigation.NavController;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.SingletonNavigation;

public class QRCodeLidoClickBotaoResponse implements Response.ErrorListener, Response.Listener{

    private View view;
    public QRCodeLidoClickBotaoResponse(View view){
        this.view = view;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        SingletonNavigation.getInstance().setMensagemErro(error.toString());
        //mostrar mensagem de erro na tela
        Snackbar.make(view,error.toString(),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response) {
        try {
            String resposta = response.toString();
            //convertendo resposta strin to json
            JSONObject jor = new JSONObject(resposta);
            if (jor.getBoolean("sucesso")){
                //mostrar mensagem de erro na tela
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
                //chamando navegação para tela de erro
                NavController navController = SingletonNavigation.getInstance().getNavController();
                navController.navigate(R.id.navigation_qrcode);
            } else {
                //mostrar mensagem de erro na tela
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();

            }
        } catch (Exception e) {  e.printStackTrace(); }
    }
}
