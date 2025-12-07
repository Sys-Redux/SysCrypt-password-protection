package com.syscrypt;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.syscrypt.crypto.CryptoManager;

public class MainActivity extends AppCompatActivity {

    private EditText plainTextInput;
    private EditText encryptedTextInput;
    private EditText keyInput;
    private Spinner algorithmSpinner;
    private CryptoManager cryptoManager;
    private boolean isProcessing = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cryptoManager = new CryptoManager();
        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        plainTextInput = findViewById(R.id.plain_text_input);
        encryptedTextInput = findViewById(R.id.encrypted_text_input);
        keyInput = findViewById(R.id.key_input);
        algorithmSpinner = findViewById(R.id.algorithm_spinner);

        // Setup algorithm dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                CryptoManager.getAlgorithmNames()
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        algorithmSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        plainTextInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(Editable s) {
                if (!isProcessing && plainTextInput.hasFocus()) {
                    performEncryption();
                }
            }
        });

        // Listen for encrypted text changes (decrypt mode)
        encryptedTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!isProcessing && encryptedTextInput.hasFocus()) {
                    performDecryption();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        // Listen for key changes (re-process current operation)
        keyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!isProcessing) {
                    // Determine which field has focus or content to process
                    if (plainTextInput.hasFocus() && plainTextInput.getText().length() > 0) {
                        performEncryption();
                    } else if (encryptedTextInput.hasFocus() && encryptedTextInput.getText().length() > 0) {
                        performDecryption();
                    } else if (plainTextInput.getText().length() > 0) {
                        // Default to encryption if plain text has content
                        performEncryption();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Listen for algorithm changes
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isProcessing && plainTextInput.getText().length() > 0) {
                    performEncryption();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void performEncryption() {
        String plainText = plainTextInput.getText().toString();
        String key = keyInput.getText().toString();

        if (plainText.isEmpty() || key.isEmpty()) {
            return;
        }

        try {
            isProcessing = true;
            String selectedAlgorithm = algorithmSpinner.getSelectedItem().toString();
            CryptoManager.Algorithm algorithm = CryptoManager.Algorithm.fromDisplayName(selectedAlgorithm);

            String encrypted = cryptoManager.encrypt(plainText, key, algorithm);
            encryptedTextInput.setText(encrypted);
        } catch (Exception e) {
            encryptedTextInput.setText("");
        } finally {
            isProcessing = false;
        }
    }

    private void performDecryption() {
        String cipherText = encryptedTextInput.getText().toString().trim();
        String key = keyInput.getText().toString();

        if (cipherText.isEmpty() || key.isEmpty()) {
            return;
        }

        try {
            isProcessing = true;
            String selectedAlgorithm = algorithmSpinner.getSelectedItem().toString();
            CryptoManager.Algorithm algorithm = CryptoManager.Algorithm.fromDisplayName(selectedAlgorithm);

            String decrypted = cryptoManager.decrypt(cipherText, key, algorithm);
            plainTextInput.setText(decrypted);
            isProcessing = false;
        } catch (Exception e) {
            isProcessing = false;
        }
    }
}