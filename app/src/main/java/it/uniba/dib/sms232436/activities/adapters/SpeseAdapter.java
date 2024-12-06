package it.uniba.dib.sms232436.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import it.uniba.dib.sms232436.R;
import it.uniba.dib.sms232436.classes.Spesa;

public class SpeseAdapter extends RecyclerView.Adapter<SpeseAdapter.SpesaViewHolder> {

    private List<Spesa> spese;
    private Context context;

    public SpeseAdapter(Context context, List<Spesa> spese) {
        this.context = context;
        this.spese = spese;
    }

    @NonNull
    @Override
    public SpesaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_spesa, parent, false);
        return new SpesaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpesaViewHolder holder, int position) {
        Spesa spesa = spese.get(position);
        String text = (" " + spesa.getNomeProdotto());
        holder.nomeProdotto.setText(text);
        text = (spesa.getImporto() + " € ");
        holder.importoSpesa.setText(text);
        text = " Categoria: " + spesa.getCategoria();
        holder.categoriaSpesa.setText(text);
        text = " Data Spesa: " + spesa.getDataSpesa();
        holder.dataSpesa.setText(text);
        if(spesa.getQuantità() != 0){
            text = " Quantità: " + spesa.getQuantità();
        }else{
            text = " ";
        }
        holder.quantita.setText(text);

    }

    @Override
    public int getItemCount() {
        return spese.size();
    }

    public static class SpesaViewHolder extends RecyclerView.ViewHolder {
        TextView nomeProdotto;
        TextView importoSpesa;
        TextView dataSpesa;
        TextView categoriaSpesa;
        TextView quantita;

        public SpesaViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProdotto = itemView.findViewById(R.id.shop_product);
            importoSpesa = itemView.findViewById(R.id.shop_price);
            dataSpesa = itemView.findViewById(R.id.shop_date);
            categoriaSpesa = itemView.findViewById(R.id.shop_category);
            quantita = itemView.findViewById(R.id.shop_quantity);
        }
    }
}
