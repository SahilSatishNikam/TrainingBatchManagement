package com.example.Training_system.dto;

import java.util.List;

public class DashboardDTO {

    private long totalTrainers;
    private long totalBatches;
    private long activeBatches;
    private long completedBatches;
    private List<BatchDTO> batchList;
	public long getTotalTrainers() {
		return totalTrainers;
	}
	public void setTotalTrainers(long totalTrainers) {
		this.totalTrainers = totalTrainers;
	}
	public long getTotalBatches() {
		return totalBatches;
	}
	public void setTotalBatches(long totalBatches) {
		this.totalBatches = totalBatches;
	}
	public long getActiveBatches() {
		return activeBatches;
	}
	public void setActiveBatches(long activeBatches) {
		this.activeBatches = activeBatches;
	}
	public long getCompletedBatches() {
		return completedBatches;
	}
	public void setCompletedBatches(long completedBatches) {
		this.completedBatches = completedBatches;
	}
	public List<BatchDTO> getBatchList() {
		return batchList;
	}
	public void setBatchList(List<BatchDTO> batchList) {
		this.batchList = batchList;
	}

    // getters/setters
}