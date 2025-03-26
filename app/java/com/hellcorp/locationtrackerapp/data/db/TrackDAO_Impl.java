package com.hellcorp.locationtrackerapp.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class TrackDAO_Impl implements TrackDAO {
    private final RoomDatabase __db;

    private final EntityInsertionAdapter<TrackItemEntity> __insertionAdapterOfTrackItemEntity;

    private final SharedSQLiteStatement __preparedStmtOfRemoveTrack;

    public TrackDAO_Impl(@NonNull final RoomDatabase __db) {
        this.__db = __db;
        this.__insertionAdapterOfTrackItemEntity = new EntityInsertionAdapter<TrackItemEntity>(__db) {
            @Override
            @NonNull
            protected String createQuery() {
                return "INSERT OR ABORT INTO `track_item` (`id`,`time`,`date`,`distance`,`average_speed`,`geopoint`) VALUES (?,?,?,?,?,?)";
            }

            @Override
            protected void bind(@NonNull final SupportSQLiteStatement statement,
                                @NonNull final TrackItemEntity entity) {
                if (entity.getId() == null) {
                    statement.bindNull(1);
                } else {
                    statement.bindLong(1, entity.getId());
                }
                if (entity.getTime() == null) {
                    statement.bindNull(2);
                } else {
                    statement.bindString(2, entity.getTime());
                }
                if (entity.getDate() == null) {
                    statement.bindNull(3);
                } else {
                    statement.bindString(3, entity.getDate());
                }
                if (entity.getDistance() == null) {
                    statement.bindNull(4);
                } else {
                    statement.bindString(4, entity.getDistance());
                }
                if (entity.getAverageSpeed() == null) {
                    statement.bindNull(5);
                } else {
                    statement.bindString(5, entity.getAverageSpeed());
                }
                if (entity.getGeopoint() == null) {
                    statement.bindNull(6);
                } else {
                    statement.bindString(6, entity.getGeopoint());
                }
            }
        };
        this.__preparedStmtOfRemoveTrack = new SharedSQLiteStatement(__db) {
            @Override
            @NonNull
            public String createQuery() {
                final String _query = "DELETE FROM track_item WHERE id = ?";
                return _query;
            }
        };
    }

    @Override
    public Object insertTrack(final TrackItemEntity trackItemEntity,
                              final Continuation<? super Unit> $completion) {
        return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
            @Override
            @NonNull
            public Unit call() throws Exception {
                __db.beginTransaction();
                try {
                    __insertionAdapterOfTrackItemEntity.insert(trackItemEntity);
                    __db.setTransactionSuccessful();
                    return Unit.INSTANCE;
                } finally {
                    __db.endTransaction();
                }
            }
        }, $completion);
    }

    @Override
    public Object removeTrack(final int trackId, final Continuation<? super Unit> $completion) {
        return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
            @Override
            @NonNull
            public Unit call() throws Exception {
                final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveTrack.acquire();
                int _argIndex = 1;
                _stmt.bindLong(_argIndex, trackId);
                try {
                    __db.beginTransaction();
                    try {
                        _stmt.executeUpdateDelete();
                        __db.setTransactionSuccessful();
                        return Unit.INSTANCE;
                    } finally {
                        __db.endTransaction();
                    }
                } finally {
                    __preparedStmtOfRemoveTrack.release(_stmt);
                }
            }
        }, $completion);
    }

    @Override
    public Object getTrack(final int trackId,
                           final Continuation<? super TrackItemEntity> $completion) {
        final String _sql = "SELECT * FROM track_item WHERE id = ?";
        final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
        int _argIndex = 1;
        _statement.bindLong(_argIndex, trackId);
        final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
        return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TrackItemEntity>() {
            @Override
            @NonNull
            public TrackItemEntity call() throws Exception {
                final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
                try {
                    final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
                    final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
                    final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
                    final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
                    final int _cursorIndexOfAverageSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "average_speed");
                    final int _cursorIndexOfGeopoint = CursorUtil.getColumnIndexOrThrow(_cursor, "geopoint");
                    final TrackItemEntity _result;
                    if (_cursor.moveToFirst()) {
                        final Integer _tmpId;
                        if (_cursor.isNull(_cursorIndexOfId)) {
                            _tmpId = null;
                        } else {
                            _tmpId = _cursor.getInt(_cursorIndexOfId);
                        }
                        final String _tmpTime;
                        if (_cursor.isNull(_cursorIndexOfTime)) {
                            _tmpTime = null;
                        } else {
                            _tmpTime = _cursor.getString(_cursorIndexOfTime);
                        }
                        final String _tmpDate;
                        if (_cursor.isNull(_cursorIndexOfDate)) {
                            _tmpDate = null;
                        } else {
                            _tmpDate = _cursor.getString(_cursorIndexOfDate);
                        }
                        final String _tmpDistance;
                        if (_cursor.isNull(_cursorIndexOfDistance)) {
                            _tmpDistance = null;
                        } else {
                            _tmpDistance = _cursor.getString(_cursorIndexOfDistance);
                        }
                        final String _tmpAverageSpeed;
                        if (_cursor.isNull(_cursorIndexOfAverageSpeed)) {
                            _tmpAverageSpeed = null;
                        } else {
                            _tmpAverageSpeed = _cursor.getString(_cursorIndexOfAverageSpeed);
                        }
                        final String _tmpGeopoint;
                        if (_cursor.isNull(_cursorIndexOfGeopoint)) {
                            _tmpGeopoint = null;
                        } else {
                            _tmpGeopoint = _cursor.getString(_cursorIndexOfGeopoint);
                        }
                        _result = new TrackItemEntity(_tmpId,_tmpTime,_tmpDate,_tmpDistance,_tmpAverageSpeed,_tmpGeopoint);
                    } else {
                        _result = null;
                    }
                    return _result;
                } finally {
                    _cursor.close();
                    _statement.release();
                }
            }
        }, $completion);
    }

    @Override
    public Flow<List<TrackItemEntity>> getTrackList() {
        final String _sql = "SELECT * FROM track_item";
        final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
        return CoroutinesRoom.createFlow(__db, false, new String[] {"track_item"}, new Callable<List<TrackItemEntity>>() {
            @Override
            @NonNull
            public List<TrackItemEntity> call() throws Exception {
                final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
                try {
                    final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
                    final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
                    final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
                    final int _cursorIndexOfDistance = CursorUtil.getColumnIndexOrThrow(_cursor, "distance");
                    final int _cursorIndexOfAverageSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "average_speed");
                    final int _cursorIndexOfGeopoint = CursorUtil.getColumnIndexOrThrow(_cursor, "geopoint");
                    final List<TrackItemEntity> _result = new ArrayList<TrackItemEntity>(_cursor.getCount());
                    while (_cursor.moveToNext()) {
                        final TrackItemEntity _item;
                        final Integer _tmpId;
                        if (_cursor.isNull(_cursorIndexOfId)) {
                            _tmpId = null;
                        } else {
                            _tmpId = _cursor.getInt(_cursorIndexOfId);
                        }
                        final String _tmpTime;
                        if (_cursor.isNull(_cursorIndexOfTime)) {
                            _tmpTime = null;
                        } else {
                            _tmpTime = _cursor.getString(_cursorIndexOfTime);
                        }
                        final String _tmpDate;
                        if (_cursor.isNull(_cursorIndexOfDate)) {
                            _tmpDate = null;
                        } else {
                            _tmpDate = _cursor.getString(_cursorIndexOfDate);
                        }
                        final String _tmpDistance;
                        if (_cursor.isNull(_cursorIndexOfDistance)) {
                            _tmpDistance = null;
                        } else {
                            _tmpDistance = _cursor.getString(_cursorIndexOfDistance);
                        }
                        final String _tmpAverageSpeed;
                        if (_cursor.isNull(_cursorIndexOfAverageSpeed)) {
                            _tmpAverageSpeed = null;
                        } else {
                            _tmpAverageSpeed = _cursor.getString(_cursorIndexOfAverageSpeed);
                        }
                        final String _tmpGeopoint;
                        if (_cursor.isNull(_cursorIndexOfGeopoint)) {
                            _tmpGeopoint = null;
                        } else {
                            _tmpGeopoint = _cursor.getString(_cursorIndexOfGeopoint);
                        }
                        _item = new TrackItemEntity(_tmpId,_tmpTime,_tmpDate,_tmpDistance,_tmpAverageSpeed,_tmpGeopoint);
                        _result.add(_item);
                    }
                    return _result;
                } finally {
                    _cursor.close();
                }
            }

            @Override
            protected void finalize() {
                _statement.release();
            }
        });
    }

    @NonNull
    public static List<Class<?>> getRequiredConverters() {
        return Collections.emptyList();
    }
}
