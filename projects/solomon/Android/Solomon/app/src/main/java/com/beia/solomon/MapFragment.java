package com.beia.solomon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import static com.beia.solomon.MainActivity.context;

public class MapFragment extends Fragment {

    public View view;

    //Display information variables
    int displayWidth;
    int displayHeight;

    //map variable
    public ImageView mapImageView;
    public Bitmap mapBitmap;
    public Paint mapPaint;

    //gesture variables
    public int pointerCount;
    public ViewConfiguration viewConfig;
    int viewScaledTouchSlop;
    private float primStartTouchEventX = -1;
    private float primStartTouchEventY = -1;
    private float secStartTouchEventX = -1;
    private float secStartTouchEventY = -1;
    private float primSecStartTouchDistance = 0;
    private float deltaDif = 0;

    public MapFragment() {
        this.pointerCount = 0;
    }

    public void setArguments(@Nullable Bundle args, String bundleDataName) {
        super.setArguments(args);
        ArrayList<String> bundleData = args.getStringArrayList(bundleDataName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_fragmet, container, false);

        //get display properties
        DisplayMetrics dm = new DisplayMetrics();
        ((MainActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;

        //implement gestures
        view.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event) {

                int action = (event.getAction() & MotionEvent.ACTION_MASK);
                int pointerCount = event.getPointerCount();
                switch (action)
                {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //the pointer count increments because a finger was pressed
                        Log.d("POINTER DOWN", Integer.toString(event.getPointerCount()));
                        if (pointerCount == 1)
                        {
                            primStartTouchEventX = event.getX(0);
                            primStartTouchEventY = event.getY(0);
                            Log.d("TAG", String.format("POINTER ONE X = %.5f, Y = %.5f", primStartTouchEventX, primStartTouchEventY));
                        }
                        if (pointerCount == 2)
                        {
                            // Starting distance between fingers
                            secStartTouchEventX = event.getX(1);
                            secStartTouchEventY = event.getY(1);
                            primSecStartTouchDistance = distance(event, 0, 1);
                            Log.d("TAG", String.format("POINTER TWO X = %.5f, Y = %.5f, distance = %.5f", secStartTouchEventX, secStartTouchEventY, primSecStartTouchDistance));
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        //the pointer count increments because a finger was pressed
                        Log.d("ACTION DOWN", Integer.toString(event.getPointerCount()));
                        if (pointerCount == 1)
                        {
                            primStartTouchEventX = event.getX(0);
                            primStartTouchEventY = event.getY(0);
                            Log.d("TAG", String.format("POINTER ONE X = %.5f, Y = %.5f", primStartTouchEventX, primStartTouchEventY));
                        }
                        if (pointerCount == 2)
                        {
                            // Starting distance between fingers
                            secStartTouchEventX = event.getX(1);
                            secStartTouchEventY = event.getY(1);
                            primSecStartTouchDistance = distance(event, 0, 1);
                            Log.d("TAG", String.format("POINTER TWO X = %.5f, Y = %.5f, distance = %.5f", secStartTouchEventX, secStartTouchEventY, primSecStartTouchDistance));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //pointer count decrements(the finger was lifted)
                        if (pointerCount < 2) {
                            secStartTouchEventX = -1;
                            secStartTouchEventY = -1;
                        }
                        if (pointerCount < 1) {
                            primStartTouchEventX = -1;
                            primStartTouchEventY = -1;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        //pointer count decrements(the finger was lifted)
                        if (pointerCount < 2) {
                            secStartTouchEventX = -1;
                            secStartTouchEventY = -1;
                        }
                        if (pointerCount < 1) {
                            primStartTouchEventX = -1;
                            primStartTouchEventY = -1;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        boolean isPrimMoving = isScrollGesture(event, 0, primStartTouchEventX, primStartTouchEventY);
                        boolean isSecMoving = (event.getPointerCount() > 1 && isScrollGesture(event, 1, secStartTouchEventX, secStartTouchEventY));
                        // There is a chance that the gesture may be a scroll
                        if (isPrimMoving && isSecMoving && isPinchGesture(event))
                        {
                            //Log.d("TAG", "PINCH");
                            //zoom in
                            if(deltaDif > 0 && mapImageView.getScaleX() < 2 && mapImageView.getScaleY() < 2)
                            {
                                double currentScaleX = mapImageView.getScaleX();
                                double xScaleMultiplier = 0.04;
                                double increasedScaleX = currentScaleX + (xScaleMultiplier * currentScaleX);
                                double currentScaleY = mapImageView.getScaleY();
                                double yScaleMultiplier = 0.04;
                                double increasedScaleY = currentScaleY + (yScaleMultiplier * currentScaleY);
                                mapImageView.setScaleX((float)increasedScaleX);
                                mapImageView.setScaleY((float)increasedScaleY);
                            }

                            //zoom out
                            if(deltaDif < 0 && mapImageView.getScaleX() > 1 && mapImageView.getScaleY() > 1)
                            {
                                double currentScaleX = mapImageView.getScaleX();
                                double xScaleMultiplier = 0.04;
                                double decreasedScaleX = currentScaleX - (xScaleMultiplier * currentScaleX);
                                double currentScaleY = mapImageView.getScaleY();
                                double yScaleMultiplier = 0.04;
                                double decreasedScaleY = currentScaleY - (yScaleMultiplier * currentScaleY);
                                mapImageView.setScaleX((float)decreasedScaleX);
                                mapImageView.setScaleY((float)decreasedScaleY);
                            }
                        }
                        else if (isPrimMoving || isSecMoving)
                        {
                            // A 1 finger or 2 finger scroll.
                            if (isPrimMoving && isSecMoving)
                            {
                                Log.d("TAG", "Two finger scroll");
                            }
                            else
                            {
                                Log.d("TAG", "One finger scroll");
                            }
                        }
                        break;
                }
                return true;
            }
        });
        initUI();

        //setup the touch movement thereshold
        viewConfig = ViewConfiguration.get(view.getContext());
        viewScaledTouchSlop = viewConfig.getScaledTouchSlop();

        //setup map's initial state
        Bitmap tempBitmap = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(mapBitmap, 0, 0, null);
        tempCanvas.drawRect(100, 100, 200, 200, mapPaint);
        mapImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
        return view;
    }

    public void initUI()
    {
        mapImageView = view.findViewById(R.id.mapImageView);
        mapImageView.setImageResource(R.drawable.afi_map);
        mapBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.afi_map);
        mapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mapPaint.setStyle(Paint.Style.STROKE);
        mapPaint.setStrokeWidth(10);
        mapPaint.setColor(ContextCompat.getColor(getContext(), R.color.mapRoadAcent));
    }

    public float distance(MotionEvent event, int firstPointerIndex, int secondPointerIndex) {
        if (event.getPointerCount() >= 2)
        {
            float xSquared = (float) Math.pow(event.getX(secondPointerIndex) - event.getX(firstPointerIndex), 2);
            float ySquared = (float) Math.pow(event.getY(secondPointerIndex) - event.getY(firstPointerIndex), 2);
            return (float) Math.sqrt(xSquared + ySquared);
        }
        else
        {
            return 0;
        }
    }


    //gesture patterns methods
    private boolean isScrollGesture(MotionEvent event, int ptrIndex, float originalX, float originalY)
    {
        //we will use this method to detect if the pointer index that we detected moved in a certain direction
        //we will use it in MotionEvent.ACTION_MOVE, we compute the distance between the first touch and the position of the finger after moving it on the screen
        float moveX = Math.abs(event.getX(ptrIndex) - originalX);
        float moveY = Math.abs(event.getY(ptrIndex) - originalY);
        if (moveX > viewScaledTouchSlop || moveY > viewScaledTouchSlop)
        {
            return true;
        }
        return false;
    }
    private boolean isPinchGesture(MotionEvent event)
    {
        if (event.getPointerCount() == 2)
        {
            final float distanceCurrent = distance(event, 0, 1);
            final float diffPrimX = primStartTouchEventX - event.getX(0);
            final float diffPrimY = primStartTouchEventY - event.getY(0);
            final float diffSecX = secStartTouchEventX - event.getX(1);
            final float diffSecY = secStartTouchEventY - event.getY(1);
            deltaDif = distanceCurrent - primSecStartTouchDistance;
            //we check if the distance between the two fingers is greater than a thereshold
            //when we pinch the fingers spread the x or y differece between the initial position of a finger and the final position of a fingar must be negative for at least one finger because one goes down and one goes up
            if (Math.abs(deltaDif) > viewScaledTouchSlop &&((diffPrimY * diffSecY) <= 0 || (diffPrimX * diffSecX) <= 0))
            {
                return true;
            }
        }
        return false;
    }

}
