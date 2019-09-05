package proyecto.com.proyecto;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class DiccionarioActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    public static ArrayList<Definicion> lista = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diccionario);

        mRecyclerView = findViewById(R.id.recyclerView);

        mLinearLayoutManager = new LinearLayoutManager(DiccionarioActivity.this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //linea decorativa
        mRecyclerView.addItemDecoration(new DividerItemDecoration(DiccionarioActivity.this,
                DividerItemDecoration.VERTICAL));


        Adapter adapter = new Adapter(lista,DiccionarioActivity.this);
        mRecyclerView.setAdapter(adapter);
    }

    public static ArrayList<Definicion> getLista() {

        return lista;
    }

}
