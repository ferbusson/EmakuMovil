package com.example.emakumovil.modules.cartera;

import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;

@SuppressLint("ValidFragment")
public class PartialPaymentDialog extends DialogFragment implements OnFocusChangeListener, OnClickListener, View.OnClickListener,TextWatcher {
	
	private LayoutInflater inflater;
	
	private String sdocumento;
	private double base;
	private double total;
	private double abono;
	private double saldo;
	private double vdesc1;
	private double vdesc2;
	private double vdesc3;
	private double vtdesc;
	
	private TextView tv_documento;
	private TextView tv_vbase_descuento;
	private TextView tv_vtotal_documento;
	private TextView tv_vsaldo_documento;
	
	private EditText et_desc1;
	private EditText et_desc2;
	private EditText et_desc3;
	
	private ImageButton im_abono;
	
	private EditText et_descuento;
	private EditText et_vabono_documento;;
	
	private TextView tv_vdesc1;
	private TextView tv_vdesc2;
	private TextView tv_vdesc3;
	
	private DecimalFormat df = new DecimalFormat("##,###,###");

	public PartialPaymentDialog() {}
	
	public PartialPaymentDialog(String sdocumento,double base,double total) {
		this.sdocumento=sdocumento;
		this.base=base;
		this.total=total;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.abonoparcial, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, this).setPositiveButton(R.string.aceptar,this);      
	    tv_documento = (TextView)v.findViewById(R.id.tv_documento);
	    tv_vbase_descuento = (TextView)v.findViewById(R.id.tv_vbase_documento);
	    tv_vtotal_documento = (TextView)v.findViewById(R.id.tv_vtotal_documento);
	    et_vabono_documento = (EditText)v.findViewById(R.id.et_abono);
	    tv_vsaldo_documento = (TextView)v.findViewById(R.id.tv_vsaldo_documento);
	    
	    et_desc1 = (EditText)v.findViewById(R.id.et_desc1);
	    et_desc2 = (EditText)v.findViewById(R.id.et_desc2);
	    et_desc3 = (EditText)v.findViewById(R.id.et_desc3);
	    
	    et_descuento = (EditText)v.findViewById(R.id.et_descuento);
	    
	    im_abono = (ImageButton)v.findViewById(R.id.im_abono);
	    im_abono.setOnClickListener(this);
	    
	    et_desc1.setOnFocusChangeListener(this);
	    et_desc2.setOnFocusChangeListener(this);
	    //et_desc3.setOnFocusChangeListener(this);
	    et_desc3.addTextChangedListener(this);
	    et_descuento.setOnFocusChangeListener(this);
	    et_vabono_documento.setOnFocusChangeListener(this);
	    
	    tv_vdesc1 = (TextView)v.findViewById(R.id.tv_vdesc1);
	    tv_vdesc2 = (TextView)v.findViewById(R.id.tv_vdesc2);
	    tv_vdesc3 = (TextView)v.findViewById(R.id.tv_vdesc3);

	    tv_documento.setText(sdocumento);
	    tv_vbase_descuento.setText(df.format(base));
	    tv_vtotal_documento.setText(df.format(total));
	    tv_vsaldo_documento.setText(df.format(total));
	    Dialog d = builder.create();
	    return d;
	}

	private void validDescs(View v) {
		String valor = ((EditText)v).getText().toString();
		double pdesc1 = getDoubleValue(et_desc1);
		if (pdesc1>=0 && pdesc1<=100) {
			vdesc1 = base*pdesc1/100;
			if (abono+vdesc1+vdesc2+vdesc3>total) {
				Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
				tv_vdesc1.setText("0");
				et_desc1.setText("");
				vdesc1=0;
			}
			else {
				tv_vdesc1.setText(df.format(vdesc1));
			}
			double pdesc2 = getDoubleValue(et_desc2);
			if (pdesc2>=0 && pdesc2<=100) {
				vdesc2 = vdesc1*pdesc2/100;
				tv_vdesc2.setText(df.format(vdesc2));
				if (abono+vdesc1+vdesc2+vdesc3>total) {
					Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
					tv_vdesc2.setText("0");
					et_desc2.setText("");
					vdesc2=0;
				}
				else {
					tv_vdesc2.setText(df.format(vdesc2));
				}
				double pdesc3 = getDoubleValue(et_desc3);
				if (pdesc3>=0 && pdesc3<=100) {
					vdesc3 = vdesc2*pdesc3/100;
					tv_vdesc3.setText(df.format(vdesc3));
					if (abono+vdesc1+vdesc2+vdesc3>total) {
						Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
						tv_vdesc3.setText("0");
						et_desc3.setText("");
						vdesc3=0;
					}
					else {
						tv_vdesc3.setText(df.format(vdesc3));
					}
				}
				else {
					Toast.makeText(getActivity(),R.string.error_descuento, Toast.LENGTH_LONG).show();
					et_desc3.setText("0");
				}
			}
			else {
				Toast.makeText(getActivity(),R.string.error_descuento, Toast.LENGTH_LONG).show();
				et_desc2.setText("0");
			}
		}
		else {
			Toast.makeText(getActivity(),R.string.error_descuento, Toast.LENGTH_LONG).show();
			et_desc1.setText("0");
		}
	
	if (v.getId()==R.id.et_abono) {
		abono = getDoubleValue(et_vabono_documento);
		if (abono+vtdesc>total) {
			Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
			et_vabono_documento.setText("0");
			abono=0;
		}
	} 
	else if (v.getId()==R.id.et_descuento) {
		vtdesc = getDoubleValue(et_descuento);
		if (abono+vtdesc>total) {
			Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
			et_descuento.setText("0");
			vtdesc=0;
			vdesc1=0;
			vdesc2=0;
			vdesc3=0;
			et_desc1.setText("");
			et_desc2.setText("");
			et_desc3.setText("");
			tv_vdesc1.setText("0");
			tv_vdesc2.setText("0");
			tv_vdesc3.setText("0");
		}
	} 
	else if (!valor.equals("") && (v.getId()==R.id.et_desc1 || v.getId()==R.id.et_desc2 || v.getId()==R.id.et_desc3)) {
		vtdesc = vdesc1+vdesc2+vdesc3;
		et_descuento.setText(df.format(vtdesc));
	}
	
	saldo = total - abono - vtdesc;
	
	tv_vsaldo_documento.setText(df.format(saldo));
		
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (!hasFocus) {
			validDescs(v);
		}
	}

	private double getDoubleValue(EditText text) {
		try {
			String sval = text.getText().toString().replace(".","");
			double val = Double.parseDouble(sval);
			text.setText(df.format(val));
			return val;
		}
		catch(NumberFormatException e) {
			text.setText("");
			return 0;
		}
	}
	
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		if (abono!=getDoubleValue(et_vabono_documento)) {
			if (abono+vtdesc>total) {
				Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
				et_vabono_documento.setText("0");
				abono=0;
			}
			else {
				abono=getDoubleValue(et_vabono_documento);
			}
			saldo = total - abono - vtdesc;
		}		
	}

	public double getAbono() {
		return abono;
	}

	public double getVtdesc() {
		return vtdesc;
	}

	public double getSaldo() {
		return saldo;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		abono=total-vtdesc;
		et_vabono_documento.setText(df.format(abono));
		saldo = 0;
		tv_vsaldo_documento.setText(df.format(saldo));
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (!s.toString().equals("")) {
			double pdesc3 = Double.parseDouble(s.toString());
			if (pdesc3>=0 && pdesc3<=100) {
				vdesc3 = vdesc2*pdesc3/100;
				tv_vdesc3.setText(df.format(vdesc3));
				if (abono+vdesc1+vdesc2+vdesc3>total) {
					Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
					tv_vdesc3.setText("0");
					et_desc3.setText("");
					vdesc3=0;
				}
				else {
					tv_vdesc3.setText(df.format(vdesc3));
				}
			}
			else {
				Toast.makeText(getActivity(),R.string.error_descuento, Toast.LENGTH_LONG).show();
				et_desc3.setText("0");
			}
		}		
	}
	
	

}
