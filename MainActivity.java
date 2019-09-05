package proyecto.com.proyecto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{


    public static String URL_SERVIDOR = "https://pert-woman.000webhostapp.com/",USUARIO;
    public static boolean PUEDE_SUBIR=false;
    private boolean unaVez = false;
    private SharedPreferences pref;
    private ComDiccionario serverr;


    //elementos de la pagina
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.btnFlotanteInfo);
        Button btnDiccionario = findViewById(R.id.btnDiccionario);
        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        Button btnSubeDefiniciones = findViewById(R.id.btnSubeTuContenido);
        TextView txtLogIn = findViewById(R.id.txtLogIn);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        DiccionarioActivity.getLista().removeAll(DiccionarioActivity.getLista());

            serverr = new ComDiccionario();
            String urlFinal = "https://pert-woman.000webhostapp.com/ObtenerDefiniciones.php";
            OkHttpClient client = new OkHttpClient();
            serverr.run(urlFinal, client, new Callback() {
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
                        JsonArray jsonArray = json.getAsJsonArray("definiciones");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject elemento = (JsonObject) jsonArray.get(i);
                            String esp = elemento.get("esp").getAsString();
                            String eng = elemento.get("eng").getAsString();
                            DiccionarioActivity.getLista().add(new Definicion(esp));
                        }
                    }
                }
            });




        btnDiccionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DiccionarioActivity.class);
                startActivity(i);
            }
        });


        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RegistroActivity.class);
                i.putExtra("modo","r");
                startActivity(i);
            }
        });

        btnSubeDefiniciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SubirContenidoActivity.class);
                startActivity(i);
            }
        });

        txtLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RegistroActivity.class);
                i.putExtra("modo","l");
                startActivity(i);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "¿Tienes dudas de cómo se dice algo en inglés?" +
                        " ¡No te preocupes, con iLandroid podrás consultar y subir tus propias definiciones! El diccionario bilingüe cooperativo.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnOpciones) {
            Intent i = new Intent(MainActivity.this, OpcionesActivity.class);
            startActivity(i);
            return true;
        }else if(id == R.id.btnDefiniciones){

        }
        return super.onOptionsItemSelected(item);
    }


}


