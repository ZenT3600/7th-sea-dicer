package it.matteoleggio.seventhseadicer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;

import it.matteoleggio.seventhseadicer.R;
import it.matteoleggio.seventhseadicer.databinding.ActivityMainBinding;
import it.matteoleggio.seventhseadicer.databinding.ContentMainBinding;
import it.matteoleggio.seventhseadicer.sensors.ShakeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DicerViewModel dicerViewModel;

    private ActivityMainBinding binding;
    private ContentMainBinding contentMainBinding;

    private ImageView[] imageViews;
    private ImageView[] pairViews;
    private boolean shakingEnabled;
    private boolean vibrationEnabled;
    private SharedPreferences sharedPreferences;

    // for Shaking
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeListener shakeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        contentMainBinding = ContentMainBinding.bind(binding.getRoot());
        setContentView(binding.getRoot());

        dicerViewModel = new ViewModelProvider(this).get(DicerViewModel.class);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        initResources();

        dicerViewModel.getDicerLiveData().observe(this, new Observer<int[]>() {
            @Override
            public void onChanged(int[] dice) {
                displaySum(dice);
                showDice(dice);

                if (vibrationEnabled) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(50);
                }
            }
        });

        dicerViewModel.getDiceNumberLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                contentMainBinding.chooseDiceNumber.setText(String.format(Locale.ENGLISH, "%d", number));
            }
        });

        dicerViewModel.getFaceNumberLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                contentMainBinding.chooseFaceNumber.setText(String.format(Locale.ENGLISH, "%d", number));
            }
        });

        dicerViewModel.getDifficultyNumberLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer number) {
                contentMainBinding.chooseDifficultyNumber.setText(String.format(Locale.ENGLISH, "%d", number));
            }
        });


        dicerViewModel.getSuccessLiveData().observe(this, new Observer<ArrayList<ArrayList<Integer>>>() {
            public void setPairView(ArrayList<Integer> pair, int offset) {
                for (int i = 0; i < pair.size(); i++) {
                    if (dicerViewModel.getFaceNumber() <= 6) {
                        pairViews[offset * 3 + i].setImageResource(getDicerDrawable(pair.get(i)));
                    } else {
                        pairViews[offset * 3 + i].setImageBitmap(createBitmapForNumber(pair.get(i)));
                    }
                }
            }

            @Override
            public void onChanged(ArrayList<ArrayList<Integer>> s) {
                for (int i = 0; i < s.size(); i++) {
                    setPairView(s.get(i), i);
                }
                if (contentMainBinding.sum15checkbox.isChecked()) {
                    int c = 0;
                    for (int i = 0; i < s.size(); i++) {
                        int subc = 0;
                        for (int j = 0; j < s.get(i).size(); j++) {
                            subc += s.get(i).get(j);
                        }
                        if (subc >= 15) {
                            c += 2;
                        } else {
                            c++;
                        }
                    }
                    contentMainBinding.successTitle.setText("Success: " + c);
                } else {
                    contentMainBinding.successTitle.setText("Success: " + s.size());
                }
            }
        });
    }

    private void initResources() {
        initResultDiceViews();
        initPairDiceViews();

        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        contentMainBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dicerViewModel.setDiceNumber(progress + 1);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        contentMainBinding.seekBarFace.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dicerViewModel.setFaceNumber(progress + 1);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        contentMainBinding.seekBarDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dicerViewModel.setDifficultyNumber(progress + 1);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Button
        contentMainBinding.rollButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rollDice();
            }
        });

        //Shaking
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeListener = new ShakeListener();
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {

            public void onShake(int count) {
                if (shakingEnabled) {
                    rollDice();
                }
            }
        });
    }

    public static void flashResult(ImageView imageView) {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        animation.setStartOffset(20);
        animation.setRepeatMode(Animation.REVERSE);
        imageView.startAnimation(animation);
    }

    public void initResultDiceViews() {
        imageViews = new ImageView[10];

        imageViews[0] = contentMainBinding.resultOne;
        imageViews[1] = contentMainBinding.resultTwo;
        imageViews[2] = contentMainBinding.resultThree;
        imageViews[3] = contentMainBinding.resultFour;
        imageViews[4] = contentMainBinding.resultFive;
        imageViews[5] = contentMainBinding.resultSix;
        imageViews[6] = contentMainBinding.resultSeven;
        imageViews[7] = contentMainBinding.resultEight;
        imageViews[8] = contentMainBinding.resultNine;
        imageViews[9] = contentMainBinding.resultTen;
    }

    public void initPairDiceViews() {
        pairViews = new ImageView[30];

        pairViews[0] = contentMainBinding.pairOne1;
        pairViews[1] = contentMainBinding.pairOne2;
        pairViews[2] = contentMainBinding.pairOne3;
        pairViews[3] = contentMainBinding.pairTwo1;
        pairViews[4] = contentMainBinding.pairTwo2;
        pairViews[5] = contentMainBinding.pairTwo3;
        pairViews[6] = contentMainBinding.pairThree1;
        pairViews[7] = contentMainBinding.pairThree2;
        pairViews[8] = contentMainBinding.pairThree3;
        pairViews[9] = contentMainBinding.pairFour1;
        pairViews[10] = contentMainBinding.pairFour2;
        pairViews[11] = contentMainBinding.pairFour3;
        pairViews[12] = contentMainBinding.pairFive1;
        pairViews[13] = contentMainBinding.pairFive2;
        pairViews[14] = contentMainBinding.pairFive3;
        pairViews[15] = contentMainBinding.pairSix1;
        pairViews[16] = contentMainBinding.pairSix2;
        pairViews[17] = contentMainBinding.pairSix3;
        pairViews[18] = contentMainBinding.pairSeven1;
        pairViews[19] = contentMainBinding.pairSeven2;
        pairViews[20] = contentMainBinding.pairSeven3;
        pairViews[21] = contentMainBinding.pairEight1;
        pairViews[22] = contentMainBinding.pairEight2;
        pairViews[23] = contentMainBinding.pairEight3;
        pairViews[24] = contentMainBinding.pairNine1;
        pairViews[25] = contentMainBinding.pairNine2;
        pairViews[26] = contentMainBinding.pairNine3;
        pairViews[27] = contentMainBinding.pairTen1;
        pairViews[28] = contentMainBinding.pairTen2;
        pairViews[29] = contentMainBinding.pairTen3;

        clearDiceViews();
    }

    private void clearDiceViews() {
        for(ImageView imageView : imageViews) {
            imageView.setImageResource(0);
        }
        for(ImageView imageView : pairViews) {
            imageView.setImageResource(0);
        }
    }

    public void rollDice() {
        applySettings();
        dicerViewModel.rollDice(contentMainBinding.sum15checkbox.isChecked());
        Toast.makeText(this, "Long press a dice to reroll...", Toast.LENGTH_SHORT).show();
    }

    private void hookDiceRerolling(final ImageView dice, final int n) {
        dice.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                dicerViewModel.rerollDice(n);
                flashResult(dice);
                dicerViewModel.calculateSuccess(dicerViewModel.getDicerLiveData().getValue(), contentMainBinding.sum15checkbox.isChecked());
                return false;
            }
        });
    }

    private void showDice(int[] dice) {
        clearDiceViews();
        for (int i = 0; i < dice.length; i++) {
            if(dicerViewModel.getFaceNumber() <= 6) {
                imageViews[i].setImageResource(getDicerDrawable(dice[i]));
            } else {
                imageViews[i].setImageBitmap(createBitmapForNumber(dice[i]));
            }
            hookDiceRerolling(imageViews[i], i);
        }
    }

    public @DrawableRes int getDicerDrawable(int number) {
        switch (number) {
            case 1:
                return R.drawable.d1;
            case 2:
                return R.drawable.d2;
            case 3:
                return R.drawable.d3;
            case 4:
                return R.drawable.d4;
            case 5:
                return R.drawable.d5;
            case 6:
                return R.drawable.d6;
            default:
                break;
        }
        return -1;
    }

    private Bitmap createBitmapForNumber(int number) {
        int height = 256;
        int width = 256;

        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Paint p = new Paint();
        float textSize = width * 2.5f / 4.0f;
        p.setColor(Color.WHITE);
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(textSize);
        p.setAntiAlias(true);

        canvas.drawText(String.format(Locale.ENGLISH, "%d", number), width / 2.0f, height / 2.0f + textSize / 3.0f, p);
        return result;
    }

    private void displaySum(int[] dice) {
        int sum = 0;
        for(int d : dice) {
            sum += d;
        }
        contentMainBinding.sumTextView.setText(getString(R.string.main_dice_sum, Integer.toString(sum)));
    }

    public void applySettings() {
        shakingEnabled = sharedPreferences.getBoolean("enable_shaking", true);
        vibrationEnabled = sharedPreferences.getBoolean("enable_vibration", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeListener, accelerometer,
                SensorManager.SENSOR_DELAY_UI);

        applySettings();
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Handle navigation view item clicks here.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;

        switch(item.getItemId()) {
            case R.id.nav_about:
                intent = new Intent(this, AboutActivity.class);
                break;
            case R.id.nav_help:
                intent = new Intent(this, HelpActivity.class);
                break;
            case R.id.nav_settimgs:
                intent = new Intent(this, SettingsActivity.class);
                break;
            case R.id.nav_tutorial:
                intent = new Intent(this, TutorialActivity.class);
                break;
            default:
                return false;
        }

        startActivity(intent);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
