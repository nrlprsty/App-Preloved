package com.example.app_preloved;

public class Produk {

    private String id;
    private String nama;
    private long harga;
    private String kategori;
    private String deskripsi;
    private String foto; // Base64
    private String sellerId;
    private String status; // "Available" atau "Sold"

    public Produk() {
        // Constructor kosong wajib ada untuk Firestore
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}