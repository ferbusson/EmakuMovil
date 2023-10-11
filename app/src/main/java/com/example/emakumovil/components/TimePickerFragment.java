package com.example.emakumovil.components;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.TimePicker;

@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {

	private String time_selected="";
	private long b;
	
    public TimePickerFragment() {
    }

    public TimePickerFragment(long b) {
    	this.b=b;
    }
    
    
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		int hora = c.get(Calendar.HOUR_OF_DAY);
		int minuto = c.get(Calendar.MINUTE);
		if (savedInstanceState!=null) {
			b=savedInstanceState.getLong("b");
		}
		return new TimePickerDialog(getActivity(), this, hora,minuto,true);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putLong("b",b);
	}
		@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		time_selected = (hourOfDay<=9?"0"+hourOfDay:""+hourOfDay)+":"+(minute<=9?"0"+minute:""+minute);
        DialogClickEvent event = new DialogClickEvent(this,time_selected,b);
        notificando(event);

	}
	
	public String getTime() {
		return time_selected;
	}

	private void notificando(DialogClickEvent event) {
		GlobalData.dialogClickListener.dialogClickEvent(event);
	}

	public void addDialogClickListener(DialogClickListener l) {
		System.out.println("se adiciono un oyente");
		GlobalData.dialogClickListener = l;
	}


}
