package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.qrcode;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private static final String TAG = "MLKit Barcode";
    private static final int PERMISSION_CODE = 1001;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private PreviewView cameraPreview;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;


    private View view = null;
    //volley
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectReq;



    private void getPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA_PERMISSION}, PERMISSION_CODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_qr_code, container, false);
        cameraPreview = view.findViewById(R.id.camera_preview);
        //instanciando a fila de requests - caso o objeto seja o view
        this.requestQueue = Volley.newRequestQueue(view.getContext());
        //inicializando a fila de requests do SO
        this.requestQueue.start();
        //qr code
        startCamera();

        return view;
    }

    public void startCamera() {
        if(ContextCompat.checkSelfPermission(getContext(), CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            setupCamera();
        } else {
            getPermissions();
        }
    }


    private void setupCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(getContext());

        int lensFacing = CameraSelector.LENS_FACING_BACK;
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "cameraProviderFuture.addListener Error", e);
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
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
        previewUseCase.setSurfaceProvider(cameraPreview.getSurfaceProvider());

        try {
            cameraProvider
                    .bindToLifecycle(this, cameraSelector, previewUseCase);
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
            cameraProvider
                    .bindToLifecycle(this, cameraSelector, analysisUseCase);
        } catch (Exception e) {
            Log.e(TAG, "Error when bind analysis", e);
        }
    }

    protected int getRotation() throws NullPointerException {
        return cameraPreview.getDisplay().getRotation();
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

    private void onSuccessListener(List<Barcode> barcodes) {
        if (barcodes.size() > 0) {
            //Toast.makeText(getContext(), barcodes.get(0).getDisplayValue(), Toast.LENGTH_SHORT).show();
            Aluno a = new Aluno();
            a.setMatricula( barcodes.get(0).getDisplayValue());
            a.setNome("");
            //colocando objeto aluno no singleton
            Singleton singleton = Singleton.getInstance();
            singleton.setAluno(a);
            //chamar REST consultar aluno aqui
            jsonObjectReq = new JsonObjectRequest(
                    Request.Method.POST,"http://192.168.5.165/ctrlentregas/conmatricula.php",
                    a.toJsonObject(), this, this);
            requestQueue.add(jsonObjectReq);
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
                //chamando navegação para tela de confirmação de entrega
                NavController navController = Singleton.getInstance().getNavController();
                navController.navigate(R.id.navigation_qrcode_lido);

            } else {
                //mostrar mensagem de erro na tela
                Snackbar.make(view,jor.getString("mensagem"),Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {  e.printStackTrace(); }


    }
}