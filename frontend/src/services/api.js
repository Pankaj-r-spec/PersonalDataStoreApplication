const API_BASE = 'http://localhost:8081/api';

function buildHeaders(extra = {}) {
  const token = localStorage.getItem('pds-token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extra
  };
}

async function readJson(url) {
  const response = await fetch(`${API_BASE}${url}`, {
    headers: buildHeaders()
  });
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }
  return response.json();
}

async function writeJson(url, payload, method = 'POST') {
  const response = await fetch(`${API_BASE}${url}`, {
    method,
    headers: buildHeaders(),
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  return response.status === 204 ? null : response.json();
}

export const login = async (email, password) => {
  const response = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
    throw new Error('Invalid credentials');
  }

  const data = await response.json();
  localStorage.setItem('pds-token', data.token);
  return data;
};

export const fetchDashboard = () => readJson('/dashboard');
export const fetchInvestments = () => readJson('/investments');
export const fetchExpenses = () => readJson('/expenses');
export const fetchNotifications = () => readJson('/notifications');
export const fetchForecast = () => readJson('/analytics/forecast');
export const createInvestment = (payload) => writeJson('/investments', payload, 'POST');
export const updateInvestment = (id, payload) => writeJson(`/investments/${id}`, payload, 'PUT');
export const createExpense = (payload) => writeJson('/expenses', payload, 'POST');
export const updateExpense = (id, payload) => writeJson(`/expenses/${id}`, payload, 'PUT');
export const deleteInvestment = (id) => writeJson(`/investments/${id}`, {}, 'DELETE');
export const deleteExpense = (id) => writeJson(`/expenses/${id}`, {}, 'DELETE');
