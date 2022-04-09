package com.lesson.brainapp.model.Test;

import com.lesson.brainapp.model.Test.TestException.TestException;

public class TestFactory {

    public static Test generate(int level) throws TestException {
        Test test = new Test(level);
        return test;
    }
}
