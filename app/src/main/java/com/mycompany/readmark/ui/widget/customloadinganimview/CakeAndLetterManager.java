package com.mycompany.readmark.ui.widget.customloadinganimview;

import android.graphics.Canvas;


import com.mycompany.readmark.ui.widget.customloadinganimview.cakes.Cake;
import com.mycompany.readmark.ui.widget.customloadinganimview.cakes.FirstCake;
import com.mycompany.readmark.ui.widget.customloadinganimview.cakes.ForthCake;
import com.mycompany.readmark.ui.widget.customloadinganimview.cakes.SecondCake;
import com.mycompany.readmark.ui.widget.customloadinganimview.cakes.ThirdCake;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.ALetter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.DLetter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.ELetter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.KLetter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.Letter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.MLetter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.RLetter;
import com.mycompany.readmark.ui.widget.customloadinganimview.letters.RLetterS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo.
 */

public class CakeAndLetterManager implements Cake.AnimatorStateListener {

    List<Cake> mCakes;
    List<Letter> mLetters;


    private LoadingAnimView mView;
    private int mEndNum = 0;
    private boolean isEnding = false;


    public CakeAndLetterManager(LoadingAnimView view, int x, int y) {
        mView = view;
        mCakes = new ArrayList<>();
        mLetters = new ArrayList<>();
        initComponents(x, y);
    }

    private void initComponents(int centerX, int centerY){
        addLetter(new RLetter(centerX - 480, centerY));
        addLetter(new ELetter(centerX - 340, centerY));
        addLetter(new ALetter(centerX - 200, centerY));
        addLetter(new DLetter(centerX - 60, centerY));
        addLetter(new MLetter(centerX + 40, centerY));
        addLetter(new ALetter(centerX + 300, centerY));
        addLetter(new RLetterS(centerX + 445, centerY));
        addLetter(new KLetter(centerX + 480, centerY));

        //startLetterAnim();

        addAndInitCake(new FirstCake(centerX - 210, centerY));
        addAndInitCake(new SecondCake(centerX - 70, centerY));
        addAndInitCake(new ThirdCake(centerX + 70, centerY));
        addAndInitCake(new ForthCake(centerX + 210, centerY));

        startCakesAnim();
    }

    private void addLetter(Letter letter){
        if(letter != null){
            mLetters.add(letter);

        }

    }

    private void addAndInitCake(Cake cake){
        if(cake != null){
            mCakes.add(cake);
            cake.prepareAnim();
            cake.setAnimatorStateListener(this);
        }
    }

    public void startCakesAnim(){
        isEnding = false;
        mEndNum = 0;
        for (Cake cake : mCakes){
            cake.startAnim();
        }
    }

    private void startLetterAnim(){
        for (Letter letter : mLetters){
            letter.startAnim();
        }
    }

    public void drawTheWorld(Canvas canvas){
        for(Cake cake : mCakes){
            cake.drawSelf(canvas);
        }
        for(Letter letter : mLetters){
            letter.drawSelf(canvas);
        }

    }

    private void prepareToShowText(){
        mEndNum = 0;
        for(Cake cake : mCakes){
            cake.endAnim();
        }
    }

    @Override
    public void onCakeChangeAnimatorEnd() {
        mEndNum ++;
        if(mEndNum == mCakes.size()){
            prepareToShowText();
        }
    }

    @Override
    public void onMoveEnd() {
        mEndNum ++;
        if(mEndNum == mCakes.size()){
            startLetterAnim();
        }
    }

    @Override
    public void onAllAnimatorEnd() {
        mView.onAllAnimEnd();
    }
}
