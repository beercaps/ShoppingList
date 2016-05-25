package com.kevin.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String INTENT_EXTRA_SEARCH_RESULT = "com.example.kevinwetzel.shoppinglist.MainActivity:SearchResult";

    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private ProductDataSource pds;

    private EditText et_id;
    private EditText et_quantity;
    private EditText et_productName;
    private EditText et_searchID;
    private Button bt_search;
    //private Button   bt_save;
    private Button   bt_clear;
    private Button   bt_showAll;
    private FloatingActionButton fab_save;
    private ArrayList<EditText> editTextList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab_save = (FloatingActionButton) findViewById(R.id.bt_save);
        fab_save.setOnClickListener(this);



        editTextList = new ArrayList<EditText>();

        et_id = (EditText) findViewById(R.id.et_id);
        et_quantity = (EditText) findViewById(R.id.et_quantity);
        et_productName = (EditText) findViewById(R.id.et_productname);
        et_searchID = (EditText) findViewById(R.id.et_searchID);
       // bt_save = (Button) findViewById(R.id.bt_save);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_clear = (Button) findViewById(R.id.bt_Clear);
        bt_showAll = (Button) findViewById(R.id.bt_showAll);

        //bt_save.setOnClickListener(this);
        bt_search.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_showAll.setOnClickListener(this);

        editTextList.add(et_quantity);
        editTextList.add(et_id);
        editTextList.add(et_productName);
        editTextList.add(et_searchID);


        pds = new ProductDataSource(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_save){

            if (!(String.valueOf(et_productName.getText()).equals("")&&String.valueOf(et_quantity.getText()).equals(""))) {
                String productName = String.valueOf(et_productName.getText());
                int quantity = Integer.parseInt(String.valueOf(et_quantity.getText()));

                pds.open();
                Product product = pds.createProduct(productName, quantity);
                pds.close();
            }else {
                Snackbar.make(view, "Product Name and Quantity must not be empty!", Snackbar.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Snackbar executed");
            }
        }else if(view.getId() == R.id.bt_search){

            if (String.valueOf(et_searchID.getText()).equals("")){
                Snackbar.make(view, "Please enter an ID first!", Snackbar.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Snackbar executed");
            }else {
                int id = Integer.parseInt(String.valueOf(et_searchID.getText()));
                Log.d(LOG_TAG, "ID equals " + id);
                pds.open();
                Product searchResult = pds.searchProductID(id);
                if (searchResult == null) {
                    Snackbar.make(view, "Couldn't find a product with this ID", Snackbar.LENGTH_LONG).show();
                }else{
                    fillEditText(searchResult);
                }
                pds.close();
            }

        }
        else if(view.getId() == R.id.bt_Clear){
            clearEditText(editTextList);
        }
        else if (view.getId() == R.id.bt_showAll){
            Log.d(LOG_TAG, "onClick: btShowAll geklickt");
            ArrayList<Product> productList = new ArrayList<>(pds.getAllProducts());
            Intent intent = new Intent(this, DisplaySearchResult.class);
            intent.putExtra(MainActivity.INTENT_EXTRA_SEARCH_RESULT, productList);
            startActivity(intent);
        }
    }

    private void fillEditText(Product p){
        et_id.setText(String.valueOf(p.getId()));
        et_productName.setText(String.valueOf(p.getProductName()));
        et_quantity.setText(String.valueOf(p.getQuantity()));
    }

    private  void clearEditText(List<EditText> editTexts){
        for (int i = 0; i< editTexts.size(); i++){
            editTexts.get(i).setText("");
        }
    }


}
