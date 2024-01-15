package com.example.notenote;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    EditText editText ;
    private String content;
    private String time;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
        editText = findViewById(R.id.edit);

        // 检查是否存在note_id额外信息
        long noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId != -1) {
            // 加载现有笔记数据以便编辑
            loadNoteData(noteId);
        }
    }

    // 方法用于加载现有笔记数据
    private void loadNoteData(long noteId) {
        // 使用CRUD或其他方法根据noteId检索现有笔记数据
        CRUD op = new CRUD(this);
        op.open();
        Note existingNote = op.getNoteById(noteId);
        op.close();

        // 用现有笔记内容填充EditText
        editText.setText(existingNote.getContent());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        // 按键事件处理，如果按下的是Home键，直接返回true，以表明 Home 键事件被正常处理
        if (keyCode == keyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 如果按下的是Back键
            Intent intent = new Intent();
            // 将EditText中的文本内容作为额外信息存入Intent
            intent.putExtra("content", editText.getText().toString());
            // 将当前时间格式化后的字符串作为额外信息存入Intent
            intent.putExtra("time", dateToStringing());
            // 获取传递的note_id额外信息，默认为-1
            long noteId = getIntent().getLongExtra("note_id", -1L);

            if (noteId == -1L) {
                // 对于新笔记，直接传递数据
                setResult(RESULT_OK, intent);
            } else {
                // 对于现有笔记，传递ID
                intent.putExtra("note_id", noteId);
                setResult(RESULT_OK, intent);
            }

            finish();// 结束当前活动
            return true;
        }
        // 调用父类的onKeyDown方法处理其他按键事件
        return super.onKeyDown(keyCode, keyEvent); // 表示已处理 Back 按下，阻止系统执行其默认操作。
        // 注：如果删除 `finish();` 语句，则按下 Back 键时当前活动 (`EditActivity`) 将不会关闭。
    }


    // 将日期转为字符串的方法
    public String dateToStringing(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        return simpleDateFormat.format(date);
    }

}
