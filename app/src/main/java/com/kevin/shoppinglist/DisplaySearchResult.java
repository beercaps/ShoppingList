package com.kevin.shoppinglist;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

public class DisplaySearchResult extends AppCompatActivity {

    private final static  String LOG_TAG = DisplaySearchResult.class.getSimpleName();
    private ProductDataSource pds;
    private Bundle bundle;
    private List<Product> myProductList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_result);

        pds = new ProductDataSource(this);

        bundle = getIntent().getExtras();
        if (bundle.get(MainActivity.INTENT_EXTRA_SEARCH_RESULT) != null) {
            myProductList = (List<Product>) bundle.get(MainActivity.INTENT_EXTRA_SEARCH_RESULT);
            showAllListEntries(myProductList);
        }


        initializeContextualMenu();
    }

    private void showAllListEntries (List<Product> productList) {

        ArrayAdapter<Product> ProductArrayAdapter = new ArrayAdapter<> (
                this,
                android.R.layout.simple_list_item_multiple_choice,
                productList);

        ListView productListView = (ListView) findViewById(R.id.lv_searchResult);
        productListView.setAdapter(ProductArrayAdapter);
    }

    private void initializeContextualMenu(){
        final ListView ProductsListView = (ListView) findViewById(R.id.lv_searchResult);
        ProductsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        ProductsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int selCount = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    selCount++;
                } else {
                    selCount--;
                }
                String cabTitle = selCount + " " + getString(R.string.cab_checked_string);
                mode.setTitle(cabTitle);
                mode.invalidate();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                MenuItem item = menu.findItem(R.id.cab_modify);
                if (selCount == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                boolean returnValue = true;
                SparseBooleanArray touchedProductsPositions = ProductsListView.getCheckedItemPositions();

                switch (item.getItemId()) {
                    case R.id.cab_delete:
                        for (int i = 0; i < touchedProductsPositions.size(); i++) {
                            boolean isChecked = touchedProductsPositions.valueAt(i);
                            if(isChecked) {
                                int positionInListView = touchedProductsPositions.keyAt(i);
                                Product product = (Product) ProductsListView.getItemAtPosition(positionInListView);
                                pds.deleteProduct(product);

                            }
                        }
                        showAllListEntries(pds.getAllProducts());
                        mode.finish();
                        break;

                    case R.id.cab_modify:
                        Log.d(LOG_TAG, "Eintrag ändern");
                        for (int i = 0; i < touchedProductsPositions.size(); i++) {
                            boolean isChecked = touchedProductsPositions.valueAt(i);
                            if (isChecked) {
                                int positionInListView = touchedProductsPositions.keyAt(i);
                                Product product = (Product) ProductsListView.getItemAtPosition(positionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + positionInListView + " Inhalt: " + product.toString());

                                AlertDialog editShoppingMemoDialog = createEditShoppingMemoDialog(product);
                                editShoppingMemoDialog.show();
                            }
                        }

                        mode.finish();
                        break;

                    default:
                        returnValue = false;
                        break;
                }
                return returnValue;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selCount = 0;

            }
        });
    }

    private AlertDialog createEditShoppingMemoDialog(final Product product) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View dialogsView = inflater.inflate(R.layout.dialog_edit_shopping_list, null);

        final EditText editTextNewQuantity = (EditText) dialogsView.findViewById(R.id.editText_new_quantity);
        editTextNewQuantity.setText(String.valueOf(product.getQuantity()));

        final EditText editTextNewProduct = (EditText) dialogsView.findViewById(R.id.editText_new_product);
        editTextNewProduct.setText(product.getProductName());

        builder.setView(dialogsView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String quantityString = editTextNewQuantity.getText().toString();
                        String productText = editTextNewProduct.getText().toString();

                        if ((TextUtils.isEmpty(quantityString)) || (TextUtils.isEmpty(productText))) {
                            Log.d(LOG_TAG, "Ein Eintrag enthielt keinen Text. Daher Abbruch der Änderung.");
                            return;
                        }

                        int quantity = Integer.parseInt(quantityString);

                        // An dieser Stelle schreiben wir die geänderten Daten in die SQLite Datenbank
                        Product updatedProduct = pds.updateProduct(product.getId(), productText, quantity);

                        Log.d(LOG_TAG, "Alter Eintrag - ID: " + product.getId() + " Inhalt: " + product.toString());
                        Log.d(LOG_TAG, "Neuer Eintrag - ID: " + updatedProduct.getId() + " Inhalt: " + updatedProduct.toString());

                        showAllListEntries(pds.getAllProducts());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_button_negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}

