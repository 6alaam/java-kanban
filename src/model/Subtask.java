package model;

public class Subtask extends Task {

    private final int epicID;


    public Subtask(String name, String description, int epicId, String startTime, int duration) {
        super(name, description, startTime, duration);
        this.epicID = epicId;
    }

    public Subtask(String name, String details, int epicId) {
        super(name, details);
        this.epicID = epicId;
    }

    public Subtask(String name, String description, int epicId, int id, Status status, String startTime, long duration) {
        super(name, description, id, status, startTime, duration);
        this.epicID = epicId;

    }

    public Subtask(int id, String name, Status status, String details, int epicId) {
        super(id, name, status, details);
        this.epicID = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }


    public int getEpicID() {
        return epicID;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                "} " + super.toString();
    }
}
