package edu.uga.cs.cs4060_final;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This is an adapter class for the RecyclerView to show all shopping items.
 */
public class ShoppingItemRecyclerAdapter extends RecyclerView.Adapter<ShoppingItemRecyclerAdapter.ShoppingItemHolder> {

   public static String DEBUG_TAG = "ShoppingItemRecyclerAdapter";

   private List<ShoppingItem> shoppingItemList;

   private Context context;

   public ShoppingItemRecyclerAdapter(List<ShoppingItem> shoppingItemList, Context context) {
       this.shoppingItemList = shoppingItemList;
       this.context = context;
   }

    // The adapter must have a ViewHolder class to "hold" one item to show.
    class ShoppingItemHolder extends RecyclerView.ViewHolder {

       TextView itemName;
       TextView quantity;

       public ShoppingItemHolder(View itemView) {
           super(itemView);

           itemName = itemView.findViewById(R.id.itemName);
           quantity = itemView.findViewById(R.id.quantity);

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
    }

    @Override
    public int getItemCount() {
        Log.d("ADAPTER_COUNT", "count = " + shoppingItemList.size());
       return shoppingItemList.size();
    }
}
