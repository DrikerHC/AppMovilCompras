package com.example.appmovilescompras;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "listas.db";
    private static final int DATABASE_VERSION = 1;

    // --- TABLA DE LISTAS ---
    private static final String TABLE_LISTAS = "listas";
    private static final String COL_ID = "id";
    private static final String COL_NOMBRE = "nombre";
    private static final String COL_FECHA = "fecha";
    private static final String COL_IMAGEN = "imagen"; // URI o path local

    // --- TABLA DE PRODUCTOS ---
    private static final String TABLE_PRODUCTOS = "productos";
    private static final String COL_PRODUCTO_ID = "id";
    private static final String COL_PRODUCTO_LISTA_ID = "lista_id";
    private static final String COL_PRODUCTO_NOMBRE = "nombre";
    private static final String COL_PRODUCTO_CANTIDAD = "cantidad";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createListas = "CREATE TABLE " + TABLE_LISTAS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NOMBRE + " TEXT," +
                COL_FECHA + " TEXT," +
                COL_IMAGEN + " TEXT)";
        db.execSQL(createListas);

        String createProductos = "CREATE TABLE " + TABLE_PRODUCTOS + " (" +
                COL_PRODUCTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_PRODUCTO_LISTA_ID + " INTEGER," +
                COL_PRODUCTO_NOMBRE + " TEXT," +
                COL_PRODUCTO_CANTIDAD + " INTEGER)";
        db.execSQL(createProductos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
        onCreate(db);
    }

    // ---------------- LISTAS ----------------

    public long addLista(String nombre, String fecha, String imagen) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOMBRE, nombre);
        cv.put(COL_FECHA, fecha);
        cv.put(COL_IMAGEN, imagen);
        return db.insert(TABLE_LISTAS, null, cv); // Retorna long
    }

    public List<Compra> getAllListas() {
        List<Compra> listas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_LISTAS, null, null, null, null, null, COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN));
                Compra c = new Compra(nombre, fecha, imagen);
                c.setId(id); // Compra debe tener setId(long id)
                listas.add(c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listas;
    }

    // ---------------- PRODUCTOS ----------------

    public long addProducto(long listaId, String nombre, int cantidad) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PRODUCTO_LISTA_ID, listaId);
        cv.put(COL_PRODUCTO_NOMBRE, nombre);
        cv.put(COL_PRODUCTO_CANTIDAD, cantidad);
        return db.insert(TABLE_PRODUCTOS, null, cv); // Retorna long
    }

    public List<Producto> getProductosPorLista(long listaId) {
        List<Producto> productos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTOS, null,
                COL_PRODUCTO_LISTA_ID + "=?",
                new String[]{String.valueOf(listaId)}, null, null, COL_PRODUCTO_ID + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PRODUCTO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCTO_NOMBRE));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCTO_CANTIDAD));
                productos.add(new Producto((int)id, nombre, cantidad)); // Producto sigue usando int id
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productos;
    }
    public boolean deleteLista(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int resultado = db.delete("listas", "id = ?", new String[]{String.valueOf(id)});
        return resultado > 0;
    }

    public void deleteProducto(int productoId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PRODUCTOS, COL_PRODUCTO_ID + "=?", new String[]{String.valueOf(productoId)});
    }
}