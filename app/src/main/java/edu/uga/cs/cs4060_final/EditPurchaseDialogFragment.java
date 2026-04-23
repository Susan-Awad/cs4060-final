package edu.uga.cs.cs4060_final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Locale;

public class EditPurchaseDialogFragment extends DialogFragment {

    private TextView roommateEmailTextView;
    private TextView dateTextView;
    private EditText priceEditText;
    private TextView itemsTextView;

    private int position;
    private String key;
    private String roommateEmail;
    private double totalPrice;
    private String datePurchased;
    private ArrayList<ShoppingItem> allItems;



    // This interface will be used to obtain the new shopping item from an AlertDialog.
    // A class implementing this interface will handle the new shopping item, i.e. store it
    // in Firebase and add it to the RecyclerAdapter.
    interface EditPurchaseDialogListener {
        void updatePurchasePrice (int position, Purchase purchase);
        void removePurchasedItem(int position, Purchase purchase, int index);
    } //EditPurchaseDialogListener

    public static EditPurchaseDialogFragment newInstance(int position, String key, String roommateEmail, double totalPrice, String datePurchased, ArrayList<ShoppingItem> allItems) {
        EditPurchaseDialogFragment fragment = new EditPurchaseDialogFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("key", key);
        args.putString("roommateEmail", roommateEmail);
        args.putDouble("totalPrice", totalPrice);
        args.putString("datePurchased", datePurchased);
        args.putSerializable("allItems", allItems);
        fragment.setArguments(args);
        return fragment;
    }




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        
        // get the view objects in the AlertDialog
       if (getArguments() != null) {
           position = getArguments().getInt("position");
           key = getArguments().getString("key");
           roommateEmail = getArguments().getString("roommateEmail");
           totalPrice = getArguments().getDouble("totalPrice");
           datePurchased = getArguments().getString("datePurchased");
           allItems = (ArrayList<ShoppingItem>) getArguments().getSerializable("allItems");
       } //if

        if (allItems == null) {
            allItems = new ArrayList<>();
        } //if 

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.edit_purchase_dialog,requireActivity().findViewById(R.id.root));
        
        roommateEmailTextView = layout.findViewById(R.id.textView12);
        dateTextView = layout.findViewById(R.id.textView13);
        itemsTextView = layout.findViewById(R.id.textView15);
        priceEditText = layout.findViewById(R.id.editTextText5);
        
        roommateEmailTextView.setText("Buyer: " + roommateEmail);
        dateTextView.setText("Date: " + datePurchased);
        priceEditText.setText(String.format(Locale.US, "%.2f", totalPrice));
        itemsTextView.setText(buildItemsText(allItems));
        
        // create a new AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        // Set its view (inflated above)
        builder.setView(layout);
        // Set the title of the AlertDialog
        builder.setTitle("Edit Purchase");

        // Provide the negative button listener
        builder.setNegativeButton("CLOSE", (dialog, which) -> dialog.dismiss());

        // Provide the positive button listener
        builder.setPositiveButton("SAVE", new SaveBtnClickListener());
        
        // Provide the neutral button listener
        builder.setNeutralButton("REMOVE ITEM", null);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> { 
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                if (allItems == null || allItems.isEmpty()) {
                    return;
                } //if 
                removeItemChooser();
            });
        });
        return dialog;

    } //onCreateDialog

    private class SaveBtnClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String priceText = priceEditText.getText().toString().trim();

            if (TextUtils.isEmpty(priceText)) {
                return;
            } //if

            double newPrice = Double.parseDouble(priceText);

            Purchase purchase = new Purchase();
            purchase.setKey(key);
            purchase.setRoomateEmaill(roommateEmail);
            purchase.setTotalPrice(newPrice);
            purchase.setAllItems(allItems);

            EditPurchaseDialogListener listener = (EditPurchaseDialogListener)requireActivity();
            listener.updatePurchasePrice(position, purchase);
        }
    }
    private void removeItemChooser() {
        String [] labels = new String[allItems.size()];

        for (int i = 0; i < allItems.size(); i++) {
            ShoppingItem item = allItems.get(i);
            labels[i] = item.getItemName() + " (" + item.getQuantity() + ")";
        } //for

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Choose an item to remove.");
        builder.setItems(labels, (dialog, which) -> {
            Purchase purchase = new Purchase();
            purchase.setKey(key);
            purchase.setRoomateEmaill(roommateEmail);
            purchase.setTotalPrice(totalPrice);
            purchase.setAllItems(allItems);

            EditPurchaseDialogListener listener = (EditPurchaseDialogListener)requireActivity();
            listener.removePurchasedItem(position, purchase, which);
            dismiss();
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private String buildItemsText(ArrayList<ShoppingItem> allItems) {
        if (allItems == null || allItems.isEmpty()) {
            return "List is empty";
        } //if

        StringBuilder sb = new StringBuilder();
        for (ShoppingItem item : allItems) {
            sb.append(" • ")
                    .append(item.getItemName())
                    .append(" (")
                    .append(item.getQuantity())
                    .append(")");
        }
        return sb.toString().trim();
    }


}
