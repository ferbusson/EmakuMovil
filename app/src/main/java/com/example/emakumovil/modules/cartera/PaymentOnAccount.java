package com.example.emakumovil.modules.cartera;

import java.text.DecimalFormat;

import com.example.emakumovil.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class PaymentOnAccount extends DialogFragment implements OnFocusChangeListener, OnClickListener, View.OnClickListener {

	private LayoutInflater inflater;
	private TextView tv_vbase_cuenta;
	private TextView tv_vtotal_cuenta;
	private EditText et_vabono_cuenta;
	private TextView tv_vsaldo_cuenta;
	private TextView tv_vdescuento;

	private EditText et_desc1;
	private EditText et_desc2;
	private EditText et_desc3;
	
	private ImageButton im_abono;
	
	private TextView tv_vdesc1;
	private TextView tv_vdesc2;
	private TextView tv_vdesc3;

	private double base;
	private double total;
	private double abono;
	private double saldo;
	private double pdesc1;
	private double pdesc2;
	private double pdesc3;
	private double vdesc1;
	private double vdesc2;
	private double vdesc3;
	private double vtdesc;
	
	private DecimalFormat df = new DecimalFormat("##,###,###");

	public PaymentOnAccount(double base,double total) {
		this.base=base;
		this.total=total;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    inflater = getActivity().getLayoutInflater();
	    View v = inflater.inflate(R.layout.abonoacuenta, null);
	    builder.setView(v)
	           .setNegativeButton(R.string.cancelar, this).setPositiveButton(R.string.aceptar,this);      
	    
	    tv_vbase_cuenta = (TextView)v.findViewById(R.id.tv_vbase_cuenta);
	    tv_vtotal_cuenta = (TextView)v.findViewById(R.id.tv_vtotal_cuenta);
	    et_vabono_cuenta = (EditText)v.findViewById(R.id.et_abono_cuenta);
	    tv_vsaldo_cuenta = (TextView)v.findViewById(R.id.tv_vsaldo_cuenta);
	    tv_vdescuento = (TextView)v.findViewById(R.id.tv_vdescuento);
	    
	    et_desc1 = (EditText)v.findViewById(R.id.et_desc1);
	    et_desc2 = (EditText)v.findViewById(R.id.et_desc2);
	    et_desc3 = (EditText)v.findViewById(R.id.et_desc3);
	    
	    im_abono = (ImageButton)v.findViewById(R.id.im_abono);
	    im_abono.setOnClickListener(this);
	    
	    et_vabono_cuenta.setOnFocusChangeListener(this);
	    et_desc1.setOnFocusChangeListener(this);
	    et_desc2.setOnFocusChangeListener(this);
	    et_desc3.setOnFocusChangeListener(this);
	    
	    tv_vdesc1 = (TextView)v.findViewById(R.id.tv_vdesc1);
	    tv_vdesc2 = (TextView)v.findViewById(R.id.tv_vdesc2);
	    tv_vdesc3 = (TextView)v.findViewById(R.id.tv_vdesc3);

	    tv_vbase_cuenta.setText(df.format(base));
	    tv_vtotal_cuenta.setText(df.format(total));
	    tv_vsaldo_cuenta.setText(df.format(total));

	    Dialog d = builder.create();
	    return d;
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (!hasFocus) {
			String valor = ((EditText)v).getText().toString();
			pdesc1 = getDoubleValue(et_desc1);
			if (pdesc1>=0 && pdesc1<=100) {
				vdesc1 = base*pdesc1/100;
				if (abono+vdesc1+vdesc2+vdesc3>total) {
					Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
					tv_vdesc1.setText("0");
					et_desc1.setText("");
					vdesc1=0;
					pdesc1=0;
				}
				else {
					tv_vdesc1.setText(df.format(vdesc1));
				}
				pdesc2 = getDoubleValue(et_desc2);
				if (pdesc2>=0 && pdesc2<=100) {
					vdesc2 = vdesc1*pdesc2/100;
					tv_vdesc2.setText(df.format(vdesc2));
					if (abono+vdesc1+vdesc2+vdesc3>total) {
						Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
						tv_vdesc2.setText("0");
						et_desc2.setText("");
						vdesc2=0;
						pdesc1=0;
					}
					else {
						tv_vdesc2.setText(df.format(vdesc2));
					}
					pdesc3 = getDoubleValue(et_desc3);
					if (pdesc3>=0 && pdesc3<=100) {
						vdesc3 = vdesc2*pdesc3/100;
						tv_vdesc3.setText(df.format(vdesc3));
						if (abono+vdesc1+vdesc2+vdesc3>total) {
							Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
							tv_vdesc3.setText("0");
							et_desc3.setText("");
							vdesc3=0;
							pdesc3=0;
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

			if (v.getId()==R.id.et_abono_cuenta) {
				abono = getDoubleValue(et_vabono_cuenta);
				if (abono+vtdesc>total) {
					Toast.makeText(getActivity(),R.string.error_mayor_valor, Toast.LENGTH_LONG).show();
					et_vabono_cuenta.setText("0");
					abono=0;
				}
			}
			else if (!valor.equals("") && (v.getId()==R.id.et_desc1 || v.getId()==R.id.et_desc2 || v.getId()==R.id.et_desc3)) {
				vtdesc = vdesc1+vdesc2+vdesc3;
				tv_vdescuento.setText(df.format(vtdesc));
			}
			
			saldo = total - abono - vtdesc;
			
			tv_vsaldo_cuenta.setText(df.format(saldo));

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

	public double getPdesc1() {
		return pdesc1;
	}

	public double getPdesc2() {
		return pdesc2;
	}

	public double getPdesc3() {
		return pdesc3;
	}

	public double getAbono() {
		return abono;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		abono = total-vtdesc;
		saldo = 0;
		tv_vsaldo_cuenta.setText(df.format(saldo));
		et_vabono_cuenta.setText(df.format(abono));
		
	}
	
}
