package ru.igap.gaplikov_fclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.igap.gaplikov_fclient.databinding.ActivityMainBinding;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'gaplikov_fclient' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("mbedcrypto");

    }
    ActivityResultLauncher activityResultLauncher;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String pin = data.getStringExtra("pin");
                            Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        int res = initRng();
        byte[] key = randomBytes(16);
//        byte[] data = randomBytes(200);
//
//        byte[] encdata = encrypt(key, data);
//        byte[] decdata = decrypt(key, encdata);
//
//        boolean equal = true;
//        if (decdata.length != data.length) equal = false;
//        for (int i = 0; i < decdata.length; i++) {
//            if (data[i] != decdata[i]) equal = false;
//        }
//        System.out.print(equal);

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
    }

    public static byte[] stringToHex(String s) {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex) {
            hex = null;
        }
        return hex;
    }

    public void onButtonClick(View view) {
//        byte[] key = stringToHex("10012002300340045005600670078008");
//        byte[] enc = encrypt(key, stringToHex("000000000000000102"));
//        byte[] dec = decrypt(key, enc);
//        String str = new String(Hex.encodeHex(dec)).toUpperCase();
//        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Intent it = new Intent(this, PinpadActivity.class);
        //startActivity(it);
        activityResultLauncher.launch(it);
    }

    /**
     * A native method that is implemented by the 'gaplikov_fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
}