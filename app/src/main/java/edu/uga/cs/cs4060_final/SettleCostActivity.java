package edu.uga.cs.cs4060_final;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettleCostActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "SettleCostActivity";
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<Purchase> purchasedItemsList;
    private TextView totalByAll;
    private TextView eachRoommateCost;
    private TextView perRoommateCost;
    private TextView avgDifference;
    private Button settle;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(DEBUG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settle_cost);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        totalByAll = findViewById(R.id.totalOfPurchases);
        eachRoommateCost = findViewById(R.id.eachSpending);
        perRoommateCost = findViewById(R.id.averagePerRoommate);
        avgDifference = findViewById(R.id.difference);
        settle = findViewById(R.id.button11);

        purchasedItemsList = new ArrayList<Purchase>();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference("purchasedList");
        Log.d(DEBUG_TAG, "user id: " + user.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Purchase purchasedItems = postSnapshot.getValue(Purchase.class);
                    purchasedItems.setKey(postSnapshot.getKey());
                    purchasedItemsList.add(purchasedItems);
                    Log.d(DEBUG_TAG, "ValueEventListener: added " + purchasedItems);
                    Log.d(DEBUG_TAG, "ValueEventListener: key " + postSnapshot.getKey());
                }
                getStatistics();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("ValueEventListener: reading failed: " + error.getMessage());
            }
        });

        settle.setOnClickListener(new SettleClickListener());
    }

    public void getStatistics() {
        double totalCost = 0.00;
        Map<String, Double> costByRoommate = new HashMap<>();
        for (Purchase purchase : purchasedItemsList) {
            totalCost += purchase.getTotalPrice();
            Log.d(DEBUG_TAG, "purchase: " + purchase);
            String email = purchase.getRoommateEmaill();
            double cost = purchase.getTotalPrice();

            if (costByRoommate.containsKey(email)) {
                double newCost = costByRoommate.get(email) + cost;
                costByRoommate.put(email, newCost);
            } else {
                costByRoommate.put(email, cost);
            }
        }

        double avgPerRoommate = totalCost != 0 ? totalCost / costByRoommate.size() : 0.00;
        double difference = costByRoommate.get(user.getEmail()) != null ?
                (avgPerRoommate != 0 ?
                        avgPerRoommate - costByRoommate.get(user.getEmail()): 0.00) : 0 - avgPerRoommate;
        StringBuilder perSpendingString = new StringBuilder();
        costByRoommate.forEach( (key, value) -> {
            perSpendingString.append(key + "\n\t\t\tTotal Paid: $" + String.valueOf(value) + "\n");
        });

        totalByAll.setText("$" + String.format("%.2f", totalCost));
        eachRoommateCost.setText(perSpendingString);
        perRoommateCost.setText("$" + String.format("%.2f", avgPerRoommate));
        avgDifference.setText("$" + String.format("%.2f", difference));
    } // getStatistics

    private class SettleClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            DatabaseReference myRef = database.getReference("purchasedList");
            myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    totalByAll.setText("$0.00");
                    eachRoommateCost.setText("Make a purchase!");
                    perRoommateCost.setText("$0.00");
                    avgDifference.setText("$0.00");
                    purchasedItemsList.clear();

                    Toast.makeText(v.getContext(), "You settled the cost!",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(v.getContext(), "Could not settle the cost: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and adds the items to the action bar.
        getMenuInflater().inflate(R.menu.menu_options, menu);

        // hide add button and checkout button
        MenuItem addItem = menu.findItem(R.id.add);
        MenuItem checkoutItem = menu.findItem(R.id.checkout);
        if (addItem != null) {
            addItem.setVisible(false);
        }
        if (checkoutItem != null) {
            checkoutItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // if clicked, the logout button will sign the user out and take them to the login page
            mAuth.getInstance().signOut();
            finish();
            return true;
        } else if (item.getItemId() == R.id.help) {
            Toast.makeText(this, "Help Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
