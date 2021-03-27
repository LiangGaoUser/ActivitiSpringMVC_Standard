package com.lianggao.bean;

import java.io.Serializable;

public class DangerApplication implements Serializable {
    private int TaskID;//在Activiti中作为BusinessKey
    private String DangerTaskNum;
    private String DangerTaskName;
    private String DangerTaskLevel;
    private String Category;
    private String Applicant;
    private String TaskInstitution;
    private String ApplyingTime;
    private String StartTime;
    private String EndTime;
    private String UploadFileName;
    private String State;
    private String Archived;
    private String ApproverList;


    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public String getDangerTaskNum() {
        return DangerTaskNum;
    }

    public void setDangerTaskNum(String dangerTaskNum) {
        DangerTaskNum = dangerTaskNum;
    }

    public String getDangerTaskName() {
        return DangerTaskName;
    }

    public void setDangerTaskName(String dangerTaskName) {
        DangerTaskName = dangerTaskName;
    }

    public String getDangerTaskLevel() {
        return DangerTaskLevel;
    }

    public void setDangerTaskLevel(String dangerTaskLevel) {
        DangerTaskLevel = dangerTaskLevel;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getApplicant() {
        return Applicant;
    }

    public void setApplicant(String applicant) {
        Applicant = applicant;
    }

    public String getTaskInstitution() {
        return TaskInstitution;
    }

    public void setTaskInstitution(String taskInstitution) {
        TaskInstitution = taskInstitution;
    }

    public String getApplyingTime() {
        return ApplyingTime;
    }

    public void setApplyingTime(String applyingTime) {
        ApplyingTime = applyingTime;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getUploadFileName() {
        return UploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        UploadFileName = uploadFileName;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getArchived() {
        return Archived;
    }

    public void setArchived(String archived) {
        Archived = archived;
    }

    public String getApproverList() {
        return ApproverList;
    }

    public void setApproverList(String approverList) {
        ApproverList = approverList;
    }


    @Override
    public String toString() {
        return "Dangerapplication{" +
                "TaskID=" + TaskID +
                ", DangerTaskNum='" + DangerTaskNum + '\'' +
                ", DangerTaskName='" + DangerTaskName + '\'' +
                ", DangerTaskLevel='" + DangerTaskLevel + '\'' +
                ", Category='" + Category + '\'' +
                ", Applicant='" + Applicant + '\'' +
                ", TaskInstitution='" + TaskInstitution + '\'' +
                ", ApplyingTime='" + ApplyingTime + '\'' +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", UploadFileName='" + UploadFileName + '\'' +
                ", State='" + State + '\'' +
                ", Archived='" + Archived + '\'' +
                ", ApproverList='" + ApproverList + '\'' +
                '}';
    }
}
