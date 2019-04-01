package ly.betime.shuriken.persistance;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ly.betime.shuriken.entities.Alarm;

import static androidx.room.OnConflictStrategy.ABORT;

@Dao
public interface AlarmDAO {
    @Update
    void update(Alarm alarm);

    @Insert()
    long insert(Alarm alarm);

    @Delete
    void delete(Alarm alarm);

    @Query("SELECT * FROM Alarm")
    List<Alarm> list();

    @Query("SELECT * FROM Alarm WHERE id = :id LIMIT 1")
    Alarm get(int id);
}
