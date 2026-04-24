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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is an adapter class for the RecyclerView to show all the purchased shopping items.
 */
public class PurchaseRecyclerAdapter extends RecyclerView.Adapter<PurchaseRecyclerAdapter.PurchaseHolder> {

    public static String DEBUG_TAG = "PurchasedRecyclerAdapter";

    private List<Purchase> purchaseList;

    private Context context;

    public PurchaseRecyclerAdapter(List<Purchase> purchaseList, Context context) {
        this.purchaseList = purchaseList;
        this.context = context;
    }

    // The adapter must have a ViewHolder class to "hold" one purchase to show.
    class PurchaseHolder extends RecyclerView.ViewHolder {
        TextView roommateEmail;
        TextView totalPrice;
        TextView datePurchased;
        TextView allItems;

        public PurchaseHolder(View itemView) {
            super(itemView);
            roommateEmail = itemView.findViewById(R.id.textView8);
            totalPrice = itemView.findViewById(R.id.textView9);
            datePurchased = itemView.findViewById(R.id.textView10);
            allItems = itemView.findViewById(R.id.textView11);
        }
    }

    @NonNull
    @Override
    public PurchaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_items, parent, false);
        return new PurchaseHolder(view);
    }

    // This method fills in the values of the Views to show a Purchase
    @Override
    public void onBindViewHolder(@NonNull PurchaseHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);

        Log.d(DEBUG_TAG, "onBindViewHolder: " + purchase);

        holder.roommateEmail.setText("Purchased by: " + purchase.getRoommateEmaill());
        holder.totalPrice.setText(String.format(Locale.US, "Total Price (with tax): $%.2f", purchase.getTotalPrice()));
        holder.datePurchased.setText("Date: " + purchase.getDatePurchased());
        holder.allItems.setText("Items: " + buildItemsList(purchase.getAllItems()));

        // We can attach an OnClickListener to the itemView of the holder;
        // itemView is a public field in the Holder class.
        // It will be called when the user taps/clicks on the whole item, i.e., one of
        // the job leads shown.
        // This will indicate that the user wishes to edit (modify or delete) this item.
        // We create and show an EditpurchaseDialogFragment.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( DEBUG_TAG, "onBindViewHolder: getItemId: " + holder.getItemId() );
                Log.d( DEBUG_TAG, "onBindViewHolder: getAdapterPosition: " + holder.getAdapterPosition() );

                EditPurchaseDialogFragment purchaseDialogFragment =
                        EditPurchaseDialogFragment.newInstance(holder.getAdapterPosition(),
                                purchase.getKey(),
                                purchase.getRoommateEmaill(),
                                purchase.getTotalPrice(),
                                purchase.getDatePurchased(),
                                new ArrayList<>(purchase.getAllItems()));
                purchaseDialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), null);

            }

        });
    }

    //method to build the list of items in the holder
    private String buildItemsList(List<ShoppingItem> allItems) {
        if (allItems == null || allItems.isEmpty()) {
            return "List Empty.";
        } //if

        StringBuilder sb = new StringBuilder();
        for (ShoppingItem items: allItems) {
            sb.append(items.getItemName())
                    .append(" (")
                    .append(items.getQuantity())
                    .append("), ");
        } //for
        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public int getItemCount() {
        Log.d("ADAPTER_COUNT", "count = " + purchaseList.size());
        return purchaseList.size();
    }
}