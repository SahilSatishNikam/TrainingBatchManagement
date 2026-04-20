const BASE_URL = "http://localhost:8080";

let currentBatch = null;

/* ================= LOAD BATCH DROPDOWN ================= */
async function loadProgressPage() {
    const dropdown = document.getElementById("batchSelect");
    if (!dropdown) return;

    const data = await apiRequest("/trainer/my-batches");
    if (!data || data.length === 0) {
        dropdown.innerHTML = `<option>No batches found</option>`;
        return;
    }

    dropdown.innerHTML = `<option value="">Select Batch</option>`;

    data.forEach(b => {
        const progress = calculateProgressValue(b);

        dropdown.innerHTML += `
            <option value="${b.id}">
                ${b.batchName} (${progress}%)
            </option>
        `;
    });

    dropdown.onchange = function () {
        handleBatchSelect(this.value);
    };
}

/* ================= HANDLE BATCH SELECT ================= */
async function handleBatchSelect(batchId) {
    if (!batchId) return;

    document.getElementById("batchId").value = batchId;

    const batch = await apiRequest(`/trainer/batch/${batchId}`);
    if (!batch) return;

    currentBatch = batch;

    document.getElementById("totalDays").value = batch.totalDays || 0;
    document.getElementById("completedDays").value = batch.completedDays || 0;

    updateProgressUI(batch);
}

/* ================= UPDATE PROGRESS UI ================= */
function updateProgressUI(batch = currentBatch) {

    if (!batch) return;

    const total = batch.totalDays || 0;
    const completed = batch.completedDays || 0;

    let percent = total > 0 ? (completed / total) * 100 : 0;
    percent = Math.min(percent, 100).toFixed(1);

    /* BAR */
    const bar = document.getElementById("progressBar");
    if (bar) bar.style.width = percent + "%";

    const text = document.getElementById("progressText");
    if (text) text.innerText = percent + "%";

    /* CIRCLE */
    const circle = document.getElementById("progressCircle");
    const circleText = document.getElementById("progressPercent");

    if (circle) {
        const radius = 50;
        const circumference = 2 * Math.PI * radius;

        circle.style.strokeDasharray = circumference;
        circle.style.strokeDashoffset =
            circumference - (percent / 100) * circumference;
    }

    if (circleText) circleText.innerText = percent + "%";

    /* STATS */
    const remaining = total - completed;

    setText("totalCard", total);
    setText("completedCard", completed);
    setText("remainingCard", remaining);

    /* MOTIVATION */
    const motivation = document.getElementById("motivationText");

    if (motivation) {
        if (percent == 100) {
            motivation.innerText = "🎉 Great Job! Batch Completed!";
        } else if (percent > 70) {
            motivation.innerText = "🔥 Almost There!";
        } else if (percent > 30) {
            motivation.innerText = "💪 Keep Going!";
        } else {
            motivation.innerText = "🚀 Start Strong!";
        }
    }

    updateMilestones(percent);
}

/* ================= SUBMIT PROGRESS ================= */
async function submitProgress() {

    const id = document.getElementById("batchId").value;
    if (!id) {
        alert("Please select batch");
        return;
    }

    const completed = Number(document.getElementById("completedDays").value);
    const total = Number(document.getElementById("totalDays").value);

    if (completed > total) {
        alert("Completed days cannot exceed total days");
        return;
    }

    const res = await apiRequest(`/trainer/batch/${id}/progress`, "PUT", {
        days: completed
    });

    if (!res) {
        alert("Update failed");
        return;
    }

    alert("Progress Updated Successfully ✅");

    handleBatchSelect(id);
    loadProgressPage();
}

/* ================= MILESTONE LOGIC ================= */
function updateMilestones(percent) {

    const beginner = document.getElementById("beginner");
    const intermediate = document.getElementById("intermediate");
    const advanced = document.getElementById("advanced");

    resetMilestone(beginner);
    resetMilestone(intermediate);
    resetMilestone(advanced);

    if (percent >= 0) beginner.classList.add("active");
    if (percent >= 40) intermediate.classList.add("active");
    if (percent >= 80) advanced.classList.add("active");
}

function resetMilestone(el) {
    if (el) el.classList.remove("active");
}

/* ================= HELPERS ================= */
function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.innerText = value ?? 0;
}

function calculateProgressValue(b) {
    if (!b) return 0;

    if (b.totalDays && b.completedDays) {
        return Math.round((b.completedDays / b.totalDays) * 100);
    }

    return b.progress ?? 0;
}

/* ================= INIT ================= */
window.addEventListener("load", () => {
    if (document.getElementById("batchSelect")) {
        loadProgressPage();
    }
});