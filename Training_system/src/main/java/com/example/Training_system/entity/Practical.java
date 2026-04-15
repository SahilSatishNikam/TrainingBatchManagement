package com.example.Training_system.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Practical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String title;

    private String description;

    private String status; // ASSIGNED / SUBMITTED / COMPLETED

    private Integer marks;

    private LocalDate assignedDate;
    private LocalDate submissionDate;

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

  	public String getDescription() {
  		return description;
  	}

  	public void setDescription(String description) {
  		this.description = description;
  	}

  	public String getStatus() {
  		return status;
  	}

  	public void setStatus(String status) {
  		this.status = status;
  	}

  	public Integer getMarks() {
  		return marks;
  	}

  	public void setMarks(Integer marks) {
  		this.marks = marks;
  	}

  	public LocalDate getAssignedDate() {
  		return assignedDate;
  	}

  	public void setAssignedDate(LocalDate assignedDate) {
  		this.assignedDate = assignedDate;
  	}

  	public LocalDate getSubmissionDate() {
  		return submissionDate;
  	}

  	public void setSubmissionDate(LocalDate submissionDate) {
  		this.submissionDate = submissionDate;
  	}

  	public Batch getBatch() {
  		return batch;
  	}

  	public void setBatch(Batch batch) {
  		this.batch = batch;
  	}

}