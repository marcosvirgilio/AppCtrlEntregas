package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import br.dev.marcosvirgilio.mobile.ctrlentregas.QRCodeSingleton;
import br.dev.marcosvirgilio.mobile.ctrlentregas.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private  TextView tvScan = null;
    private View root;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        this.tvScan = binding.textHome;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //mostrando valor do singleton
        QRCodeSingleton singleton = QRCodeSingleton.getInstance();
        this.tvScan.setText(singleton.getQrCode());
    }
}