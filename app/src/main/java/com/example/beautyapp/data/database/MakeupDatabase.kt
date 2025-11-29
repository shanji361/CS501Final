//package com.example.beautyapp.data.database
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.beautyapp.data.dao.ProductDao
//import com.example.beautyapp.data.dao.ShadeDao
//import com.example.beautyapp.data.entities.MakeupProduct
//import com.example.beautyapp.data.entities.Shade
//
//
////this file: MakeupDatabase.kt is key component to Room setup, as it connects
////our entities/tables, dao, and most importantly are pre populated SQLite makeup.db
//@Database(entities = [MakeupProduct::class, Shade::class], version = 1, exportSchema = true)
//abstract class MakeupDatabase : RoomDatabase() {
//
//    abstract fun productDao(): ProductDao
//    abstract fun shadeDao(): ShadeDao
//
//    //class level member, meaning this companion object is accessible class wide, and isn't an instance method
//    companion object {
//        //volatile= field level modifier that is used to ensure the most up to date value is always read/writted by multiple threads
//        @Volatile
//        private var INSTANCE: MakeupDatabase? = null
//
//        fun getDatabase(context: Context): MakeupDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MakeupDatabase::class.java,
//                    "makeup.db"
//                )
//                    //specific configuration telling Room to prepopulate newly created makeup db
//
//                    .createFromAsset("databases/makeup.db")
//                    //if room needs to perform migration, and it cannot find a valid migration path, it will destroy and
//                    //recreate the db, instead of crashing the app. (THIS will erase exisiting user data in db)
//                    .fallbackToDestructiveMigration()
//                    //executes the creation and return the new MakeupDatabase instance
//                    .build()
//                INSTANCE = instance
//                //returns instance
//                instance
//            }
//        }
//    }
//}
//
//

package com.example.beautyapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.beautyapp.data.dao.ProductDao
import com.example.beautyapp.data.dao.ShadeDao
import com.example.beautyapp.data.entities.MakeupProduct
import com.example.beautyapp.data.entities.Shade

// THIS IS THE MIGRATION FIX
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Since we are fixing a schema validation mismatch and not actually changing data,
        // this can be empty.
    }
}

@Database(entities = [MakeupProduct::class, Shade::class], version = 2, exportSchema = true) // <--- BUMP VERSION TO 2
abstract class MakeupDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun shadeDao(): ShadeDao

    companion object {
        @Volatile
        private var INSTANCE: MakeupDatabase? = null

        fun getDatabase(context: Context): MakeupDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MakeupDatabase::class.java,
                    "makeup.db"
                )
                    .createFromAsset("databases/makeup.db")
                    .fallbackToDestructiveMigration()
                    // THIS IS THE FIX. ADD THIS LINE:
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

