package com.example.notenote;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import java.util.List;

public class NoteAdapter extends BaseAdapter {
    // 定义笔记项点击事件的接口
    public interface OnNoteItemClickListener {
        void onNoteItemClick(long noteId);
    }

    // 定义笔记项长按事件的接口
    public interface OnNoteItemLongClickListener {
        void onNoteItemLongClick(long noteId);
    }

    private Context context;
    private List<Note> noteList;
    private OnNoteItemClickListener onNoteItemClickListener;
    private OnNoteItemLongClickListener onNoteItemLongClickListener; // 长按事件的监听器

    // 构造函数，接收上下文、笔记列表和事件监听器
    public NoteAdapter(Context context, List<Note> noteList, OnNoteItemClickListener listener, OnNoteItemLongClickListener longClickListener) {
        this.context = context;
        this.noteList = noteList;
        this.onNoteItemClickListener = listener;
        this.onNoteItemLongClickListener = longClickListener;
    }

    // 构造函数，接收上下文、笔记列表和点击事件监听器
    public NoteAdapter(Context context, List<Note> noteList, OnNoteItemClickListener listener) {
        this.context = context;
        this.noteList = noteList;
        this.onNoteItemClickListener = listener;
    }

    // 构造函数，接收上下文和笔记列表
    public NoteAdapter(Context context,List<Note> noteList)
    {
        this.context=context;
        this.noteList=noteList;
    }

    // 设置笔记列表
    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取笔记项视图
        View view = View.inflate(context, R.layout.note_disply, null);

        // 获取笔记项中的文本和时间视图
        TextView tv_content = (TextView) view.findViewById(R.id.tv_context);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);

        // 获取笔记内容的文本
        String allText = noteList.get(position).getContent();

        // 设置文本和时间视图的显示内容
        tv_content.setText(allText.split("\n")[0]);
        tv_time.setText(noteList.get(position).getTime());

        // 将笔记项的ID作为标签存储在视图中
        view.setTag(noteList.get(position).getId());

        // 设置笔记项的点击事件监听器
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 触发笔记项点击事件
                if (onNoteItemClickListener != null) {
                    onNoteItemClickListener.onNoteItemClick(noteList.get(position).getId());
                }
            }
        });

        // 设置笔记项的长按事件监听器
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 触发笔记项长按事件
                if (onNoteItemLongClickListener != null) {
                    onNoteItemLongClickListener.onNoteItemLongClick(noteList.get(position).getId());
                }
                return true; // 消耗长按事件
            }
        });

        return view; // 返回笔记项视图
    }
}
