package com.example.notenote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CRUD {
    SQLiteOpenHelper dbHandler; // SQLiteOpenHelper 实例用于处理数据库连接
    SQLiteDatabase db; // SQLiteDatabase 实例用于执行数据库操作

    // 定义数据库表的列名
    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME
    };

    // 构造方法，接受上下文参数
    public CRUD(Context context) {
        dbHandler = new NoteDatabase(context); // 初始化数据库处理器
    }

    // 打开数据库连接
    public void open() {
        db = dbHandler.getWritableDatabase(); // 获取可写的数据库连接
    }

    // 关闭数据库连接
    public void close() {
        dbHandler.close(); // 关闭数据库处理器
    }

    // 添加一条笔记记录
    public Note addNote(Note note) {
        ContentValues contentValues = new ContentValues(); // 创建一个用于存储数据的 ContentValues 对象
        contentValues.put(NoteDatabase.CONTENT, note.getContent()); // 添加内容
        contentValues.put(NoteDatabase.TIME, note.getTime()); // 添加时间
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues); // 将数据插入数据库
        note.setId(insertId); // 将插入后的 ID 设置到笔记对象中
        return note; // 返回包含新数据的笔记对象
    }

    // 获取所有笔记
    public List<Note> getAllNotes() {
        Cursor cursor = db.query(
                NoteDatabase.TABLE_NAME,  // 表名
                columns,                // 要查询的列（在这里是ID、内容、时间）
                null,                   // 查询条件（null表示无特殊条件）
                null,                   // 查询条件参数（null表示无特殊条件）
                null,                   // 分组方式（null表示不分组）
                null,                   // 过滤方式（null表示不过滤）
                null                    // 排序方式（null表示不排序）
        );

        List<Note> notes = new ArrayList<>(); // 创建一个笔记列表用于存储查询结果
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Note note = new Note(); // 创建笔记对象
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID))); // 设置 ID
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT))); // 设置内容
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME))); // 设置时间
                notes.add(note); // 将笔记对象添加到列表中
            }
        }
        cursor.close(); // 关闭游标
        return notes; // 返回包含所有笔记记录的列表
    }

    // 根据 ID 获取笔记
    public Note getNoteById(long noteId) {
        // 查询数据库，获取指定 ID 的笔记记录
        Cursor cursor = db.query(
                NoteDatabase.TABLE_NAME,   // 表名
                columns,                   // 要查询的列（在这里是ID、内容、时间）
                NoteDatabase.ID + "=?",    // 查询条件（通过 ID 进行查询）
                new String[]{String.valueOf(noteId)},  // 查询条件参数（指定要查询的 ID 值）
                null,                      // 分组方式（null表示不分组）
                null,                      // 过滤方式（null表示不过滤）
                null                       // 排序方式（null表示不排序）
        );

        Note note = null;
        if (cursor.moveToFirst()) {
            // 如果查询到结果，则创建新的笔记对象并设置其属性
            note = new Note();
            note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID))); // 设置 ID
            note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT))); // 设置内容
            note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME))); // 设置时间
        }

        cursor.close(); // 关闭游标，释放资源
        return note; // 返回获取到的笔记对象，如果未找到则返回 null
    }


    // 更新笔记
    public void updateNote(Note note) {
        // 创建一个 ContentValues 对象，用于存储要更新的数据
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.CONTENT, note.getContent());
        values.put(NoteDatabase.TIME, note.getTime());

        // 执行数据库更新操作
        db.update(
                NoteDatabase.TABLE_NAME,        // 表名
                values,                         // 更新的内容值
                NoteDatabase.ID + "=?",        // 更新条件（通过 ID 进行更新）
                //`"=?"` 是一个占位符，它表示在 SQL 查询中使用参数。这是一种防止 SQL 注入攻击的方式。在这里，它表示将在这个位置上填入具体的数值。
                new String[]{String.valueOf(note.getId())}  // 更新条件参数（指定要更新的 ID 值）
                //创建一个字符串数组，数组中包含了要替代占位符 `"=?"` 的具体数值。在这里，它包含了笔记对象的 ID。
        );
    }

    // 根据 ID 删除笔记
    public void deleteNoteById(long noteId) {
        // 执行删除操作，根据 ID 删除指定笔记
        db.delete(
                NoteDatabase.TABLE_NAME,
                NoteDatabase.ID + "=?",
                new String[]{String.valueOf(noteId)}
        );
    }

}
