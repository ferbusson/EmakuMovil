package com.example.emakumovil.modules.inventario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.emakumovil.R;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;

public class ModifyingQuantityDialog  extends DialogFragment implements OnClickListener {

	private LayoutInflater inflater;
	private EditText et_cantidad;
	private Button positiveButton;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.cantidaddialog, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, this)
	           .setPositiveButton(R.string.aceptar,this);      
	    
	    et_cantidad = (EditText)v.findViewById(R.id.et_cantidad);
	    Dialog d = builder.create();
	    return d;
	}

	@Override
	public void onStart()
	{
	    super.onStart();    
	    AlertDialog d = (AlertDialog)getDialog();
	    if(d != null)
	    {
		    positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
	        positiveButton.setOnClickListener(new View.OnClickListener()
	                {
	                    @Override
	                    public void onClick(View v)
	                    {
	                            dismiss();
	                            DialogClickEvent event = new DialogClickEvent(this,Integer.parseInt(et_cantidad.getText().toString()));
	                            notificando(event);
	                    }
	                });
	    }
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
	
	private void notificando(DialogClickEvent event) {
		GlobalData.dialogClickListener.dialogClickEvent(event);
	}

	public void addDialogClickListener(DialogClickListener l) {
		System.out.println("se adiciono un oyente");
		GlobalData.dialogClickListener = l;
	}


}
