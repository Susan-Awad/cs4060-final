package edu.uga.cs.cs4060_final;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all shopping items.
 */
public class ShoppingBasketRecyclerAdapter extends RecyclerView.Adapter<ShoppingBasketRecyclerAdapter.ShoppingBasketHolder> {

    public static String DEBUG_TAG = "ShoppingBasketRecyclerAdapter";

    private List<ShoppingItem> shoppingItemList;

    private Context context;

    public ShoppingBasketRecyclerAdapter(List<ShoppingItem> shoppingItemList, Context context) {
        this.shoppingItemList = shoppingItemList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ShoppingBasketHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView quantity;
        Button purchased;
        Button remove;

        public ShoppingBasketHolder(View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName2);
            quantity = itemView.findViewById(R.id.quantity2);
            remove = itemView.findViewById(R.id.button12);
        }
    }

    @NonNull
    @Override
    public ShoppingBasketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_basket, parent, false);
        return new ShoppingBasketHolder(view);
    }

    // This method fills in the values of the Views to show a ShoppingItem
    @Override
    public void onBindViewHolder(@NonNull ShoppingBasketHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItemList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

        String key = shoppingItem.getKey();
        String item = shoppingItem.getItemName();
        String quantity = shoppingItem.getQuantity();

        holder.itemName.setText(shoppingItem.getItemName());
        holder.quantity.setText("Quantity: " + shoppingItem.getQuantity());

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditShoppingItemDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( DEBUG_TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                Log.d( DEBUG_TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );

                EditShoppingItemDialogFragment editShoppingFragment =
                        EditShoppingItemDialogFragment.newInstance(holder.getAdapterPosition(), key, item, quantity);
                editShoppingFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });


        holder.remove.setOnClickListener(new removeFromBasket(position));
    }

    private class purchased implements View.OnClickListener {
        private int pos;

        public purchased(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {


        }
    }

    // Removes the item from the shopping basket and places it back in the shopping list
    private class removeFromBasket implements View.OnClickListener {
        private int pos;

        public removeFromBasket(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            ShoppingItem shoppingItem = shoppingItemList.get(pos);
            Log.d(DEBUG_TAG, "onBindViewHolder: " + shoppingItem);

            String key = shoppingItem.getKey();

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
                            Toast.makeText(v.getContext(), "Shopping Item created for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();

                            // Clear teh EditTexts for next use.
                            shoppingItem.setItemName("");
                            shoppingItem.setQuantity("");

                        } //onSucess
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(v.getContext(), "Failed to create a shopping item for " + shoppingItem.getItemName(),
                                    Toast.LENGTH_SHORT).show();
                        } //onFailure
                    });

            // Delete the shopping item from the basket in Firebase.
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            DatabaseReference ref = database.getReference("shoppingBasket/" + user.getUid());
            ref = ref.child(key);

            // Remove the shopping item from the basket list
            shoppingItemList.remove( pos );

            // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
            // to maintain shopping items.
            ref.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( DEBUG_TAG, "Deleted shopping item at: " + pos );
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Log.d( DEBUG_TAG, "Failed to delete shopping item at: " + pos + "(" + shoppingItem.getItemName() + ")" );
                    Toast.makeText(v.getContext(), "Failed to delete " + shoppingItem.getItemName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("ADAPTER_COUNT", "count = " + shoppingItemList.size());
        return shoppingItemList.size();
    }
}
