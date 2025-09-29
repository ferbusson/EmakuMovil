package com.example.emakumovil.modules.inventario;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.emakumovil.R;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.databinding.ActivitySearchDataDialogBinding;

/*
 * Modernized DialogFragment for searching products.
 *
 * This class now uses:
 * - androidx.fragment.app.DialogFragment: The modern, supported DialogFragment.
 * - View Binding: To safely access views without findViewById.
 * - ViewModel: To separate data logic from the UI.
 * - LiveData: To observe data changes from the ViewModel.
 * - RecyclerView: To efficiently display the list of products.
 */
public class SearchProdDialogFrame extends DialogFragment {

	private ActivitySearchDataDialogBinding binding; // View Binding instance
	private SearchProdViewModel viewModel; // ViewModel for UI logic and data
	private ProductsAdapter adapter; // Adapter for the RecyclerView
	private OnProductSelectedListener listener; // Callback listener

	String barra;

	// Callback to send the selected barcode back to the calling activity/fragment
	public interface OnProductSelectedListener {
		void onProductSelected(String barcode);
	}

	// It's better practice to set the listener via a method or onAttach
	public void setOnProductSelectedListener(OnProductSelectedListener listener) {
		this.listener = listener;
	}

	// Use a static newInstance method for safe argument passing
	/*public static SearchProdDialogFrame newInstance(String titulo) {
		SearchProdDialogFrame fragment = new SearchProdDialogFrame();
		Bundle args = new Bundle();
		args.putString("titulo", titulo);
		fragment.setArguments(args);
		return fragment;
	}*/

	public static SearchProdDialogFrame newInstance(String titulo, String query) {
		SearchProdDialogFrame fragment = new SearchProdDialogFrame();
		Bundle args = new Bundle();
		args.putString("titulo", titulo);
		args.putString("queryCode", query);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(@NonNull Context context) {
		super.onAttach(context);
		// Automatically attach the listener if the parent fragment/activity implements it
		if (getParentFragment() instanceof OnProductSelectedListener) {
			this.listener = (OnProductSelectedListener) getParentFragment();
		} else if (context instanceof OnProductSelectedListener) {
			this.listener = (OnProductSelectedListener) context;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		// Inflate the layout using View Binding
		binding = ActivitySearchDataDialogBinding.inflate(LayoutInflater.from(getContext()));

		// Get the ViewModel
		viewModel = new ViewModelProvider(this).get(SearchProdViewModel.class);

		// Retrieve the queryCode from arguments
		String queryCode = "MVSEL0033"; // Default value
		if (getArguments() != null) {
			queryCode = getArguments().getString("queryCode", "MVSEL0033");
			String titulo = getArguments().getString("titulo", "Buscar Producto");
			binding.tvTitulo.setText(titulo);
		}
		// Set up the RecyclerView
		setupRecyclerView();

		// Observe LiveData for product list changes
		viewModel.getProducts().observe(this, products -> {
			adapter.submitList(products); // Use submitList for DiffUtil
		});

		// Observe LiveData for loading state changes
		viewModel.isLoading().observe(this, isLoading -> {
			binding.progressBar1.setVisibility(isLoading ? View.VISIBLE : View.GONE);
		});

		// Set the title from arguments
		if (getArguments() != null) {
			String titulo = getArguments().getString("titulo", "Buscar Producto");
			binding.tvTitulo.setText(titulo);
		}


		// --- CONSOLIDATED AND CORRECTED CLICK LISTENER ---
		final String finalQueryCode = queryCode; // Make it final for the lambda
		binding.ibBuscar.setOnClickListener(v -> {
			String query = binding.etQuery.getText().toString();
			if (!query.isEmpty()) {
				viewModel.clearProducts();
				viewModel.searchProducts(query, finalQueryCode);
			}
		});

		// Build the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
		builder.setView(binding.getRoot())
				.setNegativeButton(R.string.cancelar, (dialog, id) -> dismiss());

		return builder.create();
	}

	private void setupRecyclerView() {
		// Initialize the adapter with a lambda for item clicks
		adapter = new ProductsAdapter(product -> {
			if (listener != null) {
				// Assuming descripcion1 is the barcode you want to return
				listener.onProductSelected(product.getDescripcion1());
			}
			dismiss(); // Close the dialog after selection
		});

		binding.lvSprod.setLayoutManager(new LinearLayoutManager(getContext()));
		binding.lvSprod.setAdapter(adapter);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null; // Avoid memory leaks with View Binding
	}

	protected String getBarra() {
		return barra;
	}
}
