package edu.uga.cs.cs4060_final;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CheckoutDialogFragment extends DialogFragment {

    private EditText totalPriceEditText;

    public interface CheckoutDialogListener {
            void checkout(double totalPrice);
}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View layout = inflater.inflate(R.layout.checkout_dialog, requireActivity().findViewById(R.id.root));

        totalPriceEditText = layout.findViewById(R.id.editTextText6);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(layout);
        builder.setTitle("Checkout");

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("PURCHASE", (dialog, which) -> {
            String priceText = totalPriceEditText.getText().toString().trim();

            if (TextUtils.isEmpty(priceText)) {
                return;
            } //if

            double totalPrice = Double.parseDouble(priceText);
            CheckoutDialogListener listener = (CheckoutDialogListener) requireActivity();
            listener.checkout(totalPrice);
        });
        return builder.create();
    }
}
