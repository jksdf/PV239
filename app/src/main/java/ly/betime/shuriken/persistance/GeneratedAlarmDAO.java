package ly.betime.shuriken.persistance;

import com.google.common.util.concurrent.ListenableFuture;

import org.threeten.bp.LocalDate;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;
import ly.betime.shuriken.entities.GeneratedAlarm;
import ly.betime.shuriken.entities.LocalDateConverter;

@Dao
public interface GeneratedAlarmDAO {
    @Update
    void update(GeneratedAlarm alarm);

    @Insert
    ListenableFuture<Long> insert(GeneratedAlarm alarm);

    @Delete
    void delete(GeneratedAlarm alarm);

    @Query("SELECT * FROM GeneratedAlarm")
    List<GeneratedAlarm> listSync();

    @Query("SELECT * FROM GeneratedAlarm WHERE forDate = :localDate LIMIT 1")
    @TypeConverters(LocalDateConverter.class)
    ListenableFuture<GeneratedAlarm> get(LocalDate localDate);
}
