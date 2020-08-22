package com.acoder.itemclickable.marqueeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.acoder.itemclickable.itemclickablemarqueeview.ItemClickAbleMarqueeView;
import com.acoder.itemclickable.itemclickablemarqueeview.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    private ItemClickAbleMarqueeView marqueeView;
    private List<String> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initAll();

        addItemToList();

        initMarqueeView();

    }


    private void addItemToList() {
        list.add("item one item one item one item one item one item one item one item one item one item one item one item one");
        list.add("item two item two item two item two item two item two item two");
        list.add("item three item three item three item three item three ");
        list.add("item four item four item four item four item four item four item four item four");
        list.add("item five item five item five item five");
    }

    private void initMarqueeView() {
        marqueeView.setContent(list);
        marqueeView.setOnMarqueeItemClickListener("bangladesh prothidin",this);
    }

    private void initAll() {
        marqueeView=findViewById(R.id.itemClickAbleMarqueeViewId);
    }

    @Override
    public void onMarqueeItemClickListener(String tag, int position) {
        Toast.makeText(this, "Tag is:- "+tag+" and position is "+position, Toast.LENGTH_SHORT).show();
    }


}