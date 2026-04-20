/**
 * =========================================
 * SMART ADMIN - FINAL CLEAN SCRIPT
 * =========================================
 */

const BASE_URL = "http://localhost:8080";

let trainers = [];
let currentPage = 0;
const PAGE_SIZE = 5;

let batches = [];
const BATCH_PAGE_SIZE = 5;
let batchPage = 0;

let selectedBatch = null;

/* ================= COMMON API ================= */
async function apiRequest(endpoint, method = "GET", body = null, isFormData = false) {
	try {
		const token = localStorage.getItem("token");

		const headers = {};

		if (token) {
			headers["Authorization"] = `Bearer ${token}`;
		}

		const options = { method, headers };

		if (body) {
			if (isFormData) {
				options.body = body;
			} else {
				headers["Content-Type"] = "application/json";
				options.body = JSON.stringify(body);
			}
		}

		const res = await fetch(BASE_URL + endpoint, options);
		const text = await res.text();

		// ✅ HANDLE AUTH WITHOUT LOGOUT
		if (!res.ok) {

			if (res.status === 401 || res.status === 403) {
				console.warn("Unauthorized API:", endpoint);
				return null; // ❗ DO NOT LOGOUT
			}

			throw new Error(text || "Server error");
		}

		return text ? JSON.parse(text) : null;

	} catch (err) {
		console.error("API ERROR:", err.message);
		alert(err.message);
		return null;
	}
}
/* ================= LOGIN ================= */
async function login() {
	const email = document.getElementById("email").value;
	const password = document.getElementById("password").value;

	try {
		const data = await apiRequest("/auth/login", "POST", { email, password });
		if (!data) {
			alert("Login failed");
			return;
		}


		const rawRole = data.role || "";
		const role = rawRole.replace("ROLE_", "").toUpperCase();

		localStorage.setItem("token", data.token);
		localStorage.setItem("role", role);

		if (role === "ADMIN") {
			window.location.href = "admin_dashboard.html";
		} else if (role === "TRAINER") {
			window.location.href = "trainer_dashboard.html";
		} else {
			alert("Unknown role");
		}

	} catch {
		alert("Invalid credentials");
	}
}


async function loadDashboard() {
	try {
		if (!document.getElementById("trainers")) return;

		const data = await apiRequest("/admin/dashboard");
		if (!data) return;

		document.getElementById("trainers").innerText = data.totalTrainers ?? 0;
		document.getElementById("batches").innerText = data.totalBatches ?? 0;
		document.getElementById("active").innerText = data.activeBatches ?? 0;
		document.getElementById("completed").innerText = data.completedBatches ?? 0;

		const table = document.getElementById("dashboardBatchTable");
		if (!table) return;

		table.innerHTML = "";

		const batches = data.batchList || [];

		if (!batches.length) {
			table.innerHTML = `<tr><td colspan="6">No ongoing batches</td></tr>`;
			return;
		}

		batches.forEach(batch => {

			// ✅ FIXED PROGRESS LOGIC
			let progress = batch.progress ??
				batch.progressPercentage ??
				calculateProgress(batch.startDate, batch.endDate);

			let color =
				progress < 30 ? "bg-danger" :
					progress < 70 ? "bg-warning" :
						"bg-success";

			table.innerHTML += `
                <tr>
                    <td>${batch.id}</td>
                    <td>${batch.batchName}</td>
                    <td>${batch.trainerName}</td>
                    <td>${batch.startDate} → ${batch.endDate}</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar ${color}" style="width:${progress}%"></div>
                        </div>
                        <small>${progress}%</small>
                    </td>
                </tr>
            `;
		});

	} catch (err) {
		console.error("Dashboard error:", err);
	}
}

/* ================= TRAINERS ================= */
async function loadTrainers() {
	trainers = await apiRequest("/admin/trainers");
	applyFilters();
}
/* ================= FILTER ================= */
function applyFilters() {
	const search = document.getElementById("searchInput")?.value.toLowerCase() || "";

	const filtered = trainers.filter(t =>
		(t.name || "").toLowerCase().includes(search) ||
		(t.lastName || "").toLowerCase().includes(search) ||
		(t.email || "").toLowerCase().includes(search) ||
		(t.department || "").toLowerCase().includes(search)
	);

	currentPage = 0;
	renderTrainerTable(filtered);
	renderTrainerPagination(filtered.length);
}


/* ================= TRAINER TABLE ================= */
function renderTrainerTable(data) {
	const table = document.getElementById("trainerTable");
	if (!table) return;

	const start = currentPage * PAGE_SIZE;
	const rows = data.slice(start, start + PAGE_SIZE);

	if (!rows.length) {
		table.innerHTML = `<tr><td colspan="7" class="text-center">No trainers found</td></tr>`;
		return;
	}

	table.innerHTML = rows.map((t, i) => renderRow(t, start + i)).join("");
}

/* ================= CREATE TRAINER ================= */
async function saveTrainer() {
	try {
		const name = document.getElementById("name").value.trim();
		const email = document.getElementById("email").value.trim();
		if (!name || !email) {
			alert("Name & Email required");
			return;
		}

		const formData = new FormData();
		[
			"name", "lastName", "email", "password", "gender", "mobile",
			"department", "designation", "dob", "joiningDate",
			"education", "address", "salary", "subject",
			"experience", "status", "bio"
		].forEach(id => {
			const el = document.getElementById(id);
			if (el) formData.append(id, el.value);
		});

		const photo = document.getElementById("photo")?.files[0];
		if (photo) formData.append("photo", photo);

		await apiRequest("/admin/create-trainer", "POST", formData, true);
		alert("Trainer created ✅");
		loadTrainers();

	} catch {
		alert("Failed to save trainer");
	}
}

/* ================= UPDATE TRAINER ================= */
async function updateTrainer() {
	try {
		const id = document.getElementById("editId").value;
		const formData = new FormData();

		formData.append("name", document.getElementById("editName").value);
		formData.append("lastName", document.getElementById("editLastName").value);
		formData.append("email", document.getElementById("editEmail").value);
		formData.append("mobile", document.getElementById("editMobile").value);
		formData.append("gender", document.getElementById("editGender").value);

		// ✅ FIXED FIELD NAMES
		formData.append("department", document.getElementById("editDept").value);
		formData.append("joiningDate", document.getElementById("editDate").value);

		formData.append("education", document.getElementById("editEducation").value);
		formData.append("salary", document.getElementById("editSalary").value);
		formData.append("subject", document.getElementById("editSubject").value);
		formData.append("experience", document.getElementById("editExperience").value);
		formData.append("status", document.getElementById("editStatus").value);
		formData.append("bio", document.getElementById("editBio").value);

		const file = document.getElementById("editPhoto")?.files[0];
		if (file) formData.append("photo", file);

		await apiRequest(`/admin/${id}`, "PUT", formData, true);

		alert("Trainer updated ✅");
		bootstrap.Modal.getInstance(document.getElementById("editTrainerModal")).hide();

		loadTrainers();

	} catch {
		alert("Update failed");
	}
}

/* ================= DELETE TRAINER ================= */
async function deleteTrainer(id) {
	if (!confirm("Delete trainer?")) return;
	await apiRequest(`/admin/${id}`, "DELETE");
	loadTrainers();
}

/* ================= TRAINER PAGINATION ================= */
function renderTrainerPagination(total) {
	const container = document.getElementById("pagination");
	if (!container) return;

	const pages = Math.ceil(total / PAGE_SIZE);
	container.innerHTML = "";
	for (let i = 0; i < pages; i++) {
		container.innerHTML += `
            <button class="btn btn-sm ${i === currentPage ? 'btn-primary' : 'btn-light'} me-1"
                onclick="changeTrainerPage(${i})">${i + 1}</button>
        `;
	}
}
function changeTrainerPage(page) {
	currentPage = page;
	applyFilters();
}

function openEditTrainerModal(id) {
	const trainer = trainers.find(t => t.id === id);
	if (!trainer) return;

	const setVal = (id, value) => {
		const el = document.getElementById(id);
		if (el) el.value = value || "";
	};

	setVal("editId", trainer.id);
	setVal("editName", trainer.name);
	setVal("editLastName", trainer.lastName);
	setVal("editEmail", trainer.email);
	setVal("editMobile", trainer.mobile);
	setVal("editDept", trainer.department);
	setVal("editGender", trainer.gender);
	setVal("editSalary", trainer.salary);
	setVal("editSubject", trainer.subject);
	setVal("editExperience", trainer.experience);
	setVal("editStatus", trainer.status);
	setVal("editEducation", trainer.education);
	setVal("editDate", trainer.joiningDate);
	setVal("editBio", trainer.bio);

	const modalEl = document.getElementById("editTrainerModal");
	const modal = new bootstrap.Modal(modalEl);
	modal.show();

	// ✅ Fix focus issue
	modalEl.removeAttribute("aria-hidden");
}
/* ================= SEARCH ================= */
document.addEventListener("input", e => {
	if (e.target.id === "searchInput") applyFilters();
});

/* ================= BATCHES ================= */
async function loadBatches() {
	try {
		const data = await apiRequest("/admin/batches");
		if (!data) return;

		batches = Array.isArray(data) ? data : [];

		const table = document.getElementById("batchTable");
		if (!table) return;

		if (!batches.length) {
			table.innerHTML = `<tr><td colspan="7">No batches found</td></tr>`;
			return;
		}

		batchPage = 0;
		renderBatchTable();
		renderBatchPagination();

	} catch (err) {
		console.error("Load batches error:", err);
	}
}
/* ================= BATCH TABLE ================= */
function renderBatchTable() {
	const table = document.getElementById("batchTable");
	if (!table) return;

	const start = batchPage * BATCH_PAGE_SIZE;
	const rows = batches.slice(start, start + BATCH_PAGE_SIZE);

	if (!rows.length) {
		table.innerHTML = `<tr><td colspan="7" class="text-center">No batches found</td></tr>`;
		return;
	}

	table.innerHTML = rows.map((b, i) => `
        <tr>
            <td>${start + i + 1}</td>
            <td>${b.batchName || "-"}</td>
			<td>${b.trainerName || (b.trainer?.name + " " + (b.trainer?.lastName || "")) || "-"}</td>
            <td>${b.startDate || "-"}</td>
            <td>${b.endDate || "-"}</td>

            <td>
                <span class="badge ${b.status === 'ONGOING' ? 'bg-success' :
			b.status === 'COMPLETED' ? 'bg-primary' :
				'bg-warning'
		}">
                    ${b.status}
                </span>
            </td>

            <td>
                <!-- ✅ FIXED: Removed Update Progress -->
                <button class="btn btn-warning btn-sm" onclick="openEditBatchModal(${b.id})">
                    Edit
                </button>

                <button class="btn btn-danger btn-sm" onclick="deleteBatch(${b.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join("");
}

/* ================= BATCH PAGINATION ================= */
function renderBatchPagination() {
	const container = document.getElementById("batchPagination");
	if (!container) return;

	const pages = Math.ceil(batches.length / BATCH_PAGE_SIZE);
	container.innerHTML = "";
	for (let i = 0; i < pages; i++) {
		container.innerHTML += `
            <button class="btn btn-sm ${i === batchPage ? 'btn-primary' : 'btn-light'} me-1"
                onclick="changeBatchPage(${i})">${i + 1}</button>
        `;
	}
}
function changeBatchPage(page) {
	batchPage = page;
	renderBatchTable();
}

/* ================= LOAD TRAINERS FOR DROPDOWN ================= */
async function loadTrainerDropdown(selectedTrainerId = null, isEdit = false) {
	const trainersList = await apiRequest("/admin/trainers");
	if (!trainersList) return;

	const createSelect = document.getElementById("trainerDropdown");
	const editSelectEl = document.getElementById("editTrainerDropdown");

	if (createSelect) {
		createSelect.innerHTML = '<option value="">Select Trainer</option>';
		trainersList.forEach(t => {
			createSelect.innerHTML += `<option value="${t.id}">${t.name} ${t.lastName || ""}</option>`;
		});
	}

	if (editSelectEl) {
		editSelectEl.innerHTML = '<option value="">Select Trainer</option>';
		trainersList.forEach(t => {
			const selected = selectedTrainerId && selectedTrainerId == t.id ? "selected" : "";
			editSelectEl.innerHTML += `<option value="${t.id}" ${selected}>${t.name} ${t.lastName || ""}</option>`;
		});
	}
}

/* ================= CREATE BATCH ================= */
async function createBatch() {
	try {
		const totalDays = Number(document.getElementById("totalDays").value);

		if (!totalDays || totalDays <= 0) {
			alert("Enter valid total days");
			return;
		}

		const trainerId = document.getElementById("trainerDropdown").value;

		if (!trainerId) {
			alert("Select trainer");
			return;
		}

		const data = {
			batchName: document.getElementById("batchName").value,
			startDate: document.getElementById("startDate").value,
			endDate: document.getElementById("endDate").value,
			totalDays: totalDays, // ✅ FIXED
			trainer: {
				id: Number(trainerId)
			}
		};

		await apiRequest("/admin/batch", "POST", data);

		alert("Batch Created ✅");
		loadBatches();

	} catch {
		alert("Failed to create batch");
	}
}
/* ================= DELETE BATCH ================= */
async function deleteBatch(id) {
	if (!confirm("Delete this batch?")) return;
	await apiRequest(`/admin/batch/${id}`, "DELETE");
	loadBatches();
}

/* ================= OPEN EDIT BATCH MODAL ================= */
async function openEditBatchModal(id) {
	const batch = batches.find(b => b.id === id);
	if (!batch) return;

	document.getElementById("editBatchId").value = batch.id;
	document.getElementById("editBatchName").value = batch.batchName;
	document.getElementById("editStartDate").value = batch.startDate;
	document.getElementById("editEndDate").value = batch.endDate;
	document.getElementById("editStatus").value = batch.status || "UPCOMING";

	// Load trainers and preselect
	await loadTrainerDropdown(batch.trainerId, true);

	const modal = new bootstrap.Modal(document.getElementById("editBatchModal"));
	modal.show();
}

/* ================= UPDATE BATCH ================= */
async function updateBatch() {
	try {
		const id = document.getElementById("editBatchId").value;

		const data = {
			batchName: document.getElementById("editBatchName").value,
			startDate: document.getElementById("editStartDate").value,
			endDate: document.getElementById("editEndDate").value,
			status: document.getElementById("editStatus").value.toUpperCase(),
			trainer: {
				id: Number(document.getElementById("editTrainerDropdown").value)
			}
		};

		if (!document.getElementById("editTrainerDropdown").value) {
			alert("Select trainer");
			return;
		}

		await apiRequest(`/admin/batch/${id}`, "PUT", data);

		alert("Batch updated ✅");

		bootstrap.Modal.getInstance(document.getElementById("editBatchModal")).hide();

		loadBatches();

	} catch {
		alert("Update failed");
	}
}

async function loadHistory() {

	const yearEl = document.getElementById("year");
	const monthEl = document.getElementById("month");
	const table = document.getElementById("tableBody");

	// ✅ SAFE CHECK (prevents null crash)
	if (!table) {
		console.warn("tableBody not found in HTML");
		return;
	}

	const year = yearEl ? yearEl.value : new Date().getFullYear();
	const month = monthEl ? monthEl.value : "";

	let url = "";

	if (!month) {
		url = `${BASE_URL}/admin/batches/history/year/${year}`;
	} else {
		url = `${BASE_URL}/admin/batches/history/month?month=${month}&year=${year}`;
	}

	const token = localStorage.getItem("token");

	try {
		const res = await fetch(url, {
			headers: token
				? { "Authorization": "Bearer " + token }
				: {}
		});

		// ✅ AUTH ERROR HANDLING
		if (res.status === 401 || res.status === 403) {
			alert("Session expired or access denied. Please login again.");
			window.location.href = "login.html";
			return;
		}

		if (!res.ok) {
			throw new Error("HTTP Error: " + res.status);
		}

		const data = await res.json();

		table.innerHTML = "";

		if (!data || data.length === 0) {
			table.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center">No data found</td>
                </tr>
            `;
			return;
		}

		data.forEach(batch => {
			table.innerHTML += `
                <tr>
                    <td>${batch.batchName || "-"}</td>
                    <td>${batch.trainerName || "-"}</td>
                    <td>${batch.startDate || "-"}</td>
                    <td>${batch.endDate || "-"}</td>
                    <td>${batch.progress ?? 0}%</td>
                </tr>
            `;
		});

	} catch (err) {
		console.error("History Load Error:", err);
		alert("❌ Failed to load history");
	}
}

async function loadTrainerBatches() {
	const table = document.getElementById("trainerBatchTable");
	if (!table) return;

	try {
		const data = await apiRequest("/trainer/my-batches");
		if (!data) return;

		if (!data.length) {
			table.innerHTML = `<tr><td colspan="4">No batches assigned</td></tr>`;
			return;
		}

		const activeBatches = data.filter(b => b.status !== "COMPLETED");

		if (!activeBatches.length) {
			table.innerHTML = `<tr><td colspan="4">No active batches</td></tr>`;
			return;
		}

		table.innerHTML = activeBatches.map(b => {

			// ✅ FIXED PROGRESS
			const progress = b.progress ??
				calculateProgress(b.startDate, b.endDate);
			return `
                <tr>
                    <td>${b.batchName}</td>
                    <td>${b.startDate}</td>
                    <td>${b.endDate}</td>
                    <td>
                        <div class="progress">
                            <div class="progress-bar 
                                ${progress < 30 ? 'bg-danger' :
					progress < 70 ? 'bg-warning' : 'bg-success'}" 
                                style="width:${progress}%">
                            </div>
                        </div>
                        <small>${progress}%</small>
                    </td>
                </tr>
            `;
		}).join("");

	} catch (err) {
		console.error(err);
	}
}
function logout() {
	localStorage.removeItem("token");
	localStorage.removeItem("role");

	alert("Logged out successfully");
	window.location.href = "login.html";
}
async function loadProgressPage() {

	const dropdown = document.getElementById("batchSelect");
	if (!dropdown) return;

	try {
		const data = await apiRequest("/trainer/my-batches");
		if (!data) return;

		if (!data.length) {
			dropdown.innerHTML = `<option>No batches</option>`;
			return;
		}

		dropdown.innerHTML = `<option value="">Select Batch</option>`;

		data.forEach(b => {

			// ✅ FIX: correct progress calculation
			const progress =
				b.progress ??
				(b.totalDays ? Math.round((b.completedDays / b.totalDays) * 100) : 0);

			dropdown.innerHTML += `
                <option value="${b.id}">
                    ${b.batchName} (${progress}%)
                </option>
            `;
		});

		dropdown.onchange = handleBatchChange;

	} catch (err) {
		console.error(err);
	}
}

async function loadTrainerDashboardSummary() {

	const totalEl = document.getElementById("totalBatches");
	const activeEl = document.getElementById("activeBatches");
	const completedEl = document.getElementById("completedBatches");
	const pendingEl = document.getElementById("pending");

	// ❌ If not dashboard page → stop
	if (!totalEl) return;

	try {
		const data = await apiRequest("/trainer/my-batches");

		let total = data.length;
		let active = 0;
		let completed = 0;
		let pending = 0;

		data.forEach(b => {
			if (b.status === "ONGOING") active++;
			else if (b.status === "COMPLETED") completed++;
			else pending++;
		});

		totalEl.innerText = total;
		activeEl.innerText = active;
		completedEl.innerText = completed;
		pendingEl.innerText = pending;

	} catch (err) {
		console.error("Trainer summary error:", err);
	}
}

async function handleBatchChange() {
	const id = this.value;
	if (!id) return;

	// ✅ SET ID HERE (CRITICAL FIX)
	document.getElementById("batchId").value = id;

	const batch = await apiRequest(`/trainer/batch/${id}`);
	if (!batch) return;

	selectedBatch = batch;

	const total = batch.totalDays ?? batch.total_days ?? 0;
	const completed = batch.completedDays ?? batch.completed_days ?? 0;

	document.getElementById("totalDays").value = total;
	document.getElementById("completedDays").value = completed;
	
	updateProgressUI();
}
async function submitProgress() {

	const batchIdEl = document.getElementById("batchId");

	if (!batchIdEl || !batchIdEl.value) {
		alert("❌ Please select batch");
		return;
	}

	const id = batchIdEl.value;
	const completed = Number(document.getElementById("completedDays").value);
	const total = Number(document.getElementById("totalDays").value);

	if (completed > total) {
		alert("Completed cannot exceed total");
		return;
	}

	const res = await apiRequest(`/trainer/batch/${id}/progress`, "PUT", {
		days: completed
	});

	if (!res) {
		alert("❌ Update failed");
		return;
	}

	alert("✅ Progress Updated");

	loadProgressPage();
	loadTrainerDashboardSummary();
}
function renderRow(t, index) {
	return `
    <tr>
      <td>${index + 1}</td>

      <td>
        <div class="trainer-box">
          <img src="${t.photo || 'https://via.placeholder.com/40'}">
          <div>
            <div class="trainer-name">${t.name}</div>
            <div class="trainer-email">${t.email}</div>
          </div>
        </div>
      </td>

      <td>${t.department || '-'}</td>

      <td>${t.mobile || '-'}</td>

      <td>${t.joiningDate || '-'}</td>

      <td>
        <span class="status ${t.status === 'Active' ? 'active' : 'inactive'}">
          ${t.status || 'Active'}
        </span>
      </td>

      <td class="action-btn">
        <button class="edit-btn" onclick="openEditTrainerModal(${t.id})">
          <i class="fas fa-edit"></i>
        </button>

        <button class="delete-btn" onclick="deleteTrainer(${t.id})">
          <i class="fas fa-trash"></i>
        </button>
      </td>
    </tr>
  `;
}

function updateProgressUI() {
	const total = Number(document.getElementById("totalDays")?.value || 0);
	const completed = Number(document.getElementById("completedDays")?.value || 0);

	let percent = 0;

	if (total > 0) {
		percent = Math.min((completed / total) * 100, 100);
	}

	// Progress bar
	const bar = document.getElementById("progressBar");
	if (bar) bar.style.width = percent + "%";

	// Text
	const text = document.getElementById("progressText");
	if (text) text.innerText = percent.toFixed(1) + "%";

	// ✅ FIXED STATS (THIS WAS YOUR ISSUE)
	const totalCard = document.getElementById("totalCard");
	const completedCard = document.getElementById("completedCard");
	const remainingCard = document.getElementById("remainingCard");

	if (totalCard) totalCard.innerText = total;
	if (completedCard) completedCard.innerText = completed;
	if (remainingCard) remainingCard.innerText = total - completed;
}

function createTrainerBatch() {

	let startDate = document.getElementById("startDate").value;
	let totalDays = Number(document.getElementById("totalDays").value);

	if (!startDate || !totalDays || totalDays <= 0) {
		alert("Enter valid start date and total days");
		return;
	}

	let start = new Date(startDate);
	let end = new Date(start);
	end.setDate(end.getDate() + totalDays);

	let endDate = end.toISOString().split("T")[0];

	const data = {
		batchName: document.getElementById("batchName").value,
		startDate: startDate,
		endDate: endDate,
		totalDays: totalDays // ✅ FIXED
	};

	fetch(BASE_URL + "/trainer/batch", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
			Authorization: "Bearer " + localStorage.getItem("token")
		},
		body: JSON.stringify(data)
	})
		.then(() => {
			alert("Batch Created Successfully ✅");
			window.location.href = "trainer_dashboard.html";
		});
}

async function loadMyBatchesPage() {

	const table = document.getElementById("batchTable"); // ✅ FIXED
	if (!table) return;

	const data = await apiRequest("/trainer/my-batches");

	console.log("DATA:", data); // DEBUG

	if (!data || data.length === 0) {
		table.innerHTML = `<tr><td colspan="7">No batches found</td></tr>`;
		return;
	}

	table.innerHTML = data.map(b => {

		const progress = b.totalDays > 0
			? Math.round((b.completedDays / b.totalDays) * 100)
			: 0;

		return `
            <tr>
                <td>${b.batchName}</td>
                <td>${b.startDate} → ${b.endDate}</td>
                <td>${b.totalDays}</td>
                <td>${b.completedDays}</td>

                <td>
                    <div class="progress">
                        <div class="progress-bar 
                            ${progress < 30 ? 'bg-danger' :
				progress < 70 ? 'bg-warning' : 'bg-success'}"
                            style="width:${progress}%">
                        </div>
                    </div>
                    <small>${progress}%</small>
                </td>

                <td>
                    <span class="badge 
                        ${b.status === 'ONGOING' ? 'bg-success' :
				b.status === 'COMPLETED' ? 'bg-primary' :
					'bg-warning'}">
                        ${b.status}
                    </span>
                </td>

                <td>
                    <button class="btn btn-sm btn-info"
                        onclick="openProgressModal(${b.id}, ${b.totalDays}, ${b.completedDays})">
                        Update
                    </button>
                </td>
            </tr>
        `;
	}).join("");
}

let stompClient = null;

function connectSocket() {

	const socket = new SockJS("http://localhost:8080/ws");
	  stompClient = Stomp.over(socket);

	  stompClient.connect({}, function () {

	      console.log("✅ Connected to WebSocket");

	      stompClient.subscribe("/topic/notifications", function (message) {

	          const data = JSON.parse(message.body);

	          console.log("📩 Notification:", data.message);

	          showNotification(data.message);
	      });
	  });
}

let notifCount = 0;



function showNotification(message, time = null) {

	notifCount++;

	const countEl = document.getElementById("notifCount");
	if (countEl) countEl.innerText = notifCount;

	// Save to localStorage
	let history = JSON.parse(localStorage.getItem("notifications") || "[]");
	
	const emptyMsg = document.getElementById("emptyMsg");
	if (emptyMsg) emptyMsg.style.display = "none";

	const newNotif = {
		message: message,
		time: time || new Date().toLocaleString()
	};

	history.unshift(newNotif);
	localStorage.setItem("notifications", JSON.stringify(history));

	// Show in UI (if exists)
	const container = document.getElementById("notificationList");
	if (!container) return;

	const div = document.createElement("div");
	div.className = "alert alert-info shadow-sm";

	div.innerHTML = `
        <div>${message}</div>
        <small class="text-muted">${newNotif.time}</small>
    `;

	container.prepend(div);

	alert("🔔 " + message);
}

/* ================= BELL TOGGLE ================= */

function toggleNotifications() {

	const box = document.getElementById("notificationList");

	if (!box) return;

	if (box.style.display === "none" || box.style.display === "") {
		box.style.display = "block";
	} else {
		box.style.display = "none";
	}
}

function openProgressModal(id, total, completed) {

	document.getElementById("batchId").value = id;
	document.getElementById("totalDays").value = total;
	document.getElementById("completedDays").value = completed;

	new bootstrap.Modal(document.getElementById("progressModal")).show();
}

function calculateProgress(startDate, endDate) {
	if (!startDate || !endDate) return 0;

	const start = new Date(startDate).getTime();
	const end = new Date(endDate).getTime();
	const now = Date.now();

	// Invalid dates safety
	if (isNaN(start) || isNaN(end) || start >= end) return 0;

	// Before start
	if (now <= start) return 0;

	// After end
	if (now >= end) return 100;

	// Progress calculation
	const progress = ((now - start) / (end - start)) * 100;

	return Math.round(progress);
}

function loadOldNotificationsStyled() {

	const container = document.getElementById("notificationList");
	const emptyMsg = document.getElementById("emptyMsg");

	if (!container) return; // ✅ safety

	const history = JSON.parse(localStorage.getItem("notifications") || "[]");

	container.innerHTML = "";

	if (history.length === 0) {
		if (emptyMsg) emptyMsg.style.display = "block"; // ✅ safe
		return;
	}

	if (emptyMsg) emptyMsg.style.display = "none";

	history.forEach(n => {

		const div = document.createElement("div");
		div.className = "notification-item";

		div.innerHTML = `
            <div>${n.message}</div>
            <div class="time">${n.time}</div>
        `;

		container.appendChild(div);
	});
}

function clearNotifications() {
	localStorage.removeItem("notifications");
	document.getElementById("notificationList").innerHTML = "";
	document.getElementById("notifCount").innerText = 0;
}

/* ================= TRAINING SESSIONS ================= */

// Load sessions for selected batch
async function loadSessions(batchId) {
	const table = document.getElementById("sessionTable");
	if (!table) return;

	const data = await apiRequest(`/trainer/session/${batchId}`);
	if (!data || !data.length) {
		table.innerHTML = `<tr><td colspan="5">No sessions</td></tr>`;
		return;
	}

	table.innerHTML = data.map(s => `
        <tr>
            <td>${s.topic}</td>
            <td>${s.sessionDate}</td>
            <td>
                <span class="badge ${s.status === 'COMPLETED' ? 'bg-success' : 'bg-warning'}">
                    ${s.status}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-success"
                    onclick="markSessionDone(${s.id})">
                    Done
                </button>

                <button class="btn btn-sm btn-danger"
                    onclick="deleteSession(${s.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join("");
}

// Create session
async function createSession() {
	const batchId = document.getElementById("batchSelect").value;

	const data = {
		topic: document.getElementById("topic").value,
		sessionDate: document.getElementById("sessionDate").value
	};

	await apiRequest(`/trainer/session/${batchId}`, "POST", data);
	alert("Session Created ✅");

	loadSessions(batchId);
}

// Mark completed
async function markSessionDone(id) {
	await apiRequest(`/trainer/session/${id}/status?status=COMPLETED`, "PUT");
	alert("Marked as Completed ✅");

	const batchId = document.getElementById("batchSelect").value;
	loadSessions(batchId);
}

// Delete
async function deleteSession(id) {
	if (!confirm("Delete session?")) return;

	await apiRequest(`/trainer/session/${id}`, "DELETE");

	const batchId = document.getElementById("batchSelect").value;
	loadSessions(batchId);
}

async function loadBatchDropdown() {
	const dropdown = document.getElementById("batchSelect");

	const data = await apiRequest("/trainer/my-batches");

	if (!data || !data.length) {
		dropdown.innerHTML = `<option>No batches</option>`;
		return;
	}

	dropdown.innerHTML = `<option value="">Select Batch</option>`;

	data.forEach(b => {
		dropdown.innerHTML += `
            <option value="${b.id}">${b.batchName}</option>
        `;
	});

	// ✅ AUTO LOAD FIRST BATCH PRACTICALS
	const firstId = data[0]?.id;

	if (firstId) {
		dropdown.value = firstId;
		loadPracticals(firstId);
	}
}

//create practical 

async function loadPracticals(batchId) {
    try {
        const data = await apiRequest(`/trainer/practical/${batchId}`);

        if (!data) return;

        renderPracticals(data);

    } catch (err) {
        console.error("Load Practicals Error:", err);
    }
}

async function updateStatus(id, status) {
	await fetch(`${BASE_URL}/trainer/practical/${id}/status?status=${status}`, {
		method: "PUT",
		headers: {
			"Authorization": "Bearer " + localStorage.getItem("token")
		}
	});

	const batchId = document.getElementById("batchSelect").value;
	loadPracticals(batchId);
}

async function createPractical() {
	const title = document.getElementById("pTitle").value;
	const marks = document.getElementById("marks").value;
	const batchId = document.getElementById("batchSelect").value;

	if (!title || !batchId) {
		alert("Please fill all fields");
		return;
	}

	try {
		await fetch(`${BASE_URL}/trainer/practical/${batchId}`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				"Authorization": "Bearer " + localStorage.getItem("token")
			},
			body: JSON.stringify({
				title,
				marks
			})
		});
		// ✅ VERY IMPORTANT
		loadPracticals(batchId);   // 🔥 refresh list

		// optional reset
		document.getElementById("pTitle").value = "";
		document.getElementById("marks").value = "";

	} catch (err) {
		console.error(err);
		alert("Error creating practical");
	}
}

async function markPracticalDone(id) {
	await apiRequest(`/trainer/practical/${id}?status=COMPLETED`, "PUT");

	const batchId = document.getElementById("batchSelect").value;
	loadPracticals(batchId);
}

async function deletePractical(id) {
	if (!confirm("Delete practical?")) return;

	await fetch(`${BASE_URL}/trainer/practical/${id}`, {
		method: "DELETE",
		headers: {
			"Authorization": "Bearer " + localStorage.getItem("token")
		}
	});

	const batchId = document.getElementById("batchSelect").value;
	loadPracticals(batchId);
}


function renderPracticals(list) {
    const table = document.getElementById("practicalTable");

    // ✅ FIX: prevent crash
    if (!table) {
        console.warn("practicalTable not found in HTML");
        return;
    }

    if (!list || list.length === 0) {
        table.innerHTML = `<tr><td colspan="4">No Practicals Found</td></tr>`;
        return;
    }

    table.innerHTML = list.map(p => `
        <tr>
            <td>${p.title}</td>
            <td>${p.status}</td>
            <td>${p.marks ?? '-'}</td>
            <td>
                <button onclick="deletePractical(${p.id})">Delete</button>
            </td>
        </tr>
    `).join("");
}
//create project

async function loadProjects(batchId) {
	const data = await apiRequest(`/trainer/project/${batchId}`);
	const table = document.getElementById("projectTable");

	if (!table) return;

	if (!data || !data.length) {
		table.innerHTML = `<tr><td colspan="5">No projects found</td></tr>`;
		return;
	}

	table.innerHTML = data.map(p => `
        <tr>
            <td>${p.title}</td>
            <td>${p.deadline || "-"}</td>
            <td>
                <span class="badge ${p.status === 'COMPLETED' ? 'bg-success' : 'bg-warning'}">
                    ${p.status}
                </span>
            </td>
            <td>${p.score ?? "-"}</td>
            <td>
                <button class="btn btn-success btn-sm" onclick="markProjectDone(${p.id})">
                    Done
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteProject(${p.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join("");
}
async function createProject() {

	const batchSelect = document.getElementById("projectBatch");

	if (!batchSelect) {
		alert("Batch dropdown missing");
		return;
	}

	const batchId = batchSelect.value;

	if (!batchId) {
		alert("Please select batch");
		return;
	}

	const title = document.getElementById("projectTitle").value;

	if (!title) {
		alert("Enter project title");
		return;
	}

	await apiRequest(`/trainer/project/${batchId}`, "POST", {
		title: title,
		deadline: document.getElementById("deadline").value,
		score: document.getElementById("score").value
	});

	loadProjects(batchId);
}

async function markProjectDone(id) {
	await apiRequest(
		`/trainer/project/${id}/status?status=COMPLETED`,
		"PUT"
	);
	const batchId = document.getElementById("projectBatch").value;
	loadProjects(batchId);
}

async function deleteProject(id) {
	await apiRequest(`/trainer/project/${id}`, "DELETE");

	const batchId = document.getElementById("projectBatch").value;
	loadProjects(batchId);
}

async function createMock() {

	const batchId = document.getElementById("mockBatch").value;
	const interviewer = document.getElementById("interviewer").value;
	const dateEl = document.getElementById("date"); // or interviewDate

	if (!batchId) {
		alert("Select batch");
		return;
	}

	if (!interviewer) {
		alert("Enter interviewer name");
		return;
	}

	if (!dateEl || !dateEl.value) {
		alert("Select interview date");
		return;
	}

	await apiRequest(`/trainer/mock/${batchId}`, "POST", {
		interviewer: interviewer,
		interviewDate: dateEl.value
	});

	alert("Mock Interview Scheduled ✅");

	loadMocks(batchId);

	document.getElementById("interviewer").value = "";
	dateEl.value = "";
}

async function loadMocks(batchId) {
	const res = await fetch(`${BASE_URL}/trainer/mock/batch/${batchId}`, {
		headers: {
			"Authorization": "Bearer " + localStorage.getItem("token")
		}
	});

	const data = await res.json();

	const table = document.getElementById("mockTable");
	table.innerHTML = "";

	data.forEach(m => {
		table.innerHTML += `
		<tr>
		    <td>${m.interviewer}</td>
		    <td>${m.interviewDate}</td>

		    <td>
		        <span class="badge ${m.status === 'COMPLETED' ? 'bg-success' : 'bg-warning'
			}">
		            ${m.status}
		        </span>
		    </td>

		    <td>${m.score ?? '-'}</td>

			<td>
			    ${m.status !== 'COMPLETED'
				? `<button class="btn btn-success btn-sm"
			                onclick="openMockModal(${m.id})">
			                Complete
			           </button>`
				: ''
			}

			    <button class="btn btn-danger btn-sm"
			        onclick="deleteMock(${m.id})">
			        Delete
			    </button>
			</td>
		</tr>
		`;
	});
}

function openMockModal(id) {
	document.getElementById("mockId").value = id;
	new bootstrap.Modal(document.getElementById("mockModal")).show();
}

async function completeMock() {

	const id = document.getElementById("mockId").value;

	const score = document.getElementById("mockScore").value;
	const feedback = document.getElementById("mockFeedback").value;

	await apiRequest(`/trainer/mock/${id}/complete`, "PUT", {
		score: score,
		feedback: feedback
	});

	alert("Interview Completed ✅");

	const batchId = document.getElementById("mockBatch").value;
	loadMocks(batchId);

	bootstrap.Modal.getInstance(
		document.getElementById("mockModal")
	).hide();
}

async function deleteMock(id) {
	if (!confirm("Delete this interview?")) return;

	await apiRequest(`/trainer/mock/${id}`, "DELETE");

	const batchId = document.getElementById("mockBatch").value;
	loadMocks(batchId);
}

async function loadMockBatchDropdown() {
	const dropdown = document.getElementById("mockBatch");

	if (!dropdown) return;

	const data = await apiRequest("/trainer/my-batches");

	if (!data || !data.length) {
		dropdown.innerHTML = `<option>No batches</option>`;
		return;
	}

	dropdown.innerHTML = `<option value="">Select Batch</option>`;

	data.forEach(b => {
		dropdown.innerHTML += `
            <option value="${b.id}">${b.batchName}</option>
        `;
	});

	// ✅ Auto load first batch
	const firstId = data[0]?.id;

	if (firstId) {
		dropdown.value = firstId;
		loadMocks(firstId);
	}

	// ✅ On change
	dropdown.onchange = () => {
		if (dropdown.value) {
			loadMocks(dropdown.value);
		}
	};
}

async function loadProjectDropdown() {
	const dropdown = document.getElementById("projectBatch");

	const data = await apiRequest("/trainer/my-batches");

	if (!data || !data.length) {
		dropdown.innerHTML = `<option>No batches</option>`;
		return;
	}

	dropdown.innerHTML = `<option value="">Select Batch</option>`;

	data.forEach(b => {
		dropdown.innerHTML += `
            <option value="${b.id}">${b.batchName}</option>
        `;
	});

	// ✅ Set first batch as selected
	const firstId = data[0]?.id;

	if (firstId) {
		dropdown.value = firstId;   // sync UI
		loadProjects(firstId);      // load data
	}

	// ✅ onchange handler
	dropdown.onchange = () => {
		if (dropdown.value) {
			loadProjects(dropdown.value);
		}
	};
}

/* =====================================================
   PROGRESS PAGE - SAFE ADDITIONAL FUNCTIONS (DO NOT EDIT EXISTING CODE)
   ===================================================== */

/* ================= SAFE INIT FOR PROGRESS PAGE ================= */
function initProgressPage() {
    const dropdown = document.getElementById("batchSelect");

    if (!dropdown) return;

    // prevent duplicate binding
    if (!dropdown.dataset.bound) {
        dropdown.dataset.bound = "true";

        dropdown.addEventListener("change", function () {
            handleProgressBatchChange(this.value);
        });
    }

    loadProgressPageSafe();
}

/* ================= SAFE LOAD PROGRESS PAGE ================= */
async function loadProgressPageSafe() {
    const dropdown = document.getElementById("batchSelect");
    if (!dropdown) return;

    const data = await apiRequest("/trainer/my-batches");
    if (!data) return;

    dropdown.innerHTML = `<option value="">Select Batch</option>`;

    data.forEach(b => {
        const progress = safeProgressCalc(b);

        const option = document.createElement("option");
        option.value = b.id;
        option.textContent = `${b.batchName} (${progress}%)`;
        dropdown.appendChild(option);
    });

    // ✅ MOVE THIS AFTER DROPDOWN BUILD
    if (data.length > 0) {
        dropdown.value = data[0].id;
        handleProgressBatchChange(data[0].id);
    }
}
/* ================= SAFE BATCH CHANGE ================= */
async function handleProgressBatchChange(batchId) {
    if (!batchId) return;

    const hidden = document.getElementById("batchId");
    if (hidden) hidden.value = batchId;

    const batch = await apiRequest(`/trainer/batch/${batchId}`);
    if (!batch) return;

    // safe field binding
	setSafeValue("totalDays", batch.totalDays ?? batch.total_days ?? 0);
	setSafeValue("completedDays", batch.completedDays ?? batch.completed_days ?? 0);
    window.currentBatch = batch;

    safeUpdateUI(batch);
}

/* ================= SAFE UI UPDATE ================= */
function safeUpdateUI(batch) {
    if (!batch) return;

	const total = batch.totalDays ?? batch.total_days ?? 0;
	const completed = batch.completedDays ?? batch.completed_days ?? 0;
	
    let percent = total > 0 ? (completed / total) * 100 : 0;
    percent = Math.min(percent, 100);

    // progress bar
    const bar = document.getElementById("progressBar");
    if (bar) bar.style.width = percent.toFixed(1) + "%";

    const text = document.getElementById("progressText");
    if (text) text.innerText = percent.toFixed(1) + "%";

    // circle safe update
    const circle = document.getElementById("progressCircle");
    if (circle) {
        const radius = 50;
        const circumference = 2 * Math.PI * radius;

        circle.style.strokeDasharray = circumference;

        const offset =
            circumference - (percent / 100) * circumference;

        circle.style.strokeDashoffset = offset;
    }

    const circleText = document.getElementById("progressPercent");
    if (circleText) circleText.innerText = percent.toFixed(1) + "%";

    // stats
    setSafeText("totalCard", total);
    setSafeText("completedCard", completed);
    setSafeText("remainingCard", total - completed);

    // motivation
    const motivation = document.getElementById("motivationText");
    if (motivation) {
        if (percent >= 100) motivation.innerText = "🎉 Batch Completed!";
        else if (percent >= 70) motivation.innerText = "🔥 Almost There!";
        else if (percent >= 30) motivation.innerText = "💪 Keep Going!";
        else motivation.innerText = "🚀 Start Strong!";
    }

    updateSafeMilestones(percent);
}

/* ================= SAFE SUBMIT ================= */
async function submitProgressSafe() {

    const id = document.getElementById("batchId")?.value;

    if (!id) {
        alert("Please select batch");
        return;
    }

    const completed = Number(document.getElementById("completedDays")?.value || 0);
    const total = Number(document.getElementById("totalDays")?.value || 0);

    if (completed > total) {
        alert("Completed days cannot exceed total days");
        return;
    }

    // ✅ UPDATE PROGRESS
    const res = await apiRequest(`/trainer/batch/${id}/progress`, "PUT", {
        days: completed
    });

    if (!res) {
        alert("Update failed");
        return;
    }

    // ✅ FIXED HERE 👇
    await apiRequest(`/admin/notify-progress/${id}`, "POST");

    alert("Progress Updated Successfully ✅");

    handleProgressBatchChange(id);
}

/* ================= MILESTONE SAFE ================= */
function updateSafeMilestones(percent) {
    const beginner = document.getElementById("beginner");
    const intermediate = document.getElementById("intermediate");
    const advanced = document.getElementById("advanced");

    if (beginner) beginner.classList.remove("active");
    if (intermediate) intermediate.classList.remove("active");
    if (advanced) advanced.classList.remove("active");

    if (percent >= 0 && beginner) beginner.classList.add("active");
    if (percent >= 40 && intermediate) intermediate.classList.add("active");
    if (percent >= 80 && advanced) advanced.classList.add("active");
}

/* ================= HELPERS ================= */
function setSafeValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value ?? 0;
}

function setSafeText(id, value) {
    const el = document.getElementById(id);
    if (el) el.innerText = value ?? 0;
}

function safeProgressCalc(b) {
    if (!b) return 0;

    const total = b.totalDays ?? b.total_days ?? 0;
    const completed = b.completedDays ?? b.completed_days ?? 0;

    if (total > 0) {
        return Math.round((completed / total) * 100);
    }

    return b.progress ?? 0;
}
/* ================= AUTO INIT ================= */
window.addEventListener("load", () => {
    if (document.getElementById("batchSelect")) {
        initProgressPage();
    }
});

window.onload = () => {

	const role = localStorage.getItem("role");

	if (document.getElementById("trainers")) loadDashboard();
	if (document.getElementById("trainerTable")) loadTrainers();

	if (role === "ADMIN" && document.getElementById("batchTable")) {
		loadBatches();
	}

	if (role === "TRAINER" && document.getElementById("batchTable")) {
		loadMyBatchesPage();
	}

	if (document.getElementById("trainerBatchTable")) {
		loadTrainerBatches();
		loadTrainerDashboardSummary();
	}


	if (document.getElementById("projectBatch")) {
		loadProjectDropdown();
	}

	if (document.getElementById("notificationList")) {
		loadOldNotificationsStyled(); // ✅ SAFE NOW
	}

	if (document.getElementById("mockBatch")) {
		loadMockBatchDropdown();
	}

	const completedInput = document.getElementById("completedDays");
	if (completedInput) {
		completedInput.addEventListener("input", () => {
		    if (window.currentBatch) {
		        safeUpdateUI({
		            ...window.currentBatch,
		            completedDays: Number(completedInput.value)
		        });
		    }
		});
	}
	
	if (document.getElementById("practicalTable")) {
	    loadBatchDropdown();

	    const dropdown = document.getElementById("batchSelect"); // ✅ FIX

	    if (dropdown) {
	        dropdown.addEventListener("change", function () {
	            const batchId = this.value;

	            if (batchId) {
	                loadPracticals(batchId);
	            }
	        });
	    }
	}
	connectSocket();
};