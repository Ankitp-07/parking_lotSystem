const API_URL = 'http://localhost:8080/api';

// Tab Navigation
document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
        e.preventDefault();
        const tab = item.dataset.tab;
        
        document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        
        item.classList.add('active');
        document.getElementById(tab).classList.add('active');
        document.getElementById('pageTitle').textContent = item.querySelector('span:last-child').textContent;
    });
});

// Live Time
function updateTime() {
    const now = new Date();
    document.getElementById('liveTime').textContent = now.toLocaleTimeString('en-IN');
}
updateTime();
setInterval(updateTime, 1000);

// Load Data
loadStatus();
loadHistory();
loadParkedVehicles();
setInterval(() => { loadStatus(); loadHistory(); loadParkedVehicles(); }, 5000);

// Park Vehicle
document.getElementById('parkForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const type = document.querySelector('input[name="vehicleType"]:checked').value;
    const number = document.getElementById('parkNumber').value.toUpperCase();
    
    try {
        const res = await fetch(`${API_URL}/park`, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `type=${type}&number=${number}`
        });
        const data = await res.json();
        const result = document.getElementById('parkResult');
        
        if (data.success) {
            result.className = 'result-box success show';
            result.innerHTML = `<strong>✅ Vehicle Parked Successfully!</strong><br>Ticket ID: <strong>${data.ticketId}</strong><br>Slot: <strong>${data.slot}</strong>`;
            document.getElementById('parkForm').reset();
            loadStatus();
        } else {
            result.className = 'result-box error show';
            result.innerHTML = `<strong>❌ Failed!</strong><br>${data.error}`;
        }
        setTimeout(() => result.classList.remove('show'), 5000);
    } catch (err) {
        showError('parkResult', err.message);
    }
});

// Exit Vehicle
document.getElementById('exitForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const number = document.getElementById('exitNumber').value.toUpperCase();
    
    try {
        const res = await fetch(`${API_URL}/exit`, {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `number=${number}`
        });
        const data = await res.json();
        const result = document.getElementById('exitResult');
        
        if (data.success) {
            result.className = 'result-box success show';
            result.innerHTML = `<strong>✅ Bill Generated!</strong><br>Duration: <strong>${data.hours} hour(s)</strong><br>Amount: <strong style="font-size:1.5rem;color:#2ecc71;">₹${data.amount.toFixed(2)}</strong>`;
            document.getElementById('exitForm').reset();
            loadStatus();
            loadHistory();
        } else {
            result.className = 'result-box error show';
            result.innerHTML = `<strong>❌ Failed!</strong><br>${data.error}`;
        }
        setTimeout(() => result.classList.remove('show'), 5000);
    } catch (err) {
        showError('exitResult', err.message);
    }
});

// Search Vehicle
document.getElementById('searchForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const number = document.getElementById('searchNumber').value.toUpperCase();
    
    try {
        const res = await fetch(`${API_URL}/search?number=${number}`);
        const data = await res.json();
        const result = document.getElementById('searchResult');
        
        if (data.found) {
            const h = Math.floor(data.minutes / 60);
            const m = data.minutes % 60;
            result.className = 'result-box success show';
            result.innerHTML = `<strong>✅ Vehicle Found!</strong><br>Type: <strong>${data.type}</strong><br>Ticket ID: <strong>${data.ticketId}</strong><br>Parked Since: <strong>${h}h ${m}m</strong>`;
        } else {
            result.className = 'result-box error show';
            result.innerHTML = `<strong>❌ Not Found!</strong><br>Vehicle is not currently parked.`;
        }
        setTimeout(() => result.classList.remove('show'), 5000);
    } catch (err) {
        showError('searchResult', err.message);
    }
});

// Load Status
async function loadStatus() {
    try {
        const res = await fetch(`${API_URL}/status`);
        const data = await res.json();
        
        document.getElementById('carTotal').textContent = data.car.total;
        document.getElementById('carAvailable').textContent = data.car.available;
        document.getElementById('bikeTotal').textContent = data.bike.total;
        document.getElementById('bikeAvailable').textContent = data.bike.available;
    } catch (err) {
        console.error('Status error:', err);
    }
}

// Load History
async function loadHistory() {
    try {
        const res = await fetch(`${API_URL}/history`);
        const data = await res.json();
        
        const html = data.length === 0 
            ? '<p style="text-align:center;color:#95a5a6;padding:2rem;">No transactions yet</p>'
            : data.map(item => `
                <div class="transaction-item">
                    <div class="transaction-info">
                        <div class="transaction-vehicle">${item.vehicleNo}</div>
                        <div class="transaction-type">${item.type}</div>
                    </div>
                    <div class="transaction-amount">₹${item.amount.toFixed(2)}</div>
                </div>
            `).join('');
        
        document.getElementById('dashboardHistory').innerHTML = html;
        document.getElementById('historyList').innerHTML = html;
    } catch (err) {
        console.error('History error:', err);
    }
}

// Load Parked Vehicles
async function loadParkedVehicles() {
    try {
        const res = await fetch(`${API_URL}/parked`);
        const data = await res.json();
        
        const cars = data.filter(v => v.type === 'CAR');
        const bikes = data.filter(v => v.type === 'BIKE');
        
        document.getElementById('parkedCars').textContent = cars.length;
        document.getElementById('parkedBikes').textContent = bikes.length;
        
        const html = data.length === 0
            ? '<p style="text-align:center;color:#95a5a6;padding:2rem;grid-column:1/-1;">No vehicles currently parked</p>'
            : data.map(v => {
                const icon = v.type === 'CAR' ? '🚗' : '🏍️';
                const cardClass = v.type === 'CAR' ? 'parked-card' : 'parked-card bike';
                return `
                    <div class="${cardClass}">
                        <div class="parked-header">
                            <span class="parked-type">${icon}</span>
                            <span class="parked-slot">${v.slot}</span>
                        </div>
                        <div class="parked-vehicle">${v.vehicleNo}</div>
                        <div class="parked-time">⏱️ ${v.duration}</div>
                    </div>
                `;
            }).join('');
        
        document.getElementById('parkedList').innerHTML = html;
    } catch (err) {
        console.error('Parked vehicles error:', err);
        document.getElementById('parkedList').innerHTML = '<p style="text-align:center;color:#e74c3c;padding:2rem;">Error loading parked vehicles</p>';
    }
}

function showError(id, msg) {
    const el = document.getElementById(id);
    el.className = 'result-box error show';
    el.innerHTML = `<strong>❌ Error!</strong><br>${msg}`;
    setTimeout(() => el.classList.remove('show'), 5000);
}
