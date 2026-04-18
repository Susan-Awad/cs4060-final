package edu.uga.cs.cs4060_final;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class is an activity to create a new shopping item. MIGHT END UP DELETING THIS FILE, SINCE WE
 * HAVE THE DIALOG
 */
public class NewShoppingItemActivity extends AppCompatActivity {

    private EditText itemNameView;
    private EditText quantityView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shopping_item);

        itemNameView = findViewById(R.id.editTextText3);
        quantityView = findViewById(R.id.editTextText4);
        Button savebutton = findViewById(R.id.button9);

        savebutton.setOnClickListener(new ButtonClickListener());
    } //onCreate

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String itemName = itemNameView.getText().toString();
            String quantity = quantityView.getText().toString();
            final ShoppingItem shoppingItem = new ShoppingItem(itemName, quantity);

            // Add a new element (ShoppingItem) to the list of shopping items in Firebase.
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("shoppingList");

            // First, a call to push() appends a new node to the existing list (one is created
            // if this is done for the first time).  Then, we set the value in the newly created
            // list node to store the new job lead.
            // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
            // the previous apps to maintain job leads.
            myRef.push().setValue(shoppingItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Shopping Item created for " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();

                    // Clear teh EditTexts for next use.
                    itemNameView.setText("");
                    quantityView.setText("");

                } //onSucess
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create a shopping item for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        } //onFailure
                    });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
