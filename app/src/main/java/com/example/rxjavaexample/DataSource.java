package com.example.rxjavaexample;

import java.util.ArrayList;
import java.util.List;

class DataSource {

    static List<Task> createTasks() {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(new Task("make bed", true, 4));
        tasks.add(new Task("make dinner", true,3));
        tasks.add(new Task("work", false,1));
        tasks.add(new Task("gym class", false,3));
        return tasks;

    }

}
