package com.punbook.mayankgupta.havi.dummy;

/**
 * Created by mayankgupta on 13/03/17.
 */

public class User {

    private int points = 10;
    //private List<Task> tasks = new ArrayList<>();

    public User() {
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

   /* public List<Task> getTasks() {

        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task task, Task t1) {
                return task.getStartDate().compareTo(t1.getStartDate());
            }
        });

        return tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }*/

    @Override
    public String toString() {
        return "User{" +
                "points=" + points +
                '}';
    }
}
