package com.example.android.inventoryappproject1;

/**
 * Created by Tea on 23.6.2018..
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappproject1.data.InventoryContract.InventoryEntry;

/**
 * {@link InventoryCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of Inventory data as its data source. This adapter knows
 * how to create list items for each row of Inventory data in the {@link Cursor}.
 */
public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {  super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.name);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.price);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button productSale = (Button)view.findViewById(R.id.sell_button);


        int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);

        final String nameProduct = getCursor().getString(productNameColumnIndex);
        final String priceProduct = getCursor().getString(priceColumnIndex);
        final String quantityProduct = getCursor().getString(quantityColumnIndex);

        productNameTextView.setText(nameProduct);
        productPriceTextView.setText(priceProduct);
        productQuantityTextView.setText(quantityProduct);

        final int idColumnIndex = getCursor().getInt(getCursor().getColumnIndex(InventoryEntry._ID));
        final int actualQuantityIndex = getCursor().getInt(getCursor().getColumnIndex(InventoryEntry.COLUMN_QUANTITY));
        final int actualQuantity = Integer.valueOf(getCursor().getString(actualQuantityIndex));

        productSale.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                if (actualQuantity > 0){

                    int newActualQuantity = actualQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_QUANTITY, newActualQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);

                    Toast.makeText(context, context.getString(R.string.sale_successful)+
                            nameProduct + context.getString(R.string.remaining_quantity) +
                            newActualQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.sale_error) +
                            nameProduct + context.getString(R.string.no_stock), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}