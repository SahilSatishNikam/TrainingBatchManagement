const BASE = "http://localhost:8080/admin";
let allBatches = [];

async function apiRequest(url, method = "GET", body = null) {
	const res = await fetch(BASE + url, {
		method,
		headers: { "Content-Type": "application/json" },
		body: body ? JSON.stringify(body) : null
	});
	return res.json();
}

// Load batches
async function loadBatches() {
	const data = await apiRequest("/batches");
	allBatches = data;
	renderBatchTable(data);
}

function renderBatchTable(data) {
	const table = document.getElementById("batchTable");
	if (data.length === 0) {
		table.innerHTML = `<tr><td colspan="7" class="text-center">No batches found</td></tr>`;
		return;
	}
	table.innerHTML = data.map((b, i) => `
    <tr>
        <td>${i + 1}</td>
        <td>${b.batchName}</td>
        <td>${b.trainer?.name || "-"}</td>
        <td>${b.startDate}</td>
        <td>${b.endDate}</td>
        <td><span class="badge ${b.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${b.status}</span></td>
        <td>
            <button class="btn btn-warning btn-sm" onclick="openEditBatch(${b.id})">Edit</button>
            <button class="btn btn-danger btn-sm" onclick="deleteBatch(${b.id})">Delete</button>
        </td>
    </tr>`).join("");
}

// Create batch
async function createBatch() {
	const batch = {
		batchName: document.getElementById("batchName").value,
		startDate: document.getElementById("startDate").value,
		endDate: document.getElementById("endDate").value,
		status: "ACTIVE"
	};
	await apiRequest("/batch", "POST", batch);
	alert("Created ✅");
	loadBatches();
}

// Delete batch
async function deleteBatch(id) {
	if (!confirm("Delete batch?")) return;
	await apiRequest(`/batch/${id}`, "DELETE");
	alert("Deleted ✅");
	loadBatches();
}

// Open edit modal
function openEditBatch(id) {
	const b = allBatches.find(b => b.id === id);
	document.getElementById("editBatchId").value = b.id;
	document.getElementById("editBatchName").value = b.batchName;
	document.getElementById("editStartDate").value = b.startDate;
	document.getElementById("editEndDate").value = b.endDate;
	document.getElementById("editStatus").value = b.status;
	new bootstrap.Modal(document.getElementById("editBatchModal")).show();
}

// Update batch
async function updateBatch() {
	const id = document.getElementById("editBatchId").value;
	const updatedData = {
		batchName: document.getElementById("editBatchName").value,
		startDate: document.getElementById("editStartDate").value,
		endDate: document.getElementById("editEndDate").value,
		status: document.getElementById("editStatus").value
	};
	await apiRequest(`/batch/${id}`, "PUT", updatedData);
	alert("Updated ✅");
	bootstrap.Modal.getInstance(document.getElementById("editBatchModal")).hide();
	loadBatches();
}

// Initialize
window.onload = () => {  
	    loadBatches(); // ✅ ADD THIS
	};