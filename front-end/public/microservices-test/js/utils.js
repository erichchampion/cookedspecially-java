// Utility Functions

export function displayResult(containerId, data, success = true) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = '';

    if (success) {
        container.className = 'result-container success';
        const pre = document.createElement('pre');
        pre.className = 'code-block';
        pre.textContent = JSON.stringify(data, null, 2);
        container.appendChild(pre);
    } else {
        container.className = 'result-container error';
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = typeof data === 'string' ? data : JSON.stringify(data, null, 2);
        container.appendChild(errorDiv);
    }
}

export function displayError(containerId, error) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.className = 'result-container error';
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.innerHTML = `
        <strong>Error:</strong><br>
        ${typeof error === 'string' ? error : JSON.stringify(error, null, 2)}
    `;
    container.appendChild(errorDiv);
}

export function displayTable(containerId, data, columns) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = '';
    container.className = 'result-container success';

    if (!data || (Array.isArray(data) && data.length === 0)) {
        container.innerHTML = '<p>No data available</p>';
        return;
    }

    const items = Array.isArray(data) ? data : [data];

    const table = document.createElement('table');
    table.className = 'data-table';

    // Header
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    columns.forEach(col => {
        const th = document.createElement('th');
        th.textContent = col.label;
        headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Body
    const tbody = document.createElement('tbody');
    items.forEach(item => {
        const row = document.createElement('tr');
        columns.forEach(col => {
            const td = document.createElement('td');
            const value = col.field.split('.').reduce((obj, key) => obj?.[key], item);
            td.textContent = col.format ? col.format(value) : (value ?? 'N/A');
            row.appendChild(td);
        });
        tbody.appendChild(row);
    });
    table.appendChild(tbody);

    container.appendChild(table);
}

export function getFormData(formElement) {
    const formData = new FormData(formElement);
    const data = {};
    for (const [key, value] of formData.entries()) {
        data[key] = value;
    }
    return data;
}

export function clearForm(formElement) {
    formElement.reset();
}

export function setDefaultDates() {
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 30);

    const startDateInput = document.getElementById('start-date');
    const endDateInput = document.getElementById('end-date');

    if (startDateInput) {
        startDateInput.valueAsDate = startDate;
    }
    if (endDateInput) {
        endDateInput.valueAsDate = endDate;
    }
}

export function formatDate(date) {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
}

export function formatCurrency(amount) {
    if (amount === null || amount === undefined) return 'N/A';
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

export function formatNumber(num) {
    if (num === null || num === undefined) return 'N/A';
    return new Intl.NumberFormat('en-US').format(num);
}

export async function checkHealth(apiClient, statusElementId) {
    const statusElement = document.getElementById(statusElementId);
    if (!statusElement) return;

    statusElement.textContent = 'Checking...';
    statusElement.className = 'checking';

    try {
        const result = await apiClient.healthCheck();
        if (result.success) {
            statusElement.textContent = '✓ Service is online';
            statusElement.className = 'online';
        } else {
            statusElement.textContent = '✗ Service is offline';
            statusElement.className = 'offline';
        }
    } catch (error) {
        statusElement.textContent = '✗ Service is offline';
        statusElement.className = 'offline';
    }
}
