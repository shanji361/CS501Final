package com.example.beautyapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// NEW AND CORRECT
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Shade::class,
            parentColumns = ["shade_id"],
            childColumns = ["shade_id"],
            onDelete = ForeignKey.NO_ACTION // <-- THIS IS THE FIX
        )
    ]
)

data class MakeupProduct(
    //in sqlite db, i name all the columns with underscore, kotlin doesn't allow underscore naming
    //convention, so I passing the column name as in the db, to ColumnInfo name.
    @PrimaryKey
    @ColumnInfo(name = "product_id")
    val productId: Int,

    @ColumnInfo(name = "shade_id")
    val shadeId: Int,

    val type: String,
    val brand: String,
    val name: String,


    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    val price: Double,
    val description: String?
)

