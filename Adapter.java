package proyecto.com.proyecto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class Adapter extends RecyclerView.Adapter<DefinicionViewHolder>{

    private List<Definicion> definiciones;
    private Context mContext;

    public Adapter(List definiciones, Context mContext) {
        this.definiciones = definiciones;
        this.mContext = mContext;
    }

    @Override
    public DefinicionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.elementos_dic,
                parent, false);
        return new DefinicionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DefinicionViewHolder holder, final int position) {
        holder.definicion.setText(definiciones.get(position).getDefinicion());
        holder.definicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("pulsado");
                TextView textView = (TextView) view.findViewById(R.id.definicion);
                String def = textView.getText().toString();
                ComEquivalenteIngles s = new ComEquivalenteIngles();
                String urlFinal = "https://pert-woman.000webhostapp.com/ObtenerDefNombre.php?def="+def+"&lang=esp";
                OkHttpClient client = new OkHttpClient();
                s.run(urlFinal, client, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Something went wrong
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //comprueba si existe
                        if (response.isSuccessful()) {
                            String rsp = response.body().string();
                            JsonObject json = new Gson().fromJson(rsp, JsonObject.class);
                            JsonObject jsonDef = json.get("usuario").getAsJsonObject();
                            String res = jsonDef.get("eng").getAsString();
                        }
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return definiciones.size();
    }

}

class DefinicionViewHolder extends RecyclerView.ViewHolder{

    TextView definicion;
    ImageView bandera;

    public DefinicionViewHolder(View itemView) {
        super(itemView);

        definicion = itemView.findViewById(R.id.definicion);
        bandera = itemView.findViewById(R.id.bandera);
    }

}
