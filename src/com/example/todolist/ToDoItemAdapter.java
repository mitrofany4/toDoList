package com.example.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToDoItemAdapter extends ArrayAdapter<ToDoItem>{
	     int resource;
	     
	public ToDoItemAdapter(Context _context, int _resource,
			List<ToDoItem> _items) {
		
		super(_context, _resource, _items);
		resource=_resource;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout todoView;
		
		ToDoItem item = getItem(position);
		String taskString = item.getTask();
		Date createdDate = item.getCreated();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		String dateString = sdf.format(createdDate);
		
		if (convertView == null) {
			todoView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, todoView, true);
		} else {
					todoView = (LinearLayout) convertView;
					}
					TextView dateView = (TextView)todoView.findViewById(R.id.rowDate);
					TextView taskView = (TextView)todoView.findViewById(R.id.row);
					dateView.setText(dateString);
					taskView.setText(taskString);
					return todoView;
					}
		
}
	
