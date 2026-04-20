package edu.uga.cs.cs4060_final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

// This is a DialogFragment to handle edits to a ShoppingItem.
public class EditShoppingItemDialogFragment extends DialogFragment {

    private static final String TAG = "EditShoppingItemDialogFragment";
    public static final int SAVE = 1; //update existing shopping item
    public static final int DELETE = 2; //delete an existing job lead

    private EditText itemNameView;
    private EditText quantityView;

    int position; // the position of the edited shopping item on the list of shopping items

    String key;
    String item;
    String quantity;

    // A callback listener interface to finish up the editing of a ShoppingItem.
    // ReviewJobLeadsActivity implements this listener interface, as it will
    // need to update the list of ShoppingItems and also update the RecyclerAdapter to reflect the
    // changes.
    public interface EditShoppingItemDialogListener {
        void updateShoppingItem(int position, ShoppingItem shoppingItem, int action);
    } //EditShoppingItemDialogListener

    public static EditShoppingItemDialogFragment newInstance(int position, String key, String item, String quantity) {
        EditShoppingItemDialogFragment dialog = new EditShoppingItemDialogFragment();

        // Supply shopping item values as an argument
        Bundle args = new Bundle();
        args.putString("key", key);
        args.putInt("position", position);
        args.putString("item", item);
        args.putString("quantity", quantity);
        dialog.setArguments(args);

        return dialog;
    } //newInstance

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        key = getArguments().getString("key");
        position = getArguments().getInt("positon");
        item = getArguments().getString("item");
        quantity = getArguments().getString("quantity");

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_shopping_item_dialog, getActivity().findViewById(R.id.root));

        itemNameView = layout.findViewById(R.id.editTextText);
        quantityView = layout.findViewById(R.id.editTextText2);

        // Pre-fill the edit texts with the current values for this shopping item.
        // The user will be able to modify them.
        itemNameView.setText(item);
        quantityView.setText(quantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(layout);
        // Set the title of the AlertDialog
        builder.setTitle("Edit Shopping Item");

        // The cancel button handler
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // The Save button handler
        builder.setPositiveButton("SAVE", new SaveButtonClickListener());

        // The Delete button handler
        builder.setNeutralButton("DELETE", new DeleteButtonClickListener());

        // Create the AlertDialog and show it
        return builder.create();

    } //onCreateDialog

    private class SaveButtonClickListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String itemName = itemNameView.getText().toString();
            String quantity = quantityView.getText().toString();
            ShoppingItem shoppingItem = new ShoppingItem(itemName, quantity);
            shoppingItem.setKey(key);

            // get the Activity's listener to add the new shopping item
            EditShoppingItemDialogListener listener = (EditShoppingItemDialogListener) getActivity();
            // add the new shopping item
            listener.updateShoppingItem(position, shoppingItem, SAVE);

            //close the dialog
            dismiss();
        } //onClick
    } //SaveButtonClickListener

    private class DeleteButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            ShoppingItem shoppingItem = new ShoppingItem(item, quantity);
            shoppingItem.setKey(key);

            // get the Activity's listener to add the new shopping item
            EditShoppingItemDialogListener listener = (EditShoppingItemDialogListener) getActivity();
            // add the new shopping item
            Log.d(TAG, "In delete:" + shoppingItem);
            listener.updateShoppingItem(position, shoppingItem, DELETE);

            //close the dialog
            dismiss();
        } //onClick
    }

}