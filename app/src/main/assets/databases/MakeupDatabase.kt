package com.example.beautyapp.data.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beautyapp.data.dao.ProductDao
import com.example.beautyapp.data.dao.ShadeDao
import com.example.beautyapp.data.entities.Product
import com.example.beautyapp.data.entities.Shade

//this file: MakeupDatabase.kt is key component to Room setup, as it connects
//our entities/tables, dao, and most importantly are pre populated SQLite makeup.db
@Database(
    entities= [Shade ::class, Product::class],
    version=1,
    exportSchema=false
)

abstract class MakeupDatabase: RoomDatabse(){
    abstract fun shadeDao():shadeDao
    abstract fun productDao():productDao

    //class level member, meaning this companion object is accessible class wide, and isn't an instance method
    companion object{
        //volatile= field level modifier that is used to ensure the most up to date value is always read/writted by multiple threads
        @Volatile
        private var INSTANCE: MakeupDatabase?=null
        fun getDatabase(context:Context): MakeupDatabase{
            return INSTANCE ?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    MakeupDatabase::class.java,
                    "makeup.db"
                )
                    //specific configuration telling Room to prepopulate newly created makeup db
                    .createFromAsset("databases/makeup.db")
                    //if room needs to perform migration, and it cannot find a valid migration path, it will destroy and
                    //recreate the db, instead of crashing the app. (THIS will erase exisiting user data in db)
                    .fallbackToDestructiveMigration()
                    //executes the creation and return the new MakeupDatabase instance
                    .build()
                INSTANCE=instance
               //return instance
                instance
            }
        }
    }
}
