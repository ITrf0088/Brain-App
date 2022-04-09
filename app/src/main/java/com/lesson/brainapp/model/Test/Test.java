package com.lesson.brainapp.model.Test;

import com.lesson.brainapp.model.Test.TestException.TestException;

public class Test {

    public static final int LOW = 1;
    public static final int MIDDLE = 2;
    public static final int HIGH = 3;

    private int level;
    private int first;
    private int second;
    private int operand;
    private int result;
    private String expression;
    private Variants variants;

    public Test(int level) throws TestException {
        this.level = level;
        createTest();
    }

    private void createTest() throws TestException {
        if (level == Test.LOW) {
            setUpLowLevel();
        } else if (level == Test.MIDDLE) {
            setUpMiddleLevel();
        } else if (level == Test.HIGH) {
            setUpHighLevel();
        } else {
            throw new TestException("Nonexistent level error");
        }

    }


    private void setUpLowLevel() {
        this.first = randomNumber(15);
        this.second = randomNumber(first);
        setUpLevel();
    }


    private void setUpMiddleLevel() {
        this.first = randomNumber(35);
        this.second = randomNumber(first);
        setUpLevel();
    }

    private void setUpHighLevel() {
        this.first = randomNumber(50);
        this.second = randomNumber(50);
        setUpLevel();

    }

    private void setUpLevel() {
        this.operand = generateOperand();
        this.result = calcResult();
        this.variants = generateVariants();
        this.expression = setExpression();
    }

    private int randomNumber(int bound) {
        return (int) (Math.random() * bound);
    }

    private int generateOperand() {
        int i = randomNumber(10) - 5;
        return (i < 0) ? -1 : 1;
    }


    private int calcResult() {
        return first + (operand * second);
    }

    //This method generate Variants with the following principle
    //if result 8, final variants - 7,8,9,10
    //if result 29, final variants - 27,29,31,33
    //if result 54, final variants - 49,54,59,64
    //the sequence will be lost
    private Variants generateVariants() {
        int size = 4;
        int indexOfRightAnswer = randomNumber(size);
        int n = (result > -10 && result < 10) ? 1 : result / 10;
        int[] variants = new int[size];
        int counter = result - n;
        for (int i = 0; i < size; i++) {
            if (i != indexOfRightAnswer) {
                if (counter != result) {
                    variants[i] = counter;
                } else {
                    i--;
                }
                counter += n;
            }
        }
        variants[indexOfRightAnswer] = result;
        return new Variants(variants, indexOfRightAnswer);
    }

    private String setExpression() {
        String oprnd = (operand == 1) ? "+" : "-";
        return first + " " + oprnd + " " + second;
    }

    public Variants getVariants() {
        return variants;
    }

    public String getExpression() {
        return expression;
    }

    public static class Variants {
        private int[] variants;
        private int indexOfRightAnswer;
        private int rightAnswer;
        private int size;

        private Variants(int[] variants, int indexOfRightAnswer) {
            this.variants = variants;
            this.indexOfRightAnswer = indexOfRightAnswer;
            this.rightAnswer = variants[indexOfRightAnswer];
            this.size = variants.length;
        }

        public int get(int index) throws TestException {
            if (index >= 0 && index < 4) {
                return variants[index];
            } else {
                throw new TestException("Index needs to be exclusive within 4");
            }
        }

        public int getIndexOfRightAnswer() {
            return indexOfRightAnswer;
        }

        public int getRightAnswer() {
            return rightAnswer;
        }

        public int getSize() {
            return size;
        }
    }
}
