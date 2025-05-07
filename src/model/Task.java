package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task {


    private String name;
    private String description;
    private int id;
    private Status status;
    private TaskType type;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String name, String description, String startTime, long duration) {
        this.name = name;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = parseStartTime(startTime);
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int id, Status status, String startTime, long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = parseStartTime(startTime);
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(int id, String name, Status status, String description) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(int id,String name,String description,Status status){
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;



    }

    public Task(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }




    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            LocalDateTime endTime = startTime.plus(duration);
            return endTime;
        } else {
            return null;
        }
    }
    public String getFormattedDuration() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, type, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    private LocalDateTime parseStartTime(String startTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        try {
            return LocalDateTime.parse(startTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Некорректный формат времени" + startTimeString, e);
        }

    }

    public String getStartTimeInString() {
        return parseStartTimeBack(startTime);
    }

    private String parseStartTimeBack(LocalDateTime startTime) {
        if (startTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return startTime.format(formatter);
        } else {
            return null;
        }
    }


}