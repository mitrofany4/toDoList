package com.example.todolist;

import java.sql.Date;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

	static final private int ADD_NEW_TODO = Menu.FIRST;
	static final private int REMOVE_TODO = Menu.FIRST + 1;
	
	private ArrayList<ToDoItem> todoItems;
	private ListView myListView;
	private EditText myEditText;
	private ArrayAdapter<ToDoItem> aa;
	
	private static final String TEXT_ENTRY_KEY = "TEXT_ENTRY_KEY";
	private static final String ADDING_ITEM_KEY = "ADDING_ITEM_KEY";
	private static final String SELECTED_INDEX_KEY = "SELECTED_INDEX_KEY";
	
	private ToDoDBAdapter toDoDBAdapter;
	Cursor toDoListCursor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myListView = (ListView) findViewById(R.id.listView1);
		myEditText = (EditText) findViewById(R.id.editText1);
		
				
		//Массив для хранения списка задач
		todoItems = new ArrayList<ToDoItem>();
		
		//
		int ResID=R.layout.list_item;
		
		aa = new ToDoItemAdapter(this, ResID, todoItems);
		
		myListView.setAdapter(aa);
		
		myEditText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN)
					if (keyCode ==KeyEvent.KEYCODE_ENTER){
						ToDoItem newItem = new ToDoItem(myEditText.getText().toString());
						toDoDBAdapter.insertTask(newItem);
						updateArray();
						//todoItems.add(0, newItem);
						aa.notifyDataSetChanged();
						cancelAdd();
						return true;
					}
				return false;
			}
		});
		toDoDBAdapter = new ToDoDBAdapter(this);
		
		// Open or create database
		
		toDoDBAdapter.open();
		
		populateToDOList();
		
		registerForContextMenu(myListView);
		restoreUIState();
	}

	
	private void populateToDOList() {
		
		// Получите все элементы из базы данных.
		toDoListCursor = toDoDBAdapter. getAllToDoItemsCursor();
		startManagingCursor(toDoListCursor);
		
		// Обновите массив.
		updateArray();
	}


	private void updateArray() {
		// TODO Auto-generated method stub
		
		toDoListCursor.requery();
		todoItems.clear();
		if (toDoListCursor.moveToFirst())
		do {
			String task = toDoListCursor.getString(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_TASK));
			long created = toDoListCursor.getLong(toDoListCursor.getColumnIndex(ToDoDBAdapter.KEY_CREATION_DATE));
			ToDoItem newItem = new ToDoItem(task, new Date(created));
			todoItems.add(0, newItem);
		} while(toDoListCursor.moveToNext());
		
		aa.notifyDataSetChanged();
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		
		// Создайте новые пункты меню.
		
		MenuItem itemAdd = menu.add(0, ADD_NEW_TODO, Menu.NONE, R.string.Add_new);
		
		MenuItem itemRem = menu.add(0, REMOVE_TODO, Menu.NONE, R.string.Remove_item);
		
		// Установите значки
		
		itemAdd.setIcon(R.drawable.add_new_item);
		itemRem.setIcon(R.drawable.remove_item);
		
		// Назначьте сокращенные клавиатурные команды для каждого пункта.
		itemAdd.setShortcut('0', 'a');
		itemRem.setShortcut('1', 'r');
		
		return true;
	}

	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.setHeaderTitle("Selected To Do item");
		menu.add(0, REMOVE_TODO, Menu.NONE, R.string.Remove_item);
		
		
	}
	
	private boolean addingNew = false;
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		int idx = myListView.getSelectedItemPosition();
		String removeTitle = getString(addingNew ?
		R.string.Cancel : R.string.Remove_item);
		MenuItem removeItem = menu.findItem(REMOVE_TODO);
		removeItem.setTitle(removeTitle);
		removeItem.setVisible(addingNew || idx > -1);
		return true;
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		super.onContextItemSelected(item);
		switch (item.getItemId()) {
			case (REMOVE_TODO): {
				AdapterView.AdapterContextMenuInfo menuInfo;
				menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
				int index = menuInfo.position;
				removeItem(index);
				return true;
			}
		}
	return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		int index = myListView.getSelectedItemPosition();
		switch (item.getItemId()) {
			case (REMOVE_TODO): {
				if (addingNew) {
					cancelAdd();
				}
				else {
					removeItem(index);
				}
				return true;
			}
			case (ADD_NEW_TODO): {
				addNewItem();
				return true;
				}
			}
		return false;
	}
	
	private void cancelAdd() {
		addingNew = false;
		myEditText.setVisibility(View.GONE);
	}
	
	private void addNewItem() {
		addingNew = true;
		myEditText.setVisibility(View.VISIBLE);
		myEditText.requestFocus();
		
	}
	
	private void removeItem(int _index) {
//		todoItems.remove(_index);
		
		// Элементы добавляются в объект ListView в обратном порядке, поэтому инвертируйте индекс.
		toDoDBAdapter.removeTask(todoItems.size()-_index);
		updateArray();
		aa.notifyDataSetChanged();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// Получите объект настроек Активности.
		SharedPreferences uiState = getPreferences(0);
		
		// Получите доступ к редактору настроек.
		SharedPreferences.Editor editor = uiState.edit();
		
		// Добавьте в качестве настроек значения состояния пользовательского интерфейса.
		editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
		editor.putBoolean(ADDING_ITEM_KEY, addingNew);
		// Сохраните настройки.
		editor.commit();
	}
	
	private void restoreUIState() {
		// Получите объект настроек Активности.
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		// Извлеките значения состояния пользовательского интерфейса, укажите значения по умолчанию.
		String text = settings.getString(TEXT_ENTRY_KEY, "");
		Boolean adding = settings.getBoolean(ADDING_ITEM_KEY, false);
		// Восстановите предыдущее состояние пользовательского интерфейса.
		if (adding) {
			addNewItem();
			myEditText.setText(text);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		int pos = -1;
		if (savedInstanceState != null)
			if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
				pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);
		myListView.setSelection(pos);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		// Закройте базу данных
		toDoDBAdapter.close();
	}
	
}
