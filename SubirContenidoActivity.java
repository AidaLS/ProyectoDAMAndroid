package proyecto.com.proyecto;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static proyecto.com.proyecto.MainActivity.PUEDE_SUBIR;
import static proyecto.com.proyecto.MainActivity.USUARIO;

public class SubirContenidoActivity extends AppCompatActivity {

    private String palabra, traduccion, respuesta, url, obDefinicion, insDefiniciones;
    private TextView txtATraducir, txtTraduccion;
    private ImageView imgEsp, imgUK, imgExiste;
    private RadioButton rdBtnEspanol, rdBtnEnglish;
    private Button btnSubir, btnVolver;
    private ComServidor server;
    private String responseStr="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_contenido);

        if (!PUEDE_SUBIR){
            AlertDialog alertDialog = new AlertDialog.Builder(SubirContenidoActivity.this).create();
            alertDialog.setTitle("Aviso");
            alertDialog.setMessage("Debes estar registrado/logueado para subir contenido.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Volver",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(SubirContenidoActivity.this, MainActivity.class);
                            startActivity(i);
                        }
                    });
            alertDialog.show();
        }

        server = new ComServidor();
        url = "https://pert-woman.000webhostapp.com/";
        obDefinicion = "ObtenerDefNombre.php";
        insDefiniciones = "InsertarDefiniciones.php";
        btnSubir = findViewById(R.id.btnSubir);
        btnVolver = findViewById(R.id.btnVolver);
        rdBtnEspanol = findViewById(R.id.radioSpanish);
        rdBtnEnglish = findViewById(R.id.radioEnglish);
        txtATraducir = findViewById(R.id.txtPalabraTraducir);
        txtTraduccion = findViewById(R.id.txtTraduccion);
        imgEsp = findViewById(R.id.imgBanderaEsp);
        imgUK = findViewById(R.id.imgBanderaUK);
        imgExiste = findViewById(R.id.imgExiste);

        imgUK.setImageResource(R.drawable.uk_flag_desactivated);
        //cuando pierde el foco el editText, comprueba si existe
        txtATraducir.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //coger la palabra, si no existe no hace nada
                boolean isEspaniol= true;
                final String palabra = txtATraducir.getText().toString();
                final String restoURL;
                if(rdBtnEnglish.isChecked()){
                    isEspaniol=false;
                    restoURL = "?def="+palabra+"&lang=eng";
                }else{
                    restoURL = "?def="+palabra+"&lang=esp";
                }
                if (palabra.equals("")) {

                } else {
                    //iniciar peticion
                    String urlFinal = url + obDefinicion+restoURL;
                    OkHttpClient client = new OkHttpClient();
                    server.run(urlFinal, client, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Something went wrong
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //comprueba si existe
                            if (response.isSuccessful()) {
                                responseStr = response.body().string();
                                // Do what you want to do with the response.
                                JsonObject json = new Gson().fromJson(responseStr, JsonObject.class);
                                final int result = json.get("estado").getAsInt();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result !=1){
                                            imgExiste.setVisibility(View.VISIBLE);
                                            btnSubir.setEnabled(true);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Esa palabra ya existe.", Toast.LENGTH_LONG).show();
                                            btnSubir.setEnabled(false);
                                            imgExiste.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });

                            } else {
                                // Request not successful
                                Toast.makeText(getApplicationContext(), "NO FUNCIONA", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                palabra = txtATraducir.getText().toString();
                traduccion = txtTraduccion.getText().toString();
                final String urlFinal;
                //InsertarDefiniciones.php?esp=Caca&eng=Poo
                if(rdBtnEnglish.isChecked()){
                    urlFinal = url+"InsertarDefiniciones.php?eng="+palabra+"&esp="+traduccion+"&autor="+USUARIO;
                }else{
                    urlFinal = url+"InsertarDefiniciones.php?esp="+palabra+"&eng="+traduccion+"&autor="+USUARIO;
                }

                if (palabra.equals("") || traduccion.equals("")) {
                    Toast.makeText(getApplicationContext(), "Se necesita rellenar los dos campos.", Toast.LENGTH_LONG).show();
                } else {
                    //para enviar la definicion
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            try {
                                server.run(urlFinal, client, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        // Something went wrong
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        //comprueba si existe
                                        if (response.isSuccessful()) {
                                            responseStr = response.body().string();
                                            // Do what you want to do with the response.
                                            JsonObject json = new Gson().fromJson(responseStr, JsonObject.class);
                                            final int result = json.get("estado").getAsInt();
                                            if (result==1){
                                                Toast.makeText(SubirContenidoActivity.this, "Enviado a moderación, gracias!", Toast.LENGTH_LONG).show();
                                            }

                                        } else {
                                            // Request not successful
                                            Toast.makeText(getApplicationContext(), "NO FUNCIONA", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                                Toast.makeText(getApplicationContext(), "Enviado a moderación, gracias!", Toast.LENGTH_LONG).show();
                                txtATraducir.setText("");
                                txtTraduccion.setText("");

                            } catch (Exception e) {
                                Log.e("Error", "Exception: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubirContenidoActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        rdBtnEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rdBtnEnglish.isChecked()) {
                    imgEsp.setImageResource(R.drawable.spanish_flag_desactivated);
                } else {
                    imgEsp.setImageResource(R.drawable.spanish_flag);
                }
            }
        });

        rdBtnEspanol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (rdBtnEspanol.isChecked()) {
                    imgUK.setImageResource(R.drawable.uk_flag_desactivated);
                } else {
                    imgUK.setImageResource(R.drawable.uk_flag);
                }
            }
        });





    }



}


