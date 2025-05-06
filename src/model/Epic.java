package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }
}

