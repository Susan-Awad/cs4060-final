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
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {

   public static String DEBUG_TAG = "ShoppingItemRecyclerAdapter";
   private List<ShoppingItem> shoppingItemList;
   private ShoppingItem shoppingItem;
    private Context context;

   public ShoppingItemRecyclerAdapter(List<ShoppingItem> shoppingItemList, Context context) {
       this.shoppingItemList = shoppingItemList;
       this.context = context;
   }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ShoppingItemHolder extends RecyclerView.ViewHolder {
       TextView itemName;
       TextView quantity;
       Button basket;

       public ShoppingItemHolder(View itemView) {
           super(itemView);

           itemName = itemView.findViewById(R.id.itemName);
           quantity = itemView.findViewById(R.id.quantity);
           basket = itemView.findViewById(R.id.button10);
       }
   }

    @NonNull
    @Override
    public ShoppingItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_item, parent, false);
        return new ShoppingItemHolder(view);
    }

    // This method fills in the values of the Views to show a ShoppingItem
    @Override
    public void onBindViewHolder(@NonNull ShoppingItemHolder holder, int position) {
        shoppingItem = shoppingItemList.get(position);

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

        holder.basket.setOnClickListener(new addToBasket(position));
    }

    private class addToBasket implements View.OnClickListener {
       private int pos;
       public addToBasket(int pos) {
           this.pos = pos;
       }

       @Override
       public void onClick (View v) {
           shoppingItem = shoppingItemList.get(pos);

           String key = shoppingItem.getKey();
           String item = shoppingItem.getItemName();
           String quantity = shoppingItem.getQuantity();

           final ShoppingItem shoppingItem = new ShoppingItem(item, quantity);

           // Add a new element (ShoppingItem) to the list of shopping items in basket in Firebase.
           FirebaseDatabase database = FirebaseDatabase.getInstance();
           FirebaseAuth mAuth = FirebaseAuth.getInstance();
           FirebaseUser user = mAuth.getCurrentUser();
           DatabaseReference myRef = database.getReference("shoppingBasket/" + user.getUid());

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

           // Delete the shopping item in Firebase.
           DatabaseReference ref = database.getReference("shoppingList");
           ref = ref.child(key);

           // Remove the shopping item from the list
           shoppingItemList.remove( pos );

           // This listener will be invoked asynchronously, hence no need for an AsyncTask class, as in the previous apps
           // to maintain shopping items.
           ref.addListenerForSingleValueEvent( new ValueEventListener() {
               @Override
               public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                   dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Log.d(DEBUG_TAG, "Deleted shopping item at: " + pos);
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
