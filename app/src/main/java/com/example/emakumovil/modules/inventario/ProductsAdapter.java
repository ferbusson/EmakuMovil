package com.example.emakumovil.modules.inventario;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.databinding.SprodrecorddataBinding; // Your layout for a single row

/*
 * This is a modern RecyclerView adapter using ListAdapter.
 * It automatically handles list updates efficiently using DiffUtil.
 */
public class ProductsAdapter extends ListAdapter<FieldsDDV, ProductsAdapter.ProductViewHolder> {

    private final OnProductClickListener clickListener;

    // Interface to handle clicks on items
    public interface OnProductClickListener {
        void onProductClick(FieldsDDV product);
    }

    // Constructor
    public ProductsAdapter(OnProductClickListener clickListener) {
        super(DIFF_CALLBACK); // Pass the DiffUtil callback to the parent constructor
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single item using View Binding
        SprodrecorddataBinding binding = SprodrecorddataBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Get the data item for this position
        FieldsDDV currentProduct = getItem(position);
        // Bind the data to the ViewHolder
        holder.bind(currentProduct, clickListener);
    }

    // The ViewHolder class that holds the views for a single item
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final SprodrecorddataBinding binding; // Binding for the item layout

        public ProductViewHolder(SprodrecorddataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        // Binds a product's data to the views
        public void bind(final FieldsDDV product, final OnProductClickListener clickListener) {
            binding.tvDescripcion1.setText(product.getDescripcion1());
            binding.tvDescripcion2.setText(product.getDescripcion2());
            // Set the click listener on the whole item view
            itemView.setOnClickListener(v -> clickListener.onProductClick(product));
        }
    }

    // DiffUtil calculates the difference between two lists and enables efficient updates.
    private static final DiffUtil.ItemCallback<FieldsDDV> DIFF_CALLBACK = new DiffUtil.ItemCallback<FieldsDDV>() {
        @Override
        public boolean areItemsTheSame(@NonNull FieldsDDV oldItem, @NonNull FieldsDDV newItem) {
            // Check for a unique, stable ID. Assuming descripcion1 (barcode) is unique.
            return oldItem.getDescripcion1().equals(newItem.getDescripcion1());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FieldsDDV oldItem, @NonNull FieldsDDV newItem) {
            // Check if the content of the items is the same, to avoid unnecessary re-draws.
            return oldItem.getDescripcion1().equals(newItem.getDescripcion1()) &&
                    oldItem.getDescripcion2().equals(newItem.getDescripcion2());
        }
    };
}
