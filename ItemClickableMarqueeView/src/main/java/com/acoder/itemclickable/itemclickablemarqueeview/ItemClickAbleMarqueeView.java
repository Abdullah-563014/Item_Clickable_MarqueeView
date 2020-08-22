package com.acoder.itemclickable.itemclickablemarqueeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;

import com.acoder.itemclickable.itemclickablemarqueeview.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemClickAbleMarqueeView extends View implements Runnable {


    private String TAG="ItemClickAbleMarqueeView";
    private String string;  //Final drawn text
    private float speed = 1;    //Moving speed
    private int textColor = Color.BLACK;    //Text color, default black
    private float textSize = 15;    //text size,default 15
    private int textdistance;
    private int textDistance1= 50;  //item spacing, dp unit
    private String black_count = "";    //Space distance

    private int repetType = REPET_INTERVAL; //Scroll mode
    public static final int REPET_ONCETIME = 0; //End once
    public static final int REPET_INTERVAL = 1; //After one time is over, continue for the second time
    public static final int REPET_CONTINUOUS = 2;   //Immediately after rolling a second time

    private float startLocationDistance = 1.0f; //The starting position is selected, the percentage is from the left, 0~1, 0 means no spacing, 1 means from the right, 1/2 means the middle.

    private boolean isClickStop = true; //Click whether to pause
    private boolean isResetLocation = true; //The default is true
    private float xLocation = 0;    //The x coordinate of the text
    private int contentWidth;   //Content width

    private boolean isRoll = false; //Whether to continue scrolling
    private float oneBlack_width;   //The width of the space

    private TextPaint paint;    //brush
    private Rect rect;

    private int repetCount = 0;
    private boolean resetInit = true;

    private Thread thread;
    private String content = "";

    private float textHeight;
    private List<String> temporaryList=new ArrayList<>();
    private int itemClickedPosition=0;
    private ItemClickListener itemClickListener;
    private Map<Integer, Float> itemWithWidth=new HashMap<>();
    float temporaryValue=0;
    float xPosition=0;
    private String objectTag=null;




    public ItemClickAbleMarqueeView(Context context) {
        this(context,null);
    }

    public ItemClickAbleMarqueeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemClickAbleMarqueeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initattrs(attrs);
        initpaint();
        initClick();
        initTouch();
    }

    @Override
    public void run() {
        while (isRoll && !TextUtils.isEmpty(content)) {
            try {
                Thread.sleep(10);
                xLocation = xLocation - speed;
                postInvalidate();//Redraw the view every 10 milliseconds


                if (xLocation>=0){
                    temporaryValue=0;
                }
                float totalItemLength=0;
                temporaryValue=temporaryValue+speed;
                for (int i=0; i<itemWithWidth.size(); i++) {
                    totalItemLength=totalItemLength+itemWithWidth.get(i);
                    if (totalItemLength>=temporaryValue){
                        i=itemWithWidth.size()-1;
                    }
                }

                Log.d(TAG,"==="+totalItemLength+"==="+temporaryValue+"==="+itemClickedPosition);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (resetInit) {
            setTextDistance(textDistance1);

            if (startLocationDistance < 0) {
                startLocationDistance = 0;
            } else if (startLocationDistance > 1) {
                startLocationDistance = 1;
            }
            xLocation = getWidth() * startLocationDistance;
            Log.d(TAG, "onMeasure: --- " + xLocation);
            resetInit = false;
        }


        //Need to determine the scroll mode
        switch (repetType) {
            case REPET_ONCETIME:
                if (contentWidth < (-xLocation)) {
                    //In other words, the text is over
                    //  Stop the thread at this time
                    stopRoll();
                }
                break;

            case REPET_INTERVAL:
                if (contentWidth <= (-xLocation)) {
                    //In other words, the text is over
                    xLocation = getWidth();
                }
                break;

            case REPET_CONTINUOUS:
                if (xLocation < 0) {
                    int beAppend = (int) ((-xLocation) / contentWidth);
                    Log.d(TAG, "onDraw: ---" + contentWidth + "--------" + (-xLocation) + "------" + beAppend);
                    if (beAppend >= repetCount) {
                        repetCount++;
                        //In other words, the text is over
//                    xLocation = speed;//There is a problem with this method, so the method of appending a string is adopted
                        string = string + content;
                    }
                }
                //The xLocation that needs to be judged here needs to be added with the corresponding width
                break;

            default:
                //The default is all right
                if (contentWidth < (-xLocation)) {
                    //In other words, the text is over
                    //  Stop the thread at this time
                    stopRoll();
                }
                break;
        }
        //Draw the text
        if (string != null) {
            canvas.drawText(string, xLocation, getHeight() / 2 + textHeight / 2, paint);
        }
    }

    //override function ended.
    //=============================================



    public void setOnMarqueeItemClickListener(String objectTag,ItemClickListener itemClickListener) {
        this.objectTag=objectTag;
        this.itemClickListener=itemClickListener;
    }

    private void getIndividualItemWidth() {
        itemWithWidth.clear();
        oneBlack_width = getBlacktWidth();//The width of the space
        int textdistance2 = dp2px(textDistance1);
        int count = (int) (textdistance2 / oneBlack_width);

        for (int i=0; i<temporaryList.size(); i++) {
            String temporaryString=temporaryList.get(i);
            if ((temporaryList.size()-1)==i){
                itemWithWidth.put(i,getContentWidth(temporaryString)+(getWidth()*startLocationDistance));
            }else {
                itemWithWidth.put(i,getContentWidth(temporaryString)+(oneBlack_width*count));
            }
        }
    }

    private void initTouch() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_UP){
                    xPosition=event.getX();
                    float totalItemLength=0;
                    for (int i=0; i<itemWithWidth.size(); i++) {
                        totalItemLength=totalItemLength+itemWithWidth.get(i);
                        if (totalItemLength>=(temporaryValue+xPosition)){
                            itemClickedPosition=i;
                            i=itemWithWidth.size()-1;
                        }
                    }
                    if (itemClickListener!=null){
                        itemClickListener.onMarqueeItemClickListener(objectTag,itemClickedPosition);
                    }
                }
                return true;
            }
        });
    }

    public int getItemClickedPosition() {
        return itemClickedPosition;
    }

    private void initClick() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isClickStop) {
                    if (isRoll) {
                        stopRoll();
                    } else {
                        continueRoll();
                    }
                }
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void initattrs(AttributeSet attrs) {
        TintTypedArray tta = TintTypedArray.obtainStyledAttributes(getContext(), attrs, R.styleable.ItemClickAbleMarqueeView);

        textColor = tta.getColor(R.styleable.ItemClickAbleMarqueeView_marqueeview_text_color, textColor);
        isClickStop = tta.getBoolean(R.styleable.ItemClickAbleMarqueeView_marqueeview_isclickalbe_stop, isClickStop);
        isResetLocation = tta.getBoolean(R.styleable.ItemClickAbleMarqueeView_marqueeview_is_resetLocation, isResetLocation);
        speed = tta.getFloat(R.styleable.ItemClickAbleMarqueeView_marqueeview_text_speed, speed);
        textSize = tta.getFloat(R.styleable.ItemClickAbleMarqueeView_marqueeview_text_size, textSize);
        textDistance1 = tta.getInteger(R.styleable.ItemClickAbleMarqueeView_marqueeview_text_distance, textDistance1);
        startLocationDistance = tta.getFloat(R.styleable.ItemClickAbleMarqueeView_marqueeview_text_startlocationdistance, startLocationDistance);
        repetType = tta.getInt(R.styleable.ItemClickAbleMarqueeView_marqueeview_repet_type, repetType);
        tta.recycle();
    }

    private void initpaint() {
        rect = new Rect();
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);//Initialize the text brush
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);//Text color value, can not be set
        paint.setTextSize(dp2px(textSize));//font size
    }

    public int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setRepetType(int repetType) {
        this.repetType = repetType;
        resetInit = true;
        setContent(content);
    }


    // Keep scrolling
    public void continueRoll() {
        if (!isRoll) {
            if (thread != null) {
                thread.interrupt();

                thread = null;
            }

            isRoll = true;
            thread = new Thread(this);
            thread.start();//Turn on the endless loop thread to make the text move

        }
    }


    // Stop Scrolling
    public void stopRoll() {
        isRoll = false;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

    }


    // Click whether to pause, the default is not
    private void setClickStop(boolean isClickStop) {
        this.isClickStop = isClickStop;
    }



    // Whether to scroll
    private void setContinueble(int isContinuable) {
        this.repetType = isContinuable;
    }

//    /**
//     * 是否反向
//     *
//     * @param isResversable
//     */
//    private void setReversalble(boolean isResversable) {
//        this.isResversable = isResversable;
//    }


    /**
     * Set text spacing However, if the content is in the form of a List, this method is not applicable. For the data source of the list, this method must be called before setting setContent.
     * @param textdistance2
     */
    public void setTextDistance(int textdistance2) {
        //After setting it needs to be initialized
        String black = " ";
        oneBlack_width = getBlacktWidth();//The width of the space
        textdistance2 = dp2px(textdistance2);
        int count = (int) (textdistance2 / oneBlack_width);//The number of spaces is a bit rough, interested friends can fine

        if (count == 0) {
            count = 1;
        }

        textdistance = (int) (oneBlack_width * count);
        black_count = "";
        for (int i = 0; i <= count; i++) {
            black_count = black_count + black;//Interval string
        }
        setContent(content);//Refresh the content distance after setting the spacing, but if the content is in List form, this method is not applicable
    }

    /**
     * Calculate the width of a space
     * @return
     */
    private float getBlacktWidth() {
        String text1 = "en en";
        String text2 = "enen";
        return getContentWidth(text1) - getContentWidth(text2);

    }

    private float getContentWidth(String black) {
        if (black == null || black == "") {
            return 0;
        }
        if (rect == null) {
            rect = new Rect();
        }
        paint.getTextBounds(black, 0, black.length(), rect);
        textHeight = getContentHeight();

        return rect.width();
    }

    /**
     * Explained in detail
     *
     * @param
     * @return
     */
    private float getContentHeight() {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return Math.abs((fontMetrics.bottom - fontMetrics.top)) / 2;
    }

    /**
     * Set text color
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        if (textColor != 0) {
            this.textColor = textColor;
            paint.setColor(getResources().getColor(textColor));//文字颜色值,可以不设定
        }
    }

    /**
     * Set text size
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        if (textSize > 0) {
            this.textSize = textSize;
            paint.setTextSize(dp2px(textSize));//Text color value, can not be set
            contentWidth = (int) (getContentWidth(content) + textdistance);//The size changes, need to recalculate the width and height
        }
    }

    /**
     * Set scroll speed
     *
     * @param speed
     */
    public void setTextSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Set the content of the scrolling entry, in the form of a collection
     *
     * @param strings
     */
    public void setContent(List<String> strings) {
        setTextDistance(textDistance1);
        temporaryList.clear();
        temporaryList.addAll(strings);
        getIndividualItemWidth();
        String temString = "";
        if (strings != null && strings.size() != 0) {
            for (int i = 0; i <strings.size(); i++) {
                temString = temString+strings.get(i) + black_count;
            }
        }
        setContent(temString);
    }

    /**
     * Set the content of the scrolling entry in the form of a string
     *
     * @parambt_control00
     */
    private void setContent(String content2) {
        if (TextUtils.isEmpty(content2)){
            return;
        }
        if (isResetLocation) {//Control whether to initialize xLocation when resetting the text content.
            xLocation = getWidth() * startLocationDistance;
        }

        if (!content2.endsWith(black_count)) {
            content2 = content2 + black_count;//Avoid no suffix
        }
        this.content = content2;

        //Here you need to calculate the width, of course it must be done according to the mode
        if (repetType == REPET_CONTINUOUS) {
//If it is a loop, you need to calculate the width of the text, and then according to the screen width, see that one screen can hold several texts

            contentWidth = (int) (getContentWidth(content) + textdistance);//Can be understood as the length of a unit content
            //Start counting the number of repetitions from 0, otherwise it will pass this hurdle and disappear in the end.
            repetCount = 0;
            int contentCount = (getWidth() / contentWidth) + 2;
            this.string = "";
            for (int i = 0; i <= contentCount; i++) {
                this.string = this.string + this.content;//According to the number of repetitions to superimpose.
            }

        } else {
            if (xLocation < 0 && repetType == REPET_ONCETIME) {
                if (-xLocation > contentWidth) {
                    xLocation = getWidth() * startLocationDistance;
                }
            }
            contentWidth = (int) getContentWidth(content);

            this.string = content2;
        }

        if (!isRoll) {//If it is not scrolling, restart thread scrolling
            continueRoll();
        }


    }

    /**
     * Whether to initialize the position when adding new content
     *
     * @param isReset
     */
    private void setResetLocation(boolean isReset) {
        isResetLocation = isReset;
    }

    public void appendContent(String appendContent) {
//Interested friends can improve by themselves, and add new announcements silently on the existing basis
    }



}
