package it.matteoleggio.seventhseadicer.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import it.matteoleggio.seventhseadicer.dicer.Dicer;

public class DicerViewModel extends ViewModel {

    private final Dicer dicer = new Dicer();
    private int faceNumber = 10;
    private int diceNumber = 5;
    private int difficultyNumber = 10;

    private final MutableLiveData<int[]> dicerLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<ArrayList<Integer>>> successLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> diceNumberLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> faceNumberLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> difficultyNumberLiveData = new MutableLiveData<>();

    public DicerViewModel() {
        dicerLiveData.postValue(new int[0]);
    }

    public LiveData<int[]> getDicerLiveData() {
        return dicerLiveData;
    }
    public LiveData<Integer> getDiceNumberLiveData() {
        return diceNumberLiveData;
    }
    public LiveData<Integer> getFaceNumberLiveData() {
        return faceNumberLiveData;
    }
    public LiveData<Integer> getDifficultyNumberLiveData() {
        return difficultyNumberLiveData;
    }
    public LiveData<ArrayList<ArrayList<Integer>>> getSuccessLiveData() {
        return successLiveData;
    }

    public int getDiceNumber() {
        return diceNumber;
    }
    public int getFaceNumber() {
        return faceNumber;
    }
    public int getDifficultyNumber() {
        return difficultyNumber;
    }

    public void setDiceNumber(int diceNumber) {
        this.diceNumber = diceNumber;
        diceNumberLiveData.postValue(diceNumber);
    }

    public void setFaceNumber(int faceNumber) {
        this.faceNumber = faceNumber;
        faceNumberLiveData.postValue(faceNumber);
    }

    public void setDifficultyNumber(int faceNumber) {
        this.difficultyNumber = faceNumber;
        difficultyNumberLiveData.postValue(faceNumber);
    }

    public void rerollDice(int diceN) {
        int[] roll = dicer.rollDice(1, faceNumber);
        int[] dices = getDicerLiveData().getValue();
        dices[diceN] = roll[0];
        dicerLiveData.postValue(dices);
    }

    public void rollDice(boolean sum15) {
        int[] roll = dicer.rollDice(diceNumber, faceNumber);
        dicerLiveData.postValue(roll);
        calculateSuccess(roll, sum15);
    }

    public void calculateSuccess(int[] roll, boolean sum15) {
        successLiveData.postValue(dicer.findSuccess(roll, difficultyNumber, sum15));
    }
}
