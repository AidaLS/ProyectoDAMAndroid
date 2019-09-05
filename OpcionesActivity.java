package proyecto.com.proyecto;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class OpcionesActivity extends PreferenceActivity {

    private SharedPreferences preferencias;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.opciones);
    }
}
