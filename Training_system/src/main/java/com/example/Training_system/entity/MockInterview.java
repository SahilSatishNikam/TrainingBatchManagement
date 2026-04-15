package com.example.Training_system.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class MockInterview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // e.g. Java Mock 1

    private String interviewer;

    private String feedback;

    private Integer score; // out of 100

    private String status; // SCHEDULED / COMPLETED
    
    @PrePersist
    public void setDefaults() {
        if (this.status == null) {
            this.status = "SCHEDULED";
        }
    }

    private LocalDate interviewDate;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInterviewer() {
		return interviewer;
	}

	public void setInterviewer(String interviewer) {
		this.interviewer = interviewer;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getInterviewDate() {
		return interviewDate;
	}

	public void setInterviewDate(LocalDate interviewDate) {
		this.interviewDate = interviewDate;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

    // getters & setters
}