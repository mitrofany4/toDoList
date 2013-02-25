package com.example.todolist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ToDoItem {
	String task;
	Date created;
	
	public String getTask(){
		return task;
	}
	
	public Date getCreated(){
		return created;
	}
	
	public ToDoItem(String _task) {
		this(_task, new Date(java.lang.System.currentTimeMillis()));
	}

	public ToDoItem(String _task, Date _created) {
		// TODO Auto-generated constructor stub
		task=_task;
		created=_created;
	}
	
	@Override
	public String toString(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		String datestring = sdf.format(created);
		return "(" + datestring + ") " + task; 
	}
}
