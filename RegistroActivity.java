package proyecto.com.proyecto;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static proyecto.com.proyecto.MainActivity.PUEDE_SUBIR;
import static proyecto.com.proyecto.MainActivity.URL_SERVIDOR;
import static proyecto.com.proyecto.MainActivity.USUARIO;

public class RegistroActivity extends AppCompatActivity {

    private TextView txtNick, txtPass;
    private Button btnDarAlta;
    private ComLogin server,srvRegistro;
    private TextView txtRegistro;
    private String modo,usu,pass,urlFinal;
    private ImageView infoPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        modo = getIntent().getStringExtra("modo");
        txtNick = findViewById(R.id.txtUsername);
        txtPass = findViewById(R.id.txtContraseña);
        btnDarAlta = findViewById(R.id.btnDarmeDeAlta);
        txtRegistro = findViewById(R.id.textViewTituloRegistrate);
        infoPass = findViewById(R.id.imageInfoPass);

        server = new ComLogin();
        srvRegistro = new ComLogin();

        if (modo.equals("l")) {
            txtRegistro.setText("Accede");
            btnDarAlta.setText("Entrar");
        } else {
            txtRegistro.setText("Regístrate");
        }


        btnDarAlta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usu = txtNick.getText().toString();
                pass = txtPass.getText().toString();
                //comprueba si es válido el pass
                boolean valido = validarPass(pass);
                if (valido &&!pass.isEmpty() || !usu.isEmpty()){
                    if (modo.equals("l")) {
                        acceder(usu,pass);
                    } else {
                        try {
                            subirUsuario(usu, pass);
                        }catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        infoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegistroActivity.this).create();
                alertDialog.setTitle("Reglas contraseña:");
                alertDialog.setMessage("- 8 caracteres + una mayúscula." +
                        "\n- 1 número.\n- 1 carácter especial.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

    }

    public void acceder(String usu,String pass) {
        //te declaras una constante que se inicie cuando se inicie sesion
        //iniciar peticion
        String urlFinal = URL_SERVIDOR+"ObtenerUsuarioNombre.php?nombre="+usu;
        final String ps=pass;
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
                    String responseStr = response.body().string();
                    // Do what you want to do with the response.
                    JsonObject json = new Gson().fromJson(responseStr, JsonObject.class);
                    int result = json.get("estado").getAsInt();
                    JsonObject cadUsuario = json.get("usuario").getAsJsonObject();
                    String passComprobar = cadUsuario.get("pass").getAsString();
                    boolean passValido=passComprobar.equals(ps);
                    if (result ==1 && passValido){
                        //si entra aquí existe, iniciar la constante
                        USUARIO= txtNick.getText()+"";
                        PUEDE_SUBIR=true;
                        Intent i = new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(i);
                    }else if(!passValido){
                        Toast.makeText(RegistroActivity.this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // Request not successful
                    Toast.makeText(getApplicationContext(), "NO FUNCIONA", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void subirUsuario(final String usu, final String pass) throws IOException {//FUNCIONA
        //comprobar que existe, se empieza como que no
        boolean existe = comprobar(usu);
        boolean passValido = validarPass(pass);
        if (!existe && passValido) {
            //comienza el proceso para dar de alta
            final String urlInsertar = URL_SERVIDOR + "/InsertarUsuarios.php?usuario="+usu+"&pass="+pass;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {


                        OkHttpClient client = new OkHttpClient();
                        server.run(urlInsertar, client, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                // Something went wrong
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                //comprueba si existe
                                if (response.isSuccessful()) {
                                    String responseStr = response.body().string();
                                    // Do what you want to do with the response.
                                    JsonObject json = new Gson().fromJson(responseStr, JsonObject.class);
                                    final int result = json.get("estado").getAsInt();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result !=1){

                                            }
                                        }
                                    });

                                } else {
                                    // Request not successful
                                    Toast.makeText(getApplicationContext(), "NO FUNCIONA", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        Toast.makeText(getApplicationContext(), "Usuario insertado.", Toast.LENGTH_LONG).show();
                        PUEDE_SUBIR=true;
                        USUARIO= txtNick.getText()+"";
                        txtNick.setText("");
                        txtPass.setText("");
                        Intent i = new Intent(RegistroActivity.this, MainActivity.class);
                        startActivity(i);

                    } catch (Exception e) {
                        Log.e("Error", "Exception: " + e.getMessage());
                    }
                }
            });
        }else{
            if(existe){
                Toast.makeText(getApplicationContext(), "Ya existe un usuario con ese nombre.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "La contraseña que has introducido es incorrecta.", Toast.LENGTH_LONG).show();
            }
        }


    }

    private boolean comprobar(String usu) {
        final boolean[] compruebo = {false};
            String urlComprobar= URL_SERVIDOR+"ObtenerUsuarioNombre.php?nombre="+usu;
            OkHttpClient client = new OkHttpClient();
            server.run(urlComprobar, client, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Something went wrong
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()){
                        String resp = response.body().string();
                        JsonObject json = new Gson().fromJson(resp, JsonObject.class);
                        final int res = json.get("estado").getAsInt();
                        if (res==1){
                            compruebo[0] = true;
                        }
                    }
                }
            });
        return compruebo[0];
    }

    public static boolean validarPass(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
