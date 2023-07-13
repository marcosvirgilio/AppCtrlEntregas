package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

import br.dev.marcosvirgilio.mobile.ctrlentregas.R;

public class QRCodeResultadoActivity extends AppCompatActivity {

    private Button btConfirmar;
    private Button btCancelar;
    private EditText etId;
    private EditText etNome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_resultado);
        // binding
        this.etId = findViewById(R.id.etCodigo);
        this.etNome = findViewById(R.id.etNome);
        this.btCancelar = findViewById(R.id.btCancelar);
        this.btConfirmar = findViewById(R.id.btConfirmar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //pegar valor do intent

        this.etId.setText();
    }
}