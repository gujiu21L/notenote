package com.example.notenote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabase extends SQLiteOpenHelper {
    // 定义数据库表和字段的常量
    public static final String TABLE_NAME = "notes"; // 表名
    public static final String CONTENT = "content"; // 内容字段
    public static final String ID = "_id"; // 主键字段
    public static final String TIME = "time"; // 时间字段

    // 构造方法，接受上下文参数
    public NoteDatabase(Context context) {
        super(context, "notes", null, 1); // 创建数据库，指定名称和版本
    }

    // 当数据库首次创建时会调用此方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 执行 SQL 语句创建名为 "notes" 的表
        // "CREATE TABLE notes(_id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, time TEXT NOT NULL)"
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // 主键字段，自增长
                + CONTENT + " TEXT NOT NULL, " // 内容字段，不能为空
                + TIME + " TEXT NOT NULL"
                        + ")"
        ); // 时间字段，不能为空
    }


    // 当数据库需要升级时会调用此方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 在数据库升级时的操作，此处不需要实现
    }
}
