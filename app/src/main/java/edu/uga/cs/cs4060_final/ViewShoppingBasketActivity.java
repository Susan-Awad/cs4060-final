package edu.uga.cs.cs4060_final;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an activity to create a new shopping item.
 */
public class ViewShoppingBasketActivity extends AppCompatActivity
        implements AddShoppingItemDialogFragment.AddShoppingItemDialogListener,
        EditShoppingItemDialogFragment.EditShoppingItemDialogListener {

    public static final String DEBUG_TAG = "ViewShoppingBasketActivity";

    private RecyclerView recyclerView;
    private ShoppingBasketRecyclerAdapter recyclerAdapter;
    private List<ShoppingItem> shoppingItemList;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

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

        recyclerView = findViewById(R.id.recyclerView);
        TextView title = findViewById(R.id.textView7);
        title.setText("Shopping Basket");

        // initialize the Shopping Item List
        shoppingItemList = new ArrayList<ShoppingItem>();

        // use a linear layout manager for the recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // the recycler adapter with shopping items is empty at first; it will be updated later
        recyclerAdapter = new ShoppingBasketRecyclerAdapter(shoppingItemList, ViewShoppingBasketActivity.this);
        recyclerView.setAdapter(recyclerAdapter);

        // get a Firebase DB instance reference
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference myRef = database.getReference("shoppingBasket/" + user.getUid());
        Log.d(DEBUG_TAG, "user id: " + user.getUid());

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
                shoppingItemList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ShoppingItem shoppingItem = postSnapshot.getValue(ShoppingItem.class);
                    shoppingItem.setKey(postSnapshot.getKey());
                    shoppingItemList.add(shoppingItem);
                    Log.d(DEBUG_TAG, "ValueEventListener: added " + shoppingItem);
                    Log.d(DEBUG_TAG, "ValueEventListener: key " + postSnapshot.getKey());
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
    public void updateShoppingItem(int position, ShoppingItem shoppingItem, int action) {
        if (action == EditShoppingItemDialogFragment.SAVE) {
            Log.d(DEBUG_TAG, "Updating shopping item at: " + position + "(" + shoppingItem.getItemName() + ")");

            // Update the recycler view to show the changes in the updated shopping item in that view
            recyclerAdapter.notifyItemChanged(position);

            // Update this shopping item in Firebase
            DatabaseReference ref = database
                    .getReference()
                    .child("shoppingBasket/" + user.getUid())
                    .child(shoppingItem.getKey());

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain shopping items.
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getRef().setValue(shoppingItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(DEBUG_TAG, "Updated shopping item at: " + position + "(" + shoppingItem.getItemName() + ")");
                            Toast.makeText(getApplicationContext(), "Shopping item updated for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(DEBUG_TAG, "Failed to updated shopping item at: " + position + "(" + shoppingItem.getItemName() + ")");
                    Toast.makeText(getApplicationContext(), "Failed to update: " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if( action == EditShoppingItemDialogFragment.DELETE ) {
            Log.d( DEBUG_TAG, "Deleting job lead at: " + position + "(" + shoppingItem.getItemName() + ")" );

            // Remove the shopping item from the list
            shoppingItemList.remove( position );

            // Update the recycler view to remove the deleted item from that view
            recyclerAdapter.notifyItemRemoved( position );

            // Delete the shopping item in Firebase.
            DatabaseReference ref = database
                    .getReference()
                    .child( "shoppingBasket/" + user.getUid())
                    .child( shoppingItem.getKey() );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain shopping items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "Deleted shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );
                            Toast.makeText(getApplicationContext(), "Shopping item deleted for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "Failed to delete shopping item at: " + position + "(" + shoppingItem.getItemName() + ")" );
                    Toast.makeText(getApplicationContext(), "Failed to delete " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    } // updateShoppingItem

    // this is our own callback for a AddJobLeadDialogFragment which adds a new job lead.
    @Override
    public void addShoppingItem(ShoppingItem shoppingItem) {
        // add the new shopping item
        // Add a new element (ShoppingItem) to the list of shopping items in Firebase.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("shoppingBasket/" + user.getUid());

        // First, a call to push() appends a new node to the existing list (one is created
        // if this is done for the first time).  Then, we set the value in the newly created
        // list node to store the new shopping item.
        // This listener will be invoked asynchronously, as no need for an AsyncTask, as in
        // the previous apps to maintain shopping items.

        myRef.push().setValue(shoppingItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Reposition the RecyclerView to show the JobLead most recently added (as the last item on the list).
                        // Use of the post method is needed to wait until the RecyclerView is rendered, and only then
                        // reposition the item into view (show the last item on the list).
                        // the post method adds the argument (Runnable) to the message queue to be executed
                        // by Android on the main UI thread.  It will be done *after* the setAdapter call
                        // updates the list items, so the repositioning to the last item will take place
                        // on the complete list of items.
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(shoppingItemList.size()-1);
                            } // run
                        });

                        Log.d(DEBUG_TAG, "Shopping item saved: " + shoppingItem);
                        Toast.makeText(getApplicationContext(), "Shopping item created for " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to create shopping item: " + shoppingItem.getItemName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    } // addShoppingItem

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and adds the items to the action bar.
        getMenuInflater().inflate(R.menu.menu_options, menu);
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
        } else if (item.getItemId() == R.id.help) {
            Toast.makeText(this, "Help Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
