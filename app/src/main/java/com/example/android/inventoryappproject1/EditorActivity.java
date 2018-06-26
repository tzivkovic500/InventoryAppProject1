package com.example.android.inventoryappproject1;

/**
 * Created by Tea on 7.6.2018..
 */
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryappproject1.data.InventoryContract.InventoryEntry;

/**
 * Allows user to create a new item or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader
     */
    private static final int EXISTING_INVENTORY_LOADER = 0;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentInventoryUri;
    /**
     * EditText field to enter the product name
     */
    private EditText productNameEditText;
    /**
     * EditText field to enter the price
     */
    private EditText priceEditText;
    /**
     * EditText field to enter the quantity
     */
    private TextView quantityTextView;
    /**
     * EditText field to enter the supplier name
     */
    private EditText supplierNameEditText;
    /**
     * EditText field to enter the supplier phone number
     */
    private EditText supplierPhoneNumberEditText;
    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean mInventoryHasChanged = false;
    private int quantity;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mInventoryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentInventoryUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        productNameEditText = (EditText) findViewById(R.id.product_name);
        priceEditText = (EditText) findViewById(R.id.price);
        quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        supplierNameEditText = (EditText) findViewById(R.id.supplier_name);
        supplierPhoneNumberEditText = (EditText) findViewById(R.id.supplier_phone);
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
       productNameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        quantityTextView.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneNumberEditText.setOnTouchListener(mTouchListener);

        View productDecreaseButton = findViewById (R.id.decrease_button);
        productDecreaseButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                decreament (quantityTextView);
            }
        });

        View productIncreaseButton = findViewById (R.id.increase_button);
        productIncreaseButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                increament (quantityTextView);
            }
        });
}
/**
     * Get user input from editor and save new product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String productNameString = productNameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String quantityString = quantityTextView.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = supplierPhoneNumberEditText.getText().toString().trim();
        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(productNameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneNumberString)) {
            Toast.makeText(this, "All fields are requaired", Toast.LENGTH_SHORT).show();
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);
        // Determine if this is a new or existing product by checking if mCurrentInventoryUri is null or not
        if (mCurrentInventoryUri == null) {
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentInventoryUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentInventoryUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the Inventory table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String cproductName = cursor.getString(productNameColumnIndex);
            int cprice = cursor.getInt(priceColumnIndex);
            int cquantity = cursor.getInt(quantityColumnIndex);
            String csupplierName = cursor.getString(supplierNameColumnIndex);
            String csupplierPhoneNumber = cursor.getString(supplierPhoneNumberColumnIndex);

            // Update the views on the screen with the values from the database
            productNameEditText.setText(cproductName);
            priceEditText.setText(Integer.toString(cprice));
            quantityTextView.setText(Integer.toString(cquantity));
            supplierNameEditText.setText(csupplierName);
            supplierPhoneNumberEditText.setText(csupplierPhoneNumber);


        }
    }
    public void increament(View view) {
        quantity = Integer.parseInt(quantityTextView.getText().toString());
        quantity = quantity + 1;
        display(quantity);
    }

    public void decreament(View view) {

        quantity = Integer.parseInt(quantityTextView.getText().toString());
        if (quantity > 0) {
            quantity = quantity - 1;
            display(quantity);
        } else {
            return;
        }
    }
    private void display(int number) {
        quantityTextView.setText("" + number);
    }

        @Override
        public void onLoaderReset(Loader<Cursor> loader){
        // If the loader is invalidated, clear out all the data from the input fields.
        productNameEditText.setText("");
        priceEditText.setText("");
        quantityTextView.setText("");
        supplierNameEditText.setText("");
        supplierPhoneNumberEditText.setText("");
        }

      /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
    * if they continue leaving the editor.
     *
    * @param discardButtonClickListener is the click listener for what to do when
    * the user confirms they want to discard their changes
 */
       private void showUnsavedChangesDialog(
        DialogInterface.OnClickListener discardButtonClickListener){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing,new DialogInterface.OnClickListener(){
       public void onClick(DialogInterface dialog,int id){
        // User clicked the "Keep editing" button, so dismiss the dialog
        // and continue editing the pet.
        if(dialog!=null){
        dialog.dismiss();
        }
        }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        }

    /**
    * Prompt the user to confirm that they want to delete this product.
    */
     private void showDeleteConfirmationDialog(){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete,new DialogInterface.OnClickListener(){
      public void onClick(DialogInterface dialog,int id){
        // User clicked the "Delete" button, so delete the product.
        deleteProduct();
        }
        });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
      public void onClick(DialogInterface dialog,int id){
        // User clicked the "Cancel" button, so dismiss the dialog
        // and continue editing the product.
        if(dialog!=null){
        dialog.dismiss();
        }
        }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
        }
     /**
     *  Perform the deletion of the product in the database.
     */
      private void deleteProduct(){
        // Only perform the delete if this is an existing product.
        if(mCurrentInventoryUri!=null){
        // Call the ContentResolver to delete the product at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentInventoryUri
        // content URI already identifies the product that we want.
        int rowsDeleted=getContentResolver().delete(mCurrentInventoryUri,null,null);

        // Show a toast message depending on whether or not the delete was successful.
        if(rowsDeleted==0){
        // If no rows were deleted, then there was an error with the delete.
        Toast.makeText(this,getString(R.string.editor_delete_product_failed),
        Toast.LENGTH_SHORT).show();
        }else{
        // Otherwise, the delete was successful and we can display a toast.
        Toast.makeText(this,getString(R.string.editor_delete_product_successful),
        Toast.LENGTH_SHORT).show();
        }
        }

        // Close the activity
        finish();
        }
        }







