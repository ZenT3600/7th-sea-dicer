package it.matteoleggio.seventhseadicer.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import it.matteoleggio.seventhseadicer.dicer.Dicer;

public class DicerViewModel extends ViewModel {

    private final Dicer dicer = new Dicer();
    private int faceNumber = 6;
    private int diceNumber = 5;
    private int difficultyNumber = 10;

    private final MutableLiveData<int[]> dicerLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> successLiveData = new MutableLiveData<>();
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
    public LiveData<String> getSuccessLiveData() {
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

    public void rollDice() {
        int[] roll = dicer.rollDice(diceNumber, faceNumber);
        dicerLiveData.postValue(roll);
        successLiveData.postValue(dicer.findSuccess(roll, difficultyNumber));
    }
}
