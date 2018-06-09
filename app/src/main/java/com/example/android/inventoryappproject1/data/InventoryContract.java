package com.example.android.inventoryappproject1.data;

/**
 * Created by Tea on 7.6.2018..
 */

import android.provider.BaseColumns;

public final class InventoryContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    /**
     * Inner class that defines constant values for the product database table.
     * Each entry in the table represents a single product.
     */
    public static final class InventoryEntry implements BaseColumns {
        /**
         * Name of database table for product
         */
        public static final String TABLE_NAME = "inventory";
        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Product name.
         *
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "productName";
        /**
         * Price.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_PRICE = "price";
        /**
         * Quantity.
         *
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY = "quantity";
        /**
         * supplier name.
         *
         * * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";
        /**
         * supplier phone number.
         *
         * * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";

    }
}
