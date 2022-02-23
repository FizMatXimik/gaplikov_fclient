package ru.igap.gaplikov_fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import ru.igap.gaplikov_fclient.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'gaplikov_fclient' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("mbedcrypto");

    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int res = initRng();
        byte[] key = randomBytes(16);
        byte[] data = randomBytes(200);

        byte[] encdata = encrypt(key, data);
        byte[] decdata = decrypt(key, encdata);

        boolean equal = true;
        if (decdata.length != data.length) equal = false;
        for (int i = 0; i < decdata.length; i++) {
            if (data[i] != decdata[i]) equal = false;
        }
        System.out.print(equal);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
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