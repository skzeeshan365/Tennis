package com.reiserx.tennis.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.reiserx.tennis.OCR.requestOcr;
import com.reiserx.tennis.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private requestOcr requestOcr;
    String TAG = "ihfsidhfs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setText("scan");
        requestOcr = new requestOcr(this, binding.editTextTextPersonName, binding.output, binding.outputHolder);
        binding.outputHolder.setVisibility(View.GONE);

        binding.editTextTextPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.editTextTextPersonName.getText().toString().equals("")) {
                    binding.button.setText("scan");
                } else {
                    binding.button.setText("run");
                }
            }
        });

        binding.button.setOnClickListener(view -> {
            if (binding.button.getText().toString().equals("scan")) {
                CropImageOptions cropImageOptions = new CropImageOptions();
                cropImageOptions.imageSourceIncludeGallery = true;
                cropImageOptions.imageSourceIncludeCamera = true;
                CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(null, cropImageOptions);
                cropImage.launch(cropImageContractOptions);
            } else {
                try {
                    requestOcr.runCode(binding.editTextTextPersonName.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    binding.output.setText(e.toString());
                }
            }
        });
    }

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
            try {
                requestOcr.scan_file(getEncoded64ImageStringFromBitmap(cropped));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    });

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();

        // Get the Base64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public String loadJSONFile() {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("response.json");
            int size = inputStream.available();
            byte[] byteArray = new byte[size];
            inputStream.read(byteArray);
            inputStream.close();
            json = new String(byteArray, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
            return null;
        }
        return json;
    }
}