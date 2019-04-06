package ly.betime.shuriken.persistance;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ly.betime.shuriken.entities.Alarm;

@Dao
public interface AlarmDAO {
    @Update
    void update(Alarm alarm);

    @Insert()
    long insert(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM Alarm")
    LiveData<List<Alarm>> list();

    @Query("SELECT * FROM Alarm")
    List<Alarm> listSync();

    @Query("SELECT * FROM Alarm WHERE id = :id LIMIT 1")
    LiveData<Alarm> get(int id);

    @Query("SELECT * FROM Alarm WHERE id = :id LIMIT 1")
    Alarm getSync(int id);
}
