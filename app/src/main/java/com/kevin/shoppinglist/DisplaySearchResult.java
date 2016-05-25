package com.kevin.shoppinglist;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
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

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.cab_delete:
                        SparseBooleanArray touchedShoppingMemosPositions = ProductsListView.getCheckedItemPositions();
                        for (int i=0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if(isChecked) {
                                int positionInListView = touchedShoppingMemosPositions.keyAt(i);
                                Product product = (Product) ProductsListView.getItemAtPosition(positionInListView);
                                pds.deleteProduct(product);

                            }
                        }
                        showAllListEntries(pds.getAllProducts());
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }
}
