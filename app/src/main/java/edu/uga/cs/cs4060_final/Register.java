package edu.uga.cs.cs4060_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private TextView newEmail;
    private TextView newPassword;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        newEmail = findViewById(R.id.editTextTextEmailAddress2);
        newPassword = findViewById(R.id.editTextTextPassword);
        register = findViewById(R.id.button3);

        // when the user is ready to register, the create user method is called on the firebase instance
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = newEmail.getText().toString();
                String password = newPassword.getText().toString();

                mAuth = FirebaseAuth.getInstance();

                // creates a new user with the email and password provided
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // if the task is successful, the app will send a message and move the user to login page to login.
                                    Toast.makeText(getApplicationContext(),
                                            "Registered user: " + email,
                                            Toast.LENGTH_SHORT).show();

                                    Log.d(TAG, "createUserWithEmail: success");

                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    // if the task fails, send the user a message
                                    Log.w(TAG, "createUserWithEmail: failure", task.getException());
                                    Toast.makeText(Register.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
