package com.kevin.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinwetzel on 20.05.16.
 */

public class ProductDataSource {

    private final static  String LOG_TAG = ProductDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ProductDbHelper productDbHelper;

    private String[] columns = {
            ProductDbHelper.C_ID,
            ProductDbHelper.C_NAME,
            ProductDbHelper.C_QUANTITY,
            ProductDbHelper.C_IS_CHECKED};

    public ProductDataSource(Context context) {
        Log.d(LOG_TAG, "ProductDataSource erzeugt den DBhelper");
        productDbHelper = new ProductDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = productDbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        productDbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }


    public Product createProduct(String productName, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ProductDbHelper.C_NAME, productName);
        values.put(ProductDbHelper.C_QUANTITY, quantity);

        long insertId = database.insert(ProductDbHelper.T_product, null, values);

        Cursor cursor = database.query(ProductDbHelper.T_product,
                columns, ProductDbHelper.C_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Product product = cursorToProduct(cursor);
        cursor.close();

        Log.d(LOG_TAG, "Product inserted: "+ product.toString());
        return product;
    }


    public Product searchProductID(int productID){
        Product product = null;
        if (productID != 0) {
            Cursor cursor = database.query(ProductDbHelper.T_product,
                    columns, ProductDbHelper.C_ID + "=" + productID,
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                cursor.moveToFirst();
                product = cursorToProduct(cursor);
                cursor.close();
                Log.d(LOG_TAG, "ID: " + product.getId() + ", Inhalt: " + product.toString());
            }
        }
        return product;
    }


    private Product cursorToProduct(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ProductDbHelper.C_ID);
        int idProduct = cursor.getColumnIndex(ProductDbHelper.C_NAME);
        int idQuantity = cursor.getColumnIndex(ProductDbHelper.C_QUANTITY);
        int idIsChecked = cursor.getColumnIndex(ProductDbHelper.C_IS_CHECKED);

        String productName = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idIsChecked);

        boolean isChecked = (intValueChecked != 0);

        Product product = new Product(id, productName, quantity, isChecked);

        return product;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        open();
        Cursor cursor = database.query(ProductDbHelper.T_product,
                columns, null, null, null, null, null);
        cursor.moveToFirst();
        Product product;

        while(!cursor.isAfterLast()) {
            product = cursorToProduct(cursor);
            productList.add(product);
            Log.d(LOG_TAG, "ID: " + product.getId() + ", Inhalt: " + product.toString());
            cursor.moveToNext();
        }

        cursor.close();
        close();

        return productList;
    }

    public void deleteProduct(Product product) {
        long id = product.getId();
        open();
        database.delete(ProductDbHelper.T_product,
                ProductDbHelper.C_ID + "=" + id,
                null);
        close();
        Log.d(LOG_TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + product.toString());
    }

    public Product updateProduct(long id, String newName, int newQuantity, boolean newChecked) {
        int intValueChecked = (newChecked) ? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ProductDbHelper.C_NAME, newName);
        values.put(ProductDbHelper.C_QUANTITY, newQuantity);
        values.put(ProductDbHelper.C_IS_CHECKED, newChecked);
        open();
        database.update(ProductDbHelper.T_product,
                values,
                ProductDbHelper.C_ID + "=" + id,
                null);


        Cursor cursor = database.query(ProductDbHelper.T_product,
                columns, ProductDbHelper.C_ID + "=" + id,
                null, null, null, null);

        cursor.moveToFirst();
        Product product = cursorToProduct(cursor);
        cursor.close();
        close();
        return product;
    }


}
