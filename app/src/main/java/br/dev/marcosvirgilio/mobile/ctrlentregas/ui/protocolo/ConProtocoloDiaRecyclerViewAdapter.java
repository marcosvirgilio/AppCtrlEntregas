package br.dev.marcosvirgilio.mobile.ctrlentregas.ui.protocolo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import br.dev.marcosvirgilio.mobile.ctrlentregas.databinding.FragmentConProtocolosDiaBinding;
import br.dev.marcosvirgilio.mobile.ctrlentregas.model.Protocolo;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Protocolo}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ConProtocoloDiaRecyclerViewAdapter extends RecyclerView.Adapter<ConProtocoloDiaRecyclerViewAdapter.ViewHolder> {

    private final List<Protocolo> mValues;

    public ConProtocoloDiaRecyclerViewAdapter(List<Protocolo> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentConProtocolosDiaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //ajustando formato da data
        String data = mValues.get(position).getData();
        String dataBR = data.substring(8,10) + "/" + data.substring(5,7) + "/" + data.substring(0,4);
        //mostrando data ajustada na tela
        holder.mIdView.setText(dataBR);
        holder.mContentView.setText("Entregas = " + String.valueOf(mValues.get(position).getQuantidade()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public Protocolo mItem;

        public ViewHolder(FragmentConProtocolosDiaBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}