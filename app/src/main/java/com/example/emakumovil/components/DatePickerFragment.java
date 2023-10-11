package com.example.emakumovil.components;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements OnDateSetListener {

	private String date_selected="";
	private long b;
	
	public DatePickerFragment() {
		
	}
	
    public DatePickerFragment(long b) {
    	this.b=b;
    }
    
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);
		if (savedInstanceState!=null) {
			b=savedInstanceState.getLong("b");
		}
		
		return new DatePickerDialog(getActivity(), this, cyear, cmonth, cday);

	}
	
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putLong("b",b);
	}
		@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		date_selected = year
				+ "/"
				+ (monthOfYear + 1 <= 9 ? "0" + (monthOfYear + 1)
						: (monthOfYear + 1)) + "/"
				+ (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);

        DialogClickEvent event = new DialogClickEvent(this,date_selected,b);
        notificando(event);
	}
	
	public String getDate() {
		return date_selected;
	}

	private void notificando(DialogClickEvent event) {
		System.out.println("notificando a "+event);
		GlobalData.dialogClickListener.dialogClickEvent(event);
	}

	public void addDialogClickListener(DialogClickListener l) {
		System.out.println("se adiciono un oyente");
		GlobalData.dialogClickListener = l;
	}
}
