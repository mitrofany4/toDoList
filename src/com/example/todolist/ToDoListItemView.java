package com.example.todolist;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;
	
public class ToDoListItemView extends TextView{

	public ToDoListItemView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public ToDoListItemView(Context context, AttributeSet ats, int ds) {
		super(context, ats, ds);
		init();
	}
	
	public ToDoListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	@Override
	public void onDraw(Canvas canvas) {
		// ������� ���� ��� �����
		canvas.drawColor(paperColor);
		
		// ��������� ������������ �����
		canvas.drawLine(0, 0, getMeasuredHeight(), 0, linePaint);
		canvas.drawLine(0, getMeasuredHeight(),
		getMeasuredWidth(), getMeasuredHeight(),linePaint);
		
		// ��������� ������
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		
		// ����������� ����� � ������� �� ������
		canvas.save();
		canvas.translate(margin, 0);
		
		// ����������� TextView ��� ������ ������.
		super.onDraw(canvas);
		canvas.restore();
	}
	


private Paint marginPaint;
private Paint linePaint;
private int paperColor;
private float margin;

private void init() {
	// �������� ������ �� ������� ��������.
	Resources myResources = getResources();
	
	// �������� ����� ��� ���������, ������� �� ����� ������������ � ������ onDraw.
	marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	marginPaint.setColor(myResources.getColor(R.color.notepad_margin));
	linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	linePaint.setColor(myResources.getColor(R.color.notepad_lines));
	// �������� ���� ���� ��� ����� � ������ ������.
	paperColor = myResources.getColor(R.color.notepad_paper);
	margin = myResources.getDimension(R.dimen.notepad_margin);
	}
}
