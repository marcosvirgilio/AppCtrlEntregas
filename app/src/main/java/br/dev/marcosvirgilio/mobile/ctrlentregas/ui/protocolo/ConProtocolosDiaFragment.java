package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.protocolo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.dev.marcosvirgilio.mobile.ctrlentregas.R;
import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Protocolo;
import br.dev.marcosvirgilio.mobile.ctrlentregas.util.Constantes;

/**
 * A fragment representing a list of Items.
 */
public class ConProtocolosDiaFragment extends Fragment implements View.OnClickListener, Response.ErrorListener, Response.Listener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    View view;
    //volley

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConProtocolosDiaFragment() {
    }


    @SuppressWarnings("unused")
    public static ConProtocolosDiaFragment newInstance(int columnCount) {
        ConProtocolosDiaFragment fragment = new ConProtocolosDiaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_con_protocolos_dia_list, container, false);
        //request com array de parâmetros em branco
        this.consultarEndPoint();
        return this.view;
    }

    public void consultarEndPoint(){
        try {
            JsonArrayRequest jsonArrayReq = new JsonArrayRequest(
                    Request.Method.GET, Constantes.getServidor() + Constantes.getEndPointConProtocolosDia(),
                    new JSONArray ("[]"), this, this);
            //adicionando a fila via singleton
            //seguindo com o request
            RequestQueue requestQueueConEntregas = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueueConEntregas.add(jsonArrayReq);
            requestQueueConEntregas.start();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Snackbar.make(view,error.toString(),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response) {
        String resposta = response.toString();
        //convertendo resposta strin to json
        JSONObject jObject = null ;
        Protocolo protocolo = null;
        try {
            JSONArray jArray = new JSONArray(resposta);
            if (jArray != null) {
                //instanciando array list
                ArrayList<Protocolo> protocolos = new ArrayList<Protocolo>();
                //copiando dados do JsonArray para ArrayList
                for (int i=0;i<jArray.length();i++){
                    jObject = new JSONObject(jArray.getString(i));
                    protocolo = new Protocolo(jObject);
                    protocolos.add(protocolo);
                }
                // Mostrando os dados do ArrayList na tela
                if (view instanceof RecyclerView) {
                    Context context = view.getContext();
                    RecyclerView recyclerView = (RecyclerView) view;
                    if (mColumnCount <= 1) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                    }
                    recyclerView.setAdapter(new ConProtocoloDiaRecyclerViewAdapter(protocolos));
                }
            } else {
                Snackbar.make(view,"Não foram encontradas entregas na base de dados",Snackbar.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Snackbar.make(view,e.toString(),Snackbar.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }


    }
}