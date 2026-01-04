package project.kavyasrees3537153.packagetracker.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_packages")
data class SavedPackage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val trackingNumber: String,
    val status: String,
    val eta: String

)



@Dao
interface SavedPackageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: SavedPackage)

    @Query("SELECT * FROM saved_packages")
    fun getAllPackages(): Flow<List<SavedPackage>>

    @Delete
    suspend fun deletePackage(pkg: SavedPackage)

    @Query("SELECT * FROM saved_packages WHERE status = :status")
    fun getPackagesByStatus(status: String): Flow<List<SavedPackage>>

    @Query("SELECT * FROM saved_packages WHERE trackingNumber = :trackingNo LIMIT 1")
    fun getPackageByTrackingNumber(trackingNo: String): SavedPackage?

    @Query("UPDATE saved_packages SET status = :status WHERE trackingNumber = :trackingNo")
    fun updateStatus(trackingNo: String, status: String)

    @Query("UPDATE saved_packages SET name = :name WHERE trackingNumber = :trackingNo")
    fun updateName(trackingNo: String, name: String)


}


@Database(entities = [SavedPackage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedPackageDao(): SavedPackageDao
}


object DatabaseProvider {
    private var db: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "package_tracker_db"
            ).build()
        }
        return db!!
    }
}
