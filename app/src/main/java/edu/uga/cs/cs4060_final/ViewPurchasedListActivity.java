package edu.uga.cs.cs4060_final;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


    /**
     * This class is an activity to create a new purchased list.
     */
    public class ViewPurchasedListActivity extends AppCompatActivity
            implements EditPurchaseDialogFragment.EditPurchaseDialogListener {

        public static final String DEBUG_TAG = "ViewPurchasedListActivity";

        private TextView textView;
        private RecyclerView recyclerView;
        private PurchaseRecyclerAdapter recyclerAdapter;
        private List<Purchase> purchaseList;
        private FirebaseDatabase database;
        private FirebaseAuth mAuth;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            Log.d(DEBUG_TAG, "onCreate()");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.view_shopping_list);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, systemBars.top, 0, 0);
                return insets;
            });

            textView = findViewById(R.id.textView7);
            recyclerView = findViewById(R.id.recyclerView);

            textView.setText("Purchased Items");
            // initialize the Purchase List
            purchaseList = new ArrayList<Purchase>();

            // use a linear layout manager for the recycler view
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // the recycler adapter with shopping items is empty at first; it will be updated later
            recyclerAdapter = new PurchaseRecyclerAdapter(purchaseList, this);
            recyclerView.setAdapter(recyclerAdapter);

            // get a Firebase DB instance reference
            database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("purchasedList");

            // Set up a listener (event handler) to receive a value for the database reference.
            // This type of listener is called by Firebase once by immediately executing its onDataChange method
            // and then each time the value at Firebase changes.
            //
            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain job leads.
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Once we have a DataSnapshot object, we need to iterate over the elements and place them on our shopping item list.
                    purchaseList.clear();
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                        Purchase purchase = postSnapshot.getValue(Purchase.class);
                        if (purchase != null) {
                            purchase.setKey(postSnapshot.getKey());
                            purchaseList.add(purchase);
                            Log.d(DEBUG_TAG, "ValueEventListener: added " + purchase);
                            Log.d(DEBUG_TAG, "ValueEventListener: key " + postSnapshot.getKey());
                        } //if
                    }

                    Log.d(DEBUG_TAG, "ValueEventListener: notifying recyclerAdapter");
                    recyclerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("ValueEventListener: reading failed: " + error.getMessage());
                }
            });
        } //onCreate

        // This is our own callback for a DialogFragment which edits an existing ShoppingItem.
        // The edit may be an update or a deletion of this ShoppingItem.
        // It is called from the EditShoppingItemDialogFragment.
        @Override
        public void updatePurchasePrice (int position, Purchase purchase) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("purchasedList")
                    .child(purchase.getKey());
            ref.setValue(purchase);
        } // updateShoppingItem



        @Override
        public void removePurchasedItem(int posiiton, Purchase purchase, int index) {
            if (purchase.getAllItems() == null || purchase.getAllItems().isEmpty()) {
                return;
            } //if

            ShoppingItem removedItem = purchase.getAllItems().get(index);
            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
            database1.getReference("shoppingList").push().setValue(removedItem);

            purchase.getAllItems().remove(index);

            DatabaseReference purchaseRef = database1.getReference()
                    .child("purchasedList")
                    .child(purchase.getKey());
            if (purchase.getAllItems().isEmpty()) {
                purchaseRef.removeValue();
            } else {
                purchaseRef.setValue(purchase);
            } //if-else
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // inflates the menu and adds the items to the action bar.
            getMenuInflater().inflate(R.menu.menu_options, menu);

            // hide add and checkout button
            MenuItem addItem = menu.findItem(R.id.add);
            MenuItem checkoutItem = menu.findItem(R.id.checkout);
            if (addItem != null && checkoutItem != null) {
                addItem.setVisible(false);
                checkoutItem.setVisible(false);
            }
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.add) {
                Toast.makeText(this, "Add Clicked", Toast.LENGTH_SHORT).show();
                DialogFragment dialogFragment = new AddShoppingItemDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), null);
                return true;
            } else if (item.getItemId() == R.id.logout) {
                // if clicked, the logout button will sign the user out and take them to the login page
                mAuth.getInstance().signOut();
                finish();
                return true;
            }   else if (item.getItemId() == R.id.help) {
                Toast.makeText(this, "Help Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return super.onOptionsItemSelected(item);
            }
        }
    }

