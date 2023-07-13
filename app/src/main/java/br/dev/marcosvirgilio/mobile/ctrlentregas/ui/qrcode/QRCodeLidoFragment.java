package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import br.dev.marcosvirgilio.mobile.ctrlentregas.QRCodeSingleton;
import br.dev.marcosvirgilio.mobile.ctrlentregas.R;


public class QRCodeLidoFragment extends Fragment {

    private EditText etCd;
    private EditText etNm;
    private Button btConfirmar;
    private Button btCancelar;
    View view;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_qr_code_lido, container, false);

        this.etCd = view.findViewById(R.id.etCodigo);
        //mostrando valor do singleton
        QRCodeSingleton singleton = QRCodeSingleton.getInstance();
        this.etCd.setText(singleton.getQrCode());

        return this.view;
    }
}