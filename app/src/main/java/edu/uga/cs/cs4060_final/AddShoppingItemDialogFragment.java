package edu.uga.cs.cs4060_final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddShoppingItemDialogFragment extends DialogFragment{

        private EditText itemNameView;
        private EditText quantityView;

    // This interface will be used to obtain the new shopping item from an AlertDialog.
    // A class implementing this interface will handle the new shopping item, i.e. store it
    // in Firebase and add it to the RecyclerAdapter.
        public interface AddShoppingItemDialogListener {
            void addShoppingItem(ShoppingItem shoppingItem);
        } //AddShoppingItemDialogListener


        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.add_shopping_item_dialog, getActivity().findViewById(R.id.root));

            // get the view objects in the AlertDialog
            itemNameView = layout.findViewById(R.id.editTextText);
            quantityView = layout.findViewById(R.id.editTextText2);

            // create a new AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set its view (inflated above)
            builder .setView(layout);

            // Set the title of the AlertDialog
            builder.setTitle("New Shopping Item");

            // Provide the negative button listener
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // close the dialog
                    dialogInterface.dismiss();
                }
            });

            // Provide the positive button listener
            builder.setPositiveButton(android.R.string.ok, new AddShoppingItemListener());


            // Create the AlertDialog and show it
            return builder.create();

        } //onCreateDialog

    private class AddShoppingItemListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // get the shopping item data from the user
            String itemName = itemNameView.getText().toString();
            String quantity = quantityView.getText().toString();

            // create a new ShoppingItem object
            ShoppingItem shoppingItem = new ShoppingItem(itemName, quantity);

            // get the activity's listener to add the new job lead
            AddShoppingItemDialogListener listener = (AddShoppingItemDialogListener) getActivity();

            // add the new shopping item
            listener.addShoppingItem(shoppingItem);
            //close the dialog
            dismiss();
        }
    }


}
