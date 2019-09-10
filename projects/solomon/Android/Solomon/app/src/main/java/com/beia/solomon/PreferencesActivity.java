package com.beia.solomon;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beia.solomon.runnables.UpdateImageLocationRunnable;
import java.util.Random;

public class PreferencesActivity extends AppCompatActivity {

    public static int displayWidth;
    public static int displayHeight;
    public static RelativeLayout preferencesRelativeLayout;

    //preferences images and data related to each preference
    //shoes
    public static LinearLayout shoesLayout;
    public static ImageView shoesImageView;
    public static TextView shoesTextView;
    public static int shoesImageWidth;
    public static int shoesImageHeight;
    public static Thread updateShoesImageLocation;
    public static volatile boolean shoesSelected;

    //electronics
    public static LinearLayout electronicsLayout;
    public static ImageView electronicsImageView;
    public static TextView electronicsTextView;
    public static int electronicsImageWidth;
    public static int electronicsImageHeight;
    public static Thread updateElectronicsImageLocation;
    public static volatile boolean electronicsSelected;

    //clothes
    public static LinearLayout clothesLayout;
    public static ImageView clothesImageView;
    public static TextView clothesTextView;
    public static int clothesImageWidth;
    public static int clothesImageHeight;
    public static Thread updateClothesImageLocation;
    public static volatile boolean clothesSelected;

    //food
    public static LinearLayout foodLayout;
    public static ImageView foodImageView;
    public static TextView foodTextView;
    public static int foodImageWidth;
    public static int foodImageHeight;
    public static Thread updateFoodImageLocation;
    public static volatile boolean foodSelected;

    //cofee
    public static LinearLayout cofeeLayout;
    public static ImageView cofeeImageView;
    public static TextView cofeeTextView;
    public static int cofeeImageWidth;
    public static int cofeeImageHeight;
    public static Thread updateCofeeImageLocation;
    public static volatile boolean cofeeSelected;

    //sports
    public static LinearLayout sportsLayout;
    public static ImageView sportsImageView;
    public static TextView sportsTextView;
    public static int sportsImageWidth;
    public static int sportsImageHeight;
    public static Thread updateSportsImageLocation;
    public static volatile boolean sportsSelected;



    public static Handler handler1 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change shoes position
                    int[] incrementXYShoes = (int[])msg.obj;
                    RelativeLayout.LayoutParams paramsShoes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                    paramsShoes.leftMargin =  shoesLayout.getLeft() + incrementXYShoes[0];//XCOORD
                    paramsShoes.topMargin = shoesLayout.getTop() + incrementXYShoes[1]; //YCOORD
                    shoesLayout.setLayoutParams(paramsShoes);
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change electronics position
                    int[] incrementXYElectronics = (int[])msg.obj;
                    RelativeLayout.LayoutParams paramsElectronics = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                    paramsElectronics.leftMargin =  electronicsLayout.getLeft() + incrementXYElectronics[0];//XCOORD
                    paramsElectronics.topMargin = electronicsLayout.getTop() + incrementXYElectronics[1]; //YCOORD
                    electronicsLayout.setLayoutParams(paramsElectronics);
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler3 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change clothes position
                    int[] incrementXYClothes = (int[])msg.obj;
                    RelativeLayout.LayoutParams paramsClothes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                    paramsClothes.leftMargin =  clothesLayout.getLeft() + incrementXYClothes[0];//XCOORD
                    paramsClothes.topMargin = clothesLayout.getTop() + incrementXYClothes[1]; //YCOORD
                    clothesLayout.setLayoutParams(paramsClothes);
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler4 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change food position
                    int[] incrementXYFood = (int[])msg.obj;
                    RelativeLayout.LayoutParams paramsFood = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                    paramsFood.leftMargin =  foodLayout.getLeft() + incrementXYFood[0];//XCOORD
                    paramsFood.topMargin = foodLayout.getTop() + incrementXYFood[1]; //YCOORD
                    foodLayout.setLayoutParams(paramsFood);
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler5 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change cofee position
                    int[] incrementXYCofee = (int[])msg.obj;
                    RelativeLayout.LayoutParams paramsCofee = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                    paramsCofee.leftMargin =  cofeeLayout.getLeft() + incrementXYCofee[0];//XCOORD
                    paramsCofee.topMargin = cofeeLayout.getTop() + incrementXYCofee[1]; //YCOORD
                    cofeeLayout.setLayoutParams(paramsCofee);
                    break;
                default:
                    break;
            }
        }
    };
    public static Handler handler6 = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case 1://change sports position
                    int[] incrementXYSports = (int[])msg.obj;
                    RelativeLayout.LayoutParams paramsSports = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                    paramsSports.leftMargin =  sportsLayout.getLeft() + incrementXYSports[0];//XCOORD
                    paramsSports.topMargin = sportsLayout.getTop() + incrementXYSports[1]; //YCOORD
                    sportsLayout.setLayoutParams(paramsSports);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_preferences);
        initUI();

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
        preferencesRelativeLayout.post(new Runnable() {
            @Override
            public void run() {
                //set the initial location of the shoes preference
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random random = new Random();
                params.leftMargin = random.nextInt(displayWidth - shoesLayout.getMeasuredWidth()); //XCOORD
                params.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                shoesLayout.setLayoutParams(params);
                //update the shoes location
                updateShoesImageLocation = new Thread(new UpdateImageLocationRunnable(shoesLayout, shoesImageWidth, shoesImageHeight, displayWidth, displayHeight, false, "shoes"));
                updateShoesImageLocation.start();

                //set the initial location of the electronics preference
                RelativeLayout.LayoutParams paramsElectronics = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomElectronics = new Random();
                paramsElectronics.leftMargin = randomElectronics.nextInt(displayWidth - electronicsLayout.getMeasuredWidth()); //XCOORD
                paramsElectronics.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                electronicsLayout.setLayoutParams(paramsElectronics);
                //update the electronics location
                updateElectronicsImageLocation = new Thread(new UpdateImageLocationRunnable(electronicsLayout, electronicsImageWidth, electronicsImageHeight, displayWidth, displayHeight, false, "electronics"));
                updateElectronicsImageLocation.start();

                //set the initial location of the clothes preference
                RelativeLayout.LayoutParams paramsClothes = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomClothes = new Random();
                paramsClothes.leftMargin = randomClothes.nextInt(displayWidth - clothesLayout.getMeasuredWidth()); //XCOORD
                paramsClothes.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                clothesLayout.setLayoutParams(paramsClothes);
                //update the clothes location
                updateClothesImageLocation = new Thread(new UpdateImageLocationRunnable(clothesLayout, clothesImageWidth, clothesImageHeight, displayWidth, displayHeight, false, "clothes"));
                updateClothesImageLocation.start();

                //set the initial location of the food preference
                RelativeLayout.LayoutParams paramsFood = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomFood = new Random();
                paramsFood.leftMargin = randomFood.nextInt(displayWidth - foodLayout.getMeasuredWidth()); //XCOORD
                paramsFood.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                foodLayout.setLayoutParams(paramsFood);
                //update the food location
                updateFoodImageLocation = new Thread(new UpdateImageLocationRunnable(foodLayout, foodImageWidth, foodImageHeight, displayWidth, displayHeight, false, "food"));
                updateFoodImageLocation.start();

                //set the initial location of the cofee preference
                RelativeLayout.LayoutParams paramsCofee = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomCofee = new Random();
                paramsCofee.leftMargin = randomCofee.nextInt(displayWidth - cofeeLayout.getMeasuredWidth()); //XCOORD
                paramsCofee.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                cofeeLayout.setLayoutParams(paramsCofee);
                //update the cofee location
                updateCofeeImageLocation = new Thread(new UpdateImageLocationRunnable(cofeeLayout, cofeeImageWidth, cofeeImageHeight, displayWidth, displayHeight, false, "cofee"));
                updateCofeeImageLocation.start();

                //set the initial location of the sports preference
                RelativeLayout.LayoutParams paramsSports = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
                Random randomSports = new Random();
                paramsSports.leftMargin = randomSports.nextInt(displayWidth - sportsLayout.getMeasuredWidth()); //XCOORD
                paramsSports.topMargin = random.nextInt((int)(displayHeight / 2)); //YCOORD
                sportsLayout.setLayoutParams(paramsSports);
                //update the sports location
                updateSportsImageLocation = new Thread(new UpdateImageLocationRunnable(sportsLayout, sportsImageWidth, sportsImageHeight, displayWidth, displayHeight, false, "sports"));
                updateSportsImageLocation.start();
            }
        });
    }
    public void initUI()
    {
        preferencesRelativeLayout = findViewById(R.id.preferencesLayout);

        //find shoes data
        shoesImageView = findViewById(R.id.shoes);
        shoesTextView = findViewById(R.id.shoesText);
        shoesLayout = findViewById(R.id.shoesLayout);


        //find electronics data
        electronicsImageView = findViewById(R.id.eletronics);
        electronicsTextView = findViewById(R.id.electronicsText);
        electronicsLayout = findViewById(R.id.electronicsLayout);

        //find clothes data
        clothesImageView = findViewById(R.id.clothes);
        clothesTextView = findViewById(R.id.clothesText);
        clothesLayout = findViewById(R.id.clothesLayout);

        //find food data
        foodImageView = findViewById(R.id.food);
        foodTextView = findViewById(R.id.foodText);
        foodLayout = findViewById(R.id.foodLayout);

        //find cofee data
        cofeeImageView = findViewById(R.id.cofee);
        cofeeTextView = findViewById(R.id.cofeeText);
        cofeeLayout = findViewById(R.id.cofeeLayout);

        //find sports data
        sportsImageView = findViewById(R.id.sports);
        sportsTextView = findViewById(R.id.sportsText);
        sportsLayout = findViewById(R.id.sportsLayout);
    }
}
