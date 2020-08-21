package com.acoder.itemclickable.marqueeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.acoder.itemclickable.itemclickablemarqueeview.ItemClickAbleMarqueeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        marqueeView.setOnClickListener(this);
    }

    private void initAll() {
        marqueeView=findViewById(R.id.itemClickAbleMarqueeViewId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.itemClickAbleMarqueeViewId:
                Toast.makeText(this, "marquee item clicked at position "+marqueeView.getItemClickedPosition(), Toast.LENGTH_SHORT).show();
                break;
        }
    }


}