package edu.uga.cs.cs4060_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenu extends AppCompatActivity {

    private static final String TAG = "MainMenu";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        Button addNewItemBtn = findViewById(R.id.button5);
        Button shoppingListBtn = findViewById(R.id.button4);
        Button shoppingBasketBtn = findViewById(R.id.button6);
        Button purchasedItemsBtn = findViewById(R.id.button7);
        Button settleCostBtn = findViewById(R.id.button8);

        addNewItemBtn.setOnClickListener(new AddNewItemBtnClickListener());
        shoppingListBtn.setOnClickListener(new ShoppingListBtnClickListener());
        shoppingBasketBtn.setOnClickListener(new ShoppingBasketBtnClickListener());
        purchasedItemsBtn.setOnClickListener(new PurchasedItemsBtnClickListener());
        settleCostBtn.setOnClickListener(new SettleCostBtnClickListener());
        mAuth = FirebaseAuth.getInstance();

        mAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if( currentUser != null ) {
                    // if user is signed in
                    String userEmail = currentUser.getEmail();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" +
                            currentUser.getUid() + " email: " + userEmail);
                } else {
                    // if user is signed out
                    Log.d(TAG, "User is not signed in" );
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflates the menu and adds the items to the action bar.
        getMenuInflater().inflate(R.menu.menu_options, menu);

        // hide checkout button
        MenuItem addItem = menu.findItem(R.id.checkout);
        if (addItem != null) {
            addItem.setVisible(false);
        }
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

    private class AddNewItemBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), NewShoppingItemActivity.class);
            startActivity(intent);
        } // onClick
    } // AddNewItemBtnClickListener


    private class ShoppingListBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewShoppingListActivity.class);
            startActivity(intent);
        } // onClick
    } // ShoppingListBtnClickListener

    private class ShoppingBasketBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewShoppingBasketActivity.class);
            startActivity(intent);
        } // onClick
    } // ShoppingBasketBtnClickListener

    private class PurchasedItemsBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), ViewPurchasedListActivity.class);
            startActivity(intent);
        } // onClick
    } // PurchasedItemsBtnClickListener

    private class SettleCostBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), SettleCostActivity.class);
            startActivity(intent);
        }
    }
}
