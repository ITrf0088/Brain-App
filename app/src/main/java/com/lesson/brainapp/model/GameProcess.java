package com.lesson.brainapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lesson.brainapp.model.Test.Test;
import com.lesson.brainapp.model.Test.TestException.TestException;
import com.lesson.brainapp.model.Test.TestFactory;

public class GameProcess implements Parcelable {


    private int level;
    private int record;

    private Test currentTest;
    private int availableTime;
    private int right;
    private int wrong;

    private boolean isRun;
    private String lvlRecordNameForPreference;


    public void generateTest() throws TestException {
        currentTest = TestFactory.generate(level);
    }

    public int getVariant(int index) throws TestException {
        Test.Variants variants = currentTest.getVariants();
        return variants.get(index);
    }

    public String getExpression() {
        return currentTest.getExpression();
    }

    public void resetTime() {
        availableTime = 10_000;
    }

    public boolean answerProcessing(int answer) {
        boolean isRight = true;
        Test.Variants variants = currentTest.getVariants();
        if (answer == variants.getIndexOfRightAnswer()) {
            right++;
            availableTime += 2000;
            checkRecord();
        } else {
            isRight = false;
            wrong++;
            availableTime -= 1000;
        }
        return isRight;
    }

    private void checkRecord() {
        if (right > record) {
            record++;
        }
    }

    public boolean isNewRecordGreater(int oldRecord) {
        return record > oldRecord;

    }

    public void finishTheTest() {
        currentTest = null;
        isRun = false;
        right = 0;
        wrong = 0;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setRecordLevel(int record) {
        this.record = record;
    }


    public int getLevel() {
        return level;
    }

    public int getRecord() {
        return record;
    }

    public int getRight() {
        return right;
    }

    public int getWrong() {
        return wrong;
    }

    public int getAvailableTime() {
        return availableTime;
    }

    public void decreaseTime(int i) {
        availableTime -= i;
    }

    public boolean isRun() {
        return isRun;
    }


    public void run() {
        isRun = true;
        resetTime();
    }

    public String getLvlRecordNameForPreference() {
        return lvlRecordNameForPreference;
    }

    public void setLvlRecordNameForPreference(String lvlRecordNameForPreference) {
        this.lvlRecordNameForPreference = lvlRecordNameForPreference;
    }


    public GameProcess() {
    }

    protected GameProcess(Parcel in) {
        level = in.readInt();
        record = in.readInt();
        availableTime = in.readInt();
        right = in.readInt();
        wrong = in.readInt();
        isRun = in.readByte() != 0;
        lvlRecordNameForPreference = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeInt(record);
        dest.writeInt(availableTime);
        dest.writeInt(right);
        dest.writeInt(wrong);
        dest.writeByte((byte) (isRun ? 1 : 0));
        dest.writeString(lvlRecordNameForPreference);
    }

    public static final Creator<GameProcess> CREATOR = new Creator<GameProcess>() {
        @Override
        public GameProcess createFromParcel(Parcel in) {
            return new GameProcess(in);
        }

        @Override
        public GameProcess[] newArray(int size) {
            return new GameProcess[size];
        }
    };
}
