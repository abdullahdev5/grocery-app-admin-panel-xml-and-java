package com.grocery.groceryadmin.models;

public class ProductsModel {

    String productTitle, productDescription, productCategory, productImage, key, shortProductName;
    long productPrice, availableProducts;

    public ProductsModel() {
    }

    public ProductsModel(
            String productTitle, long productPrice, long availableProducts, String productDescription, String productCategory,
                String productImages, String key, String shortProductName
    ) {
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.availableProducts = availableProducts;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productImage = productImages;
        this.key = key;
        this.shortProductName = shortProductName;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public long getAvailableProducts() {
        return availableProducts;
    }

    public void setAvailableProducts(long availableProducts) {
        this.availableProducts = availableProducts;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShortProductName() {
        return shortProductName;
    }

    public void setShortProductName(String shortProductName) {
        this.shortProductName = shortProductName;
    }
}
