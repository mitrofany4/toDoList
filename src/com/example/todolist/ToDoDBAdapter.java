package com.example.todolist;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class ToDoDBAdapter {
	
	private static final String DATABASE_NAME = "LIST.db";
	private static final String DATABASE_TABLE = "todoItems";
	private static final int DATABASE_VERSION = 1;
	
	private SQLiteDatabase db;
	private final Context context;
	
	public static final String KEY_ID = "_id";
	public static final String KEY_TASK = "task";
	public static final String KEY_CREATION_DATE = "creation_date";
	
	private ToDoDBOpenHelper dbHelper;
	
	public class ToDoDBOpenHelper extends SQLiteOpenHelper{

		public ToDoDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
			// TODO Auto-generated constructor stub
			super(context, name, factory, version);
		}
		
		// ����������� �� ����� SQL ��� �������� ����� ���� ������.
		private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" + KEY_ID + 
		" integer primary key autoincrement, " + 	KEY_TASK + " text not null, " + KEY_CREATION_DATE + " long);";
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			Log.w("TaskDBAdapter", "Upgrading from version " +_oldVersion + " to " +_newVersion + 
																		", which will destroy all old data");
			// ������� ������ �������.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			
			// �������� �����.
			onCreate(_db);
		}
	}
	
	public ToDoDBAdapter(Context _context) {
		// TODO Auto-generated constructor stub
		this.context=_context;
		dbHelper = new ToDoDBOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION);
	}
	
	public void close() {
		db.close();
	}
	
	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
	}
	
	// ������� ������ ��������
	public long insertTask(ToDoItem _task) {
	
		// �������� ����� ������ �� ����������, ������� ����� ��������.
		ContentValues newTaskValues = new ContentValues();
	
		// ��������� �������� ��� ������ ������.
		newTaskValues.put(KEY_TASK, _task.getTask());
		newTaskValues.put(KEY_CREATION_DATE, _task.getCreated().getTime());
	
		// �������� ������.
		return db.insert(DATABASE_TABLE, null, newTaskValues);
	}
	
	// �������� �������� � ���������� ��������
	public boolean removeTask(long _rowIndex) {
			return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
	}
	
	// ���������� ��������
	public boolean updateTask(long _rowIndex, String _task) {
		ContentValues newValue = new ContentValues();
		newValue.put(KEY_TASK, _task);
		return db.update(DATABASE_TABLE, newValue, KEY_ID + "=" + _rowIndex, null) > 0;
	}
	public Cursor getAllToDoItemsCursor() {
		return db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_TASK, KEY_CREATION_DATE},
																		null, null, null, null, null);
	}
	
	public Cursor setCursorToToDoItem(long _rowIndex) throws SQLException {
		Cursor result = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_TASK},
									KEY_ID + "=" + _rowIndex, null, null, null,null, null);
		if ((result.getCount() == 0) || !result.moveToFirst()) {
			throw new SQLException("No to do items found for row: " + _rowIndex);
		}
		return result;
	}
	
	public ToDoItem getToDoItem(long _rowIndex) throws SQLException {
		Cursor cursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ID, KEY_TASK},
									KEY_ID + "=" + _rowIndex, null, null, null,null, null);
		if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
				throw new SQLException("No to do item found for row: " + _rowIndex);
		}
		String task = cursor.getString(cursor.getColumnIndex(KEY_TASK));
		long created = cursor.getLong(cursor.getColumnIndex(KEY_CREATION_DATE));
		ToDoItem result = new ToDoItem(task, new Date(created));
		return result;
		}
	
}

