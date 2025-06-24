package com.example.cctask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ASSIGNED_TO = "assigned_to";
    public static final String COLUMN_CREATED_BY = "created_by";
    public static final String COLUMN_DUE_DATE = "due_date";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                "role TEXT)";

        db.execSQL(createTable);

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_ASSIGNED_TO + " TEXT, " +
                COLUMN_CREATED_BY + " TEXT, " +
                COLUMN_DUE_DATE + " TEXT)";
        db.execSQL(createTasksTable);

        ContentValues admin = new ContentValues();
        admin.put(COLUMN_USERNAME, "admin");
        admin.put(COLUMN_PASSWORD, "1234");  // poți schimba parola
        admin.put("role", "admin");
        db.insert(TABLE_USERS, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Adaugăm coloana 'role' cu valoare implicită "user"
            db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN role TEXT DEFAULT 'user'");
        }
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
            String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_ASSIGNED_TO + " TEXT, " +
                    COLUMN_CREATED_BY + " TEXT, " +
                    COLUMN_DUE_DATE + " TEXT)";
            db.execSQL(createTasksTable);
        }
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        //onCreate(db);
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verifică dacă userul deja există
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return false; // user deja înregistrat
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put("role", "user"); // implicit user normal

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public String getUserRole(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{"role"},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    public boolean insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_ASSIGNED_TO, task.getAssignedTo());
        values.put(COLUMN_CREATED_BY, task.getCreatedBy());
        values.put(COLUMN_DUE_DATE, Task.dateFormat.format(task.getDataExpirare()));
        long result = db.insert(TABLE_TASKS, null, values);
        return result != -1;
    }

    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_TASKS, null, null, null, null, null, null);
    }

    public boolean deleteTask(String title, String createdBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS,
                COLUMN_TITLE + "=? AND " + COLUMN_CREATED_BY + "=?",
                new String[]{title, createdBy});
        return result > 0;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME, "role"},
                null, null, null, null, null);
    }

    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COLUMN_USERNAME + "=?", new String[]{username});
        return result > 0;
    }

}

