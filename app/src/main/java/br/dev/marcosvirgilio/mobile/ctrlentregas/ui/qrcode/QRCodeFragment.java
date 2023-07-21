package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import br.dev.marcosvirgilio.mobile.ctrlentregas.Singleton;
import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Aluno;


public class QRCodeFragment extends Fragment implements  Response.ErrorListener, Response.Listener {

    //qrcode
    private static final String TAG = "MLKit Barcode";
    private static final int PERMISSION_CODE = 1001;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;
    private View view = null;
    //volley
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectReq;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qr_code, container, false);

        //qrcode
        previewView = this.view.findViewById(R.id.previewView);
        InicializarCamera();

        //instanciando a fila de requests - caso o objeto seja o view
        this.requestQueue = Volley.newRequestQueue(view.getContext());
//inicializando a fila de requests do SO
        this.requestQueue.start();


        return view;
    }

      private void onSuccessListener(List<Barcode> barcodes) {
        if (barcodes.size() > 0) {

            //buscando qrLido na base de codigos de matrículas

            Aluno a = new Aluno();
            a.setMatricula(barcodes.get(0).getDisplayValue());
            //colocando matricula lida qrcode no singleton
            Singleton singleton = Singleton.getInstance();
            singleton.setAluno(a);
            //chamar REST consultar aluno aqui
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST,"http://10.0.2.2/conmatricula.php",
                    a.toJsonObject(), this, this);
            requestQueue.add(jsonObjectReq);

            //Toast.makeText(this, barcodes.get(0).getDisplayValue(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );

        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();

        barcodeScanner.process(inputImage)
                .addOnSuccessListener(this::onSuccessListener)
                .addOnFailureListener(e -> Log.e(TAG, "Barcode process failure", e))
                .addOnCompleteListener(task -> image.close());
    }



    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        builder.setTargetRotation(getRotation());

        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind preview", e);
        }
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetRotation(getRotation());

        analysisUseCase = builder.build();
        analysisUseCase.setAnalyzer(cameraExecutor, this::analyze);

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind analysis", e);
        }
    }

    protected int getRotation() throws NullPointerException {
        return previewView.getDisplay().getRotation();
    }
    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void InicializarCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(view.getContext());

        int lensFacing = CameraSelector.LENS_FACING_BACK;
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "cameraProviderFuture.addListener Error", e);
            }
        }, ContextCompat.getMainExecutor(view.getContext()));
    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(Object response) {

        try {
            String resposta = response.toString();
            //convertendo resposta strin to json
            JSONObject jor = new JSONObject(resposta);
            Aluno ar = new Aluno(jor);
            if (ar.getNome().equals("erro")){
                //mostrar mensagem de erro na tela
                Snackbar.make(view,"Matrícula Inválida",Snackbar.LENGTH_LONG).show();
            } else {
                //guardar objeto aluno com nome no Singletom
                Singleton.getInstance().setAluno(ar);
                //chamando navegação para tela de confirmação de entrega
                NavController navController = Singleton.getInstance().getNavController();
                navController.navigate(R.id.navigation_qrcode_lido);
            }
        } catch (Exception e) {  e.printStackTrace(); }


    }
}