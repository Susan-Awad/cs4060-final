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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private TextView user;
    private TextView pass;
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = findViewById( R.id.editTextTextEmailAddress );
        pass = findViewById(R.id.editTextTextPassword2);
        login = findViewById(R.id.button);
        register = findViewById(R.id.button2);
        mAuth = FirebaseAuth.getInstance();

        // when the user logs in, the sign in method is called on the firebase instance
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user.getText().toString();
                String password = pass.getText().toString();
                email = email.trim();

                mAuth.signInWithEmailAndPassword( email, password )
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // if the sign in is successful, move the user to the main menu activity
                                    Log.d( TAG, "signInWithEmail:success" );
                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                    Intent intent = new Intent(v.getContext(), MainMenu.class);
                                    intent.putExtra("User", currentUser);
                                    startActivity(intent);
                                }
                                else {
                                    // if sign in fails, display a message to the user
                                    Log.d( TAG, "signInWithEmail:failure", task.getException() );
                                    Toast.makeText( MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // if the user want to register, the on click listener will start the register activity
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Register.class);
                startActivity(intent);
            }
        });
    }
}