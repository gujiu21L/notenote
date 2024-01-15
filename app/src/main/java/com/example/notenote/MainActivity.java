package com.example.notenote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import android.widget.RelativeLayout;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteItemClickListener , NoteAdapter.OnNoteItemLongClickListener{

    final private String TAG = "FXS";
    // 成员变量声明
    private Context context = this; // 上下文对象，用于数据库操作
    private NoteDatabase dbHelper; // 数据库帮助类
    private NoteAdapter adapter; // 笔记适配器
    private List<Note> noteList = new ArrayList<>(); // 笔记列表
    private FloatingActionButton btn; // 悬浮按钮
    private ListView lv; // 列表视图

    // ActivityResultLauncher 用于处理其他活动的结果
    public ActivityResultLauncher<Intent> someActivityResultLauncher;
    private SearchView searchView; // 搜索视图

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // 恢复原始数据
        setContentView(R.layout.activity_main); // 打开 activity_main 界面
                                                                                                                                                                                                                                                                       
        btn = (FloatingActionButton) findViewById(R.id.fab); // 初始化 fab，便于对 fab 进行操作
        lv = findViewById(R.id.lv); // 列表视图，用于显示数据列表

        adapter = new NoteAdapter(getApplicationContext(), noteList, this, this); // 初始化一个笔记适配器，并将应用的上下文对象和笔记列表传递给适配器
        refreshListView(); // 刷新笔记列表
        lv.setAdapter(adapter); // 将适配器与列表视图关联，从而显示笔记列表中的数据在界面上

        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(16, 16, 16, 16);

        RelativeLayout relativeLayout = findViewById(R.id.relateLayout); // 根据你的布局结构获取 CoordinatorLayout 实例


        searchView = findViewById(R.id.searchView); // 搜索视图 用于在应用程序中提供搜索功能

        // 设置一个侦听器，该侦听器将收到有关 `SearchView` 中查询文本更改的通知
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override  // 查询文本提交
            public boolean onQueryTextSubmit(String query) {
                // 处理搜索提交（如果需要的话） 这里显然不需要
                return false; // 不处理搜索提交 ， 将依赖系统提供的默认行为（关闭键盘）
            }

            @Override // 只要“SearchView”中的文本发生更改，即当用户在搜索框中键入或删除字符时，就会调用此方法。
            public boolean onQueryTextChange(String newText) {
                // 处理文本更改时的搜索
                filterNotes(newText);
                return true; // 完全处理了文本更改时的搜索 ，并将不采取默认行为
                }
            }
        );



        // 初始化 someActivityResultLauncher，用于处理其他活动的结果
        someActivityResultLauncher = registerForActivityResult(
                /*
                第一个参数是`ActivityResultContract<I, O>`接口的实例，
                其中`I`是输入类型（要发送到活动的数据类型），
                `O` 是输出类型（您期望从活动接收的数据类型）。
                 */
                new ActivityResultContracts.StartActivityForResult(),
                /*
                new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                     ....
                    }
                }
                 */
                result -> { // lambda 表达式  result 是 lambda 表达式的（单）参数
                    // 检查活动返回的结果是否为 RESULT_OK
                    if (result.getResultCode() == RESULT_OK) {
                        // 获取从活动返回的数据
                        Intent data = result.getData();
                        if (data != null) {
                            // 从 Intent 中提取笔记内容、时间和ID
                            String content = data.getStringExtra("content");
                            String time = data.getStringExtra("time");
                            long noteId = data.getLongExtra("note_id", -1);

                            // 检查是否是新笔记还是更新现有笔记
                            if (noteId == -1L) {
                                // 如果是新笔记，调用添加新笔记的方法
                                if(!content.isEmpty()) addNewNote(content, time);
                            } else {
                                // 如果是现有笔记，调用更新现有笔记的方法
                                if(!content.isEmpty()) updateExistingNote(noteId, content, time);
                                else deleteNoteById(noteId);
                            }

                            // 刷新笔记列表
                            refreshListView();
                        }
                    }
                }
        );

        // 对 fab 进行操作 设置了事件监听器
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个意图（Intent）对象，用于实现页面跳转，从当前活动（MainActivity）跳转到目标活动（EditActivity）
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                // 通过Intent传递数据，将"note_id"设置为-1L，表示新建笔记，而不是编辑现有笔记
                intent.putExtra("note_id", -1L);
                // 使用ActivityResultLauncher启动活动，并在活动返回后执行回调函数
                someActivityResultLauncher.launch(intent);
            }
        });
    }


    //负责根据在搜索框中输入的新文本过滤注释列表。
    private void filterNotes(String query) {
        // 创建一个新的笔记列表，用于存储过滤后的笔记
        List<Note> filteredList = new ArrayList<>();

        // 遍历原始笔记列表
        for (Note note : noteList) {
            // 判断笔记内容是否包含用户输入的查询条件（不区分大小写）
            if (note.getContent().toLowerCase().contains(query.toLowerCase())) {
                // 笔记内容匹配查询条件，将笔记添加到过滤后的列表中
                filteredList.add(note);
            }
        }

        // 更新适配器的笔记列表为过滤后的列表
        adapter.setNoteList(filteredList);

        // 通知适配器数据已更改，刷新列表视图
        adapter.notifyDataSetChanged();

        // 刷新整个笔记列表视图
        refreshListView();
    }


    @Override
    public void onNoteItemClick(long noteId) {
        // 处理项点击，启动 EditActivity 并传递选定笔记以进行编辑
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("note_id", noteId);

        someActivityResultLauncher.launch(intent);
    }

    // 更新现有笔记
    private void updateExistingNote(long noteId, String content, String time) {
        CRUD op = new CRUD(this);
        op.open();
        Note updatedNote = new Note(content, time);
        updatedNote.setId(noteId);
        op.updateNote(updatedNote);
        op.close();
    }

    // 添加新笔记
    private void addNewNote(String content, String time) {
        CRUD op = new CRUD(this);
        op.open();
        Note newNote = new Note(content, time);
        op.addNote(newNote);
        op.close();
    }

    // 刷新笔记列表
    public void refreshListView() {
        // 创建数据库操作对象，打开数据库连接
        CRUD op = new CRUD(context);
        op.open();

        if (noteList.size() > 0) noteList.clear(); // 清空笔记列表
        noteList.addAll(op.getAllNotes()); // 获取数据库中所有笔记
        op.close(); // 关闭数据库连接

        adapter.notifyDataSetChanged(); // 通知适配器数据已更改，刷新列表视图
    }

    @Override
    public void onNoteItemLongClick(long noteId) {
        showDeleteConfirmationDialog(noteId);
    }

    private void showDeleteConfirmationDialog(final long noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要删除此笔记吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 在确认后删除笔记
                        deleteNoteById(noteId);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 用户取消对话框，什么也不做
                    }
                });
        builder.create().show();
    }

    // 通过 ID 删除笔记的方法
    private void deleteNoteById(long noteId) {
        CRUD op = new CRUD(this);
        op.open();
        op.deleteNoteById(noteId);
        op.close();
        refreshListView(); // 删除后刷新列表
    }


}
