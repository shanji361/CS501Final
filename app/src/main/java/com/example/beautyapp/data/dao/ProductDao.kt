package com.example.beautyapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.beautyapp.data.Product
// this file: ProductDao.kt, writes the QUERY and interacts with SQLite db, to get data from our products table
//returns 3 recommended products (foundation, blush, lip liner )
@Dao
interface ProductDao{
    @Query("SELECT * FROM products")
    fun getAllProducts(shadeId:Int):List<Product>
    @Query("SELECT * FROM products WHERE product_id=:productId")
    fun getProductById(productId:Int): Product?
}