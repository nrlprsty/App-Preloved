package com.example.app_preloved;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    public interface OnProdukClickListener {
        void onItemClick(Produk produk);
    }

    private final List<Produk> daftarProduk;
    private final OnProdukClickListener listener;

    public ProdukAdapter(List<Produk> daftarProduk, OnProdukClickListener listener) {
        this.daftarProduk = daftarProduk;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = daftarProduk.get(position);

        holder.tvNamaProduk.setText(produk.getNama());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String hargaFormatted = formatRupiah.format(produk.getHarga()).replace(",00", "");
        holder.tvHargaProduk.setText(hargaFormatted);

        // Story: Tampilkan status Available/Sold
        String status = produk.getStatus() != null ? produk.getStatus() : "Available";
        holder.tvStatusProduk.setText(status);

        if (produk.getFoto() != null && !produk.getFoto().isEmpty()) {
            try {
                byte[] decoded = Base64.decode(produk.getFoto(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                holder.ivFotoProduk.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.ivFotoProduk.setImageDrawable(null);
            }
        }

        // Tap kartu untuk masuk ke Edit Produk (yang juga punya tombol Hapus di dalamnya)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(produk);
        });
    }

    @Override
    public int getItemCount() {
        return daftarProduk.size();
    }

    static class ProdukViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFotoProduk;
        TextView tvNamaProduk, tvHargaProduk, tvStatusProduk;

        ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFotoProduk    = itemView.findViewById(R.id.ivFotoProduk);
            tvNamaProduk    = itemView.findViewById(R.id.tvNamaProduk);
            tvHargaProduk   = itemView.findViewById(R.id.tvHargaProduk);
            tvStatusProduk  = itemView.findViewById(R.id.tvStatusProduk);
        }
    }
}