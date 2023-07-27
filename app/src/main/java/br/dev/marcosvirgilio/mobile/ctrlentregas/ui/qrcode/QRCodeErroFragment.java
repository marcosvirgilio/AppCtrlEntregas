package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.Singleton;


public class QRCodeErroFragment extends Fragment implements View.OnClickListener{
    private EditText etCd;
    private EditText etMensagem;
    private Button btProxima;
    private NavController navController;
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_qr_code_erro, container, false);
        this.navController  = Singleton.getInstance().getNavController();
        this.etCd = view.findViewById(R.id.etCodigo);
        this.etMensagem = view.findViewById(R.id.etMensagem);
        this.btProxima = view.findViewById(R.id.btProximo);
        this.btProxima.setOnClickListener(this);
        //mostrando valor do singleton
        this.etCd.setText(Singleton.getInstance().getAluno().getMatricula());
        this.etMensagem.setText(Singleton.getInstance().getMensagemErro());
        return this.view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btProximo) {
            //chamando navegação
            navController.navigate(R.id.navigation_qrcode);
        }
    }
}