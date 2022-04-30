package ca.robertonicanor.robstoreapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ca.robertonicanor.robstoreapp.Interface.ItemClickListener;
import ca.robertonicanor.robstoreapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtproductName,
                    txtProductDescription,
                    txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        txtproductName = itemView.findViewById(R.id.itemLay_product_name);
        txtProductPrice = itemView.findViewById(R.id.itemLay_product_price);
        txtProductDescription = itemView.findViewById(R.id.itemLay_product_description);
        imageView = itemView.findViewById(R.id.itemLay_product_Image);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
