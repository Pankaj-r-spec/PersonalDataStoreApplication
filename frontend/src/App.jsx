import { useEffect, useMemo, useState } from 'react';
import {
  fetchDashboard,
  fetchInvestments,
  fetchExpenses,
  fetchNotifications,
  fetchForecast,
  createInvestment,
  updateInvestment,
  createExpense,
  updateExpense,
  deleteInvestment,
  deleteExpense,
  login
} from './services/api';

const emptyInvestmentForm = {
  name: '',
  type: 'FD',
  category: 'Emergency Fund',
  amount: '',
  currentValue: '',
  purchaseDate: new Date().toISOString().slice(0, 10),
  notes: ''
};

const emptyExpenseForm = {
  category: 'Rent',
  amount: '',
  expenseDate: new Date().toISOString().slice(0, 10),
  recurring: true,
  notes: ''
};

function App() {
  const [dashboard, setDashboard] = useState({});
  const [investments, setInvestments] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [forecast, setForecast] = useState({ suggestions: [] });
  const [auth, setAuth] = useState({ email: 'demo@pds.local', password: 'password123' });
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(localStorage.getItem('pds-token')));
  const [investmentForm, setInvestmentForm] = useState(emptyInvestmentForm);
  const [expenseForm, setExpenseForm] = useState(emptyExpenseForm);
  const [editingInvestmentId, setEditingInvestmentId] = useState(null);
  const [editingExpenseId, setEditingExpenseId] = useState(null);
  const [activeTab, setActiveTab] = useState('overview');
  const [budgetPlan, setBudgetPlan] = useState({
    monthlyIncome: 75000,
    housing: 18000,
    groceries: 7500,
    transport: 3500,
    utilities: 4500,
    leisure: 6000,
    investments: 15000,
    savings: 12000
  });

  const tabs = [
    { id: 'overview', label: 'Overview' },
    { id: 'investments', label: 'Investments' },
    { id: 'expenses', label: 'Expenses' },
    { id: 'reports', label: 'Reports' },
    { id: 'insights', label: 'Insights' }
  ];

  const investmentBreakdown = useMemo(() => {
    const totals = {};
    investments.forEach((item) => {
      const value = Number(item.currentValue || item.amount || 0);
      totals[item.type] = (totals[item.type] || 0) + value;
    });

    const total = Object.values(totals).reduce((sum, value) => sum + value, 0) || 1;
    return Object.entries(totals)
      .map(([name, value]) => ({
        name,
        value,
        percent: (value / total) * 100
      }))
      .sort((a, b) => b.value - a.value);
  }, [investments]);

  const expenseBreakdown = useMemo(() => {
    const totals = {};
    expenses.forEach((item) => {
      const value = Number(item.amount || 0);
      totals[item.category] = (totals[item.category] || 0) + value;
    });

    const total = Object.values(totals).reduce((sum, value) => sum + value, 0) || 1;
    return Object.entries(totals)
      .map(([name, value]) => ({
        name,
        value,
        percent: (value / total) * 100
      }))
      .sort((a, b) => b.value - a.value);
  }, [expenses]);

  const savingsGoals = useMemo(() => {
    const totalInvested = Number(dashboard.totalInvested || 0);
    const totalCurrentValue = Number(dashboard.totalCurrentValue || 0);
    const totalMonthlyExpenses = Number(dashboard.totalMonthlyExpenses || 0);

    return [
      { label: 'Emergency fund', target: 300000, current: Math.min(Math.max(totalCurrentValue * 0.35, 0), 300000), tone: 'success' },
      { label: 'Annual investing', target: 600000, current: Math.min(Math.max(totalInvested, 0), 600000), tone: 'primary' },
      { label: 'Lifestyle buffer', target: 150000, current: Math.min(Math.max(totalMonthlyExpenses * 12, 0), 150000), tone: 'warning' }
    ];
  }, [dashboard]);

  const budgetSummary = useMemo(() => {
    const plannedExpense = Object.entries(budgetPlan)
      .filter(([key]) => key !== 'monthlyIncome')
      .reduce((sum, [, value]) => sum + Number(value || 0), 0);

    const remaining = Number(budgetPlan.monthlyIncome || 0) - plannedExpense;
    const usedPercent = Number(budgetPlan.monthlyIncome || 0) > 0 ? (plannedExpense / Number(budgetPlan.monthlyIncome || 0)) * 100 : 0;

    return { plannedExpense, remaining, usedPercent };
  }, [budgetPlan]);

  const handleBudgetChange = (field, value) => {
    setBudgetPlan((current) => ({
      ...current,
      [field]: Number(value || 0)
    }));
  };

  const handleExportCsv = () => {
    const rows = [
      ['Section', 'Metric', 'Value'],
      ['Dashboard', 'Total Invested', Number(dashboard.totalInvested || 0)],
      ['Dashboard', 'Current Value', Number(dashboard.totalCurrentValue || 0)],
      ['Dashboard', 'Monthly Expenses', Number(dashboard.totalMonthlyExpenses || 0)],
      ['Dashboard', 'Net Portfolio Value', Number(dashboard.netPortfolioValue || 0)],
      ['Forecast', 'Risk Level', forecast.riskLevel || ''],
      ['Forecast', 'Projected Cash Flow', Number(forecast.projectedCashFlowNextMonth || 0)],
      ['Forecast', 'Liquidity Ratio', Number(forecast.liquidityCoverageRatio || 0)]
    ];

    const investmentRows = investments.map((item) => ['Investment', item.name, `${item.type} | ${item.category} | ${Number(item.currentValue || item.amount || 0)}`]);
    const expenseRows = expenses.map((item) => ['Expense', item.category, `${item.recurring ? 'Recurring' : 'One-time'} | ${Number(item.amount || 0)}`]);

    const csv = [...rows, ...investmentRows, ...expenseRows]
      .map((row) => row.map((value) => `"${String(value).replace(/"/g, '""')}"`).join(','))
      .join('\n');

    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'personal-data-store-report.csv';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const loadData = async () => {
    const [summary, investmentData, expenseData, notificationData, forecastData] = await Promise.all([
      fetchDashboard(),
      fetchInvestments(),
      fetchExpenses(),
      fetchNotifications(),
      fetchForecast()
    ]);

    setDashboard(summary);
    setInvestments(investmentData);
    setExpenses(expenseData);
    setNotifications(notificationData);
    setForecast(forecastData);
  };

  useEffect(() => {
    if (isAuthenticated) {
      loadData();
    }
  }, [isAuthenticated]);

  const handleLogin = async (event) => {
    event.preventDefault();
    try {
      await login(auth.email, auth.password);
      setIsAuthenticated(true);
    } catch (error) {
      alert('Login failed. Please use demo@pds.local / password123');
    }
  };

  const handleInvestmentSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      ...investmentForm,
      amount: Number(investmentForm.amount),
      currentValue: Number(investmentForm.currentValue)
    };

    if (editingInvestmentId) {
      await updateInvestment(editingInvestmentId, payload);
      setEditingInvestmentId(null);
    } else {
      await createInvestment(payload);
    }

    setInvestmentForm(emptyInvestmentForm);
    await loadData();
  };

  const handleExpenseSubmit = async (event) => {
    event.preventDefault();
    const payload = {
      ...expenseForm,
      amount: Number(expenseForm.amount)
    };

    if (editingExpenseId) {
      await updateExpense(editingExpenseId, payload);
      setEditingExpenseId(null);
    } else {
      await createExpense(payload);
    }

    setExpenseForm(emptyExpenseForm);
    await loadData();
  };

  const beginEditInvestment = (item) => {
    setActiveTab('investments');
    setEditingInvestmentId(item.id);
    setInvestmentForm({
      name: item.name,
      type: item.type,
      category: item.category,
      amount: item.amount,
      currentValue: item.currentValue,
      purchaseDate: item.purchaseDate || new Date().toISOString().slice(0, 10),
      notes: item.notes || ''
    });
  };

  const beginEditExpense = (item) => {
    setActiveTab('expenses');
    setEditingExpenseId(item.id);
    setExpenseForm({
      category: item.category,
      amount: item.amount,
      expenseDate: item.expenseDate || new Date().toISOString().slice(0, 10),
      recurring: item.recurring,
      notes: item.notes || ''
    });
  };

  const handleDeleteInvestment = async (id) => {
    await deleteInvestment(id);
    await loadData();
  };

  const handleDeleteExpense = async (id) => {
    await deleteExpense(id);
    await loadData();
  };

  if (!isAuthenticated) {
    return (
      <div className="auth-shell">
        <form className="auth-card" onSubmit={handleLogin}>
          <h1>Personal Data Store</h1>
          <p>Login to continue</p>
          <input
            type="email"
            value={auth.email}
            onChange={(e) => setAuth({ ...auth, email: e.target.value })}
            placeholder="Email"
            required
          />
          <input
            type="password"
            value={auth.password}
            onChange={(e) => setAuth({ ...auth, password: e.target.value })}
            placeholder="Password"
            required
          />
          <button type="submit" className="primary-btn">Login</button>
          <small>Demo user: demo@pds.local / password123</small>
        </form>
      </div>
    );
  }

  return (
    <div className="page-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">@PersonalDataStoreApplication</p>
          <h1>Personal Data Store</h1>
        </div>
        <div className="header-badge">Smart finance + life tracking</div>
      </header>

      <nav className="tab-nav" aria-label="Main navigation">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            className={`tab-btn ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      {activeTab === 'overview' && (
        <section className="overview-layout">
          <div className="stats-grid">
            <div className="stat-card gradient-blue">
              <span>Total Invested</span>
              <strong>₹{Number(dashboard.totalInvested || 0).toLocaleString('en-IN')}</strong>
            </div>
            <div className="stat-card gradient-violet">
              <span>Current Value</span>
              <strong>₹{Number(dashboard.totalCurrentValue || 0).toLocaleString('en-IN')}</strong>
            </div>
            <div className="stat-card gradient-orange">
              <span>Monthly Expenses</span>
              <strong>₹{Number(dashboard.totalMonthlyExpenses || 0).toLocaleString('en-IN')}</strong>
            </div>
            <div className="stat-card gradient-green">
              <span>Net Portfolio Value</span>
              <strong>₹{Number(dashboard.netPortfolioValue || 0).toLocaleString('en-IN')}</strong>
            </div>
          </div>

          <div className="panel-grid overview-grid">
            <div className="panel wide-panel">
              <div className="panel-header">
                <h2>Financial snapshot</h2>
                <span className="status-pill">Healthy</span>
              </div>
              <div className="kpi-grid">
                <div className="mini-metric">
                  <label>Emergency Fund</label>
                  <strong>{dashboard.emergencyFundStatus || 'Pending'}</strong>
                </div>
                <div className="mini-metric">
                  <label>Risk Level</label>
                  <strong>{forecast.riskLevel || 'Loading...'}</strong>
                </div>
                <div className="mini-metric">
                  <label>Cash Flow</label>
                  <strong>₹{Number(forecast.projectedCashFlowNextMonth || 0).toLocaleString('en-IN')}</strong>
                </div>
                <div className="mini-metric">
                  <label>Liquidity Ratio</label>
                  <strong>{Number(forecast.liquidityCoverageRatio || 0).toFixed(2)}</strong>
                </div>
              </div>
            </div>

            <div className="panel">
              <h2>Quick alerts</h2>
              <div className="notification-list">
                <div className="notification alert">Emergency fund status: {dashboard.emergencyFundStatus || 'Pending'}</div>
                {notifications.slice(0, 3).map((item) => (
                  <div key={item.id} className="notification info">
                    <strong>{item.type}</strong> — {item.message}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>
      )}

      {activeTab === 'investments' && (
        <section className="panel-grid forms-grid">
          <div className="panel">
            <h2>{editingInvestmentId ? 'Update Investment' : 'Add Investment'}</h2>
            <form className="record-form" onSubmit={handleInvestmentSubmit}>
              <input value={investmentForm.name} onChange={(e) => setInvestmentForm({ ...investmentForm, name: e.target.value })} placeholder="Name" required />
              <select value={investmentForm.type} onChange={(e) => setInvestmentForm({ ...investmentForm, type: e.target.value })}>
                <option value="FD">FD</option>
                <option value="Emergency Fund">Emergency Fund</option>
                <option value="Stock">Stock</option>
                <option value="Mutual Fund">Mutual Fund</option>
                <option value="Gold">Gold</option>
              </select>
              <input value={investmentForm.category} onChange={(e) => setInvestmentForm({ ...investmentForm, category: e.target.value })} placeholder="Category" required />
              <input type="number" value={investmentForm.amount} onChange={(e) => setInvestmentForm({ ...investmentForm, amount: e.target.value })} placeholder="Amount invested" required />
              <input type="number" value={investmentForm.currentValue} onChange={(e) => setInvestmentForm({ ...investmentForm, currentValue: e.target.value })} placeholder="Current value" required />
              <input type="date" value={investmentForm.purchaseDate} onChange={(e) => setInvestmentForm({ ...investmentForm, purchaseDate: e.target.value })} />
              <textarea value={investmentForm.notes} onChange={(e) => setInvestmentForm({ ...investmentForm, notes: e.target.value })} placeholder="Notes" />
              <div className="button-row">
                <button type="submit" className="primary-btn">
                  {editingInvestmentId ? 'Update Investment' : 'Save Investment'}
                </button>
                {editingInvestmentId && (
                  <button
                    type="button"
                    className="secondary-btn"
                    onClick={() => {
                      setEditingInvestmentId(null);
                      setInvestmentForm(emptyInvestmentForm);
                    }}
                  >
                    Cancel
                  </button>
                )}
              </div>
            </form>
          </div>

          <div className="panel">
            <h2>Investments</h2>
            <ul>
              {investments.length === 0 ? (
                <li className="empty-state">No investments yet.</li>
              ) : (
                investments.map((item) => (
                  <li key={item.id} className="list-row">
                    <div>
                      <strong>{item.name}</strong>
                      <small>{item.type} • {item.category}</small>
                    </div>
                    <div className="list-actions">
                      <span>₹{Number(item.currentValue || 0).toLocaleString('en-IN')}</span>
                      <button className="secondary-btn" onClick={() => beginEditInvestment(item)}>Edit</button>
                      <button className="danger-btn" onClick={() => handleDeleteInvestment(item.id)}>Delete</button>
                    </div>
                  </li>
                ))
              )}
            </ul>
          </div>
        </section>
      )}

      {activeTab === 'expenses' && (
        <section className="panel-grid forms-grid">
          <div className="panel">
            <h2>{editingExpenseId ? 'Update Expense' : 'Add Expense'}</h2>
            <form className="record-form" onSubmit={handleExpenseSubmit}>
              <select value={expenseForm.category} onChange={(e) => setExpenseForm({ ...expenseForm, category: e.target.value })}>
                <option value="Rent">Rent</option>
                <option value="Milk">Milk</option>
                <option value="Vegetables">Vegetables</option>
                <option value="Travel">Travel</option>
                <option value="Utilities">Utilities</option>
              </select>
              <input type="number" value={expenseForm.amount} onChange={(e) => setExpenseForm({ ...expenseForm, amount: e.target.value })} placeholder="Amount" required />
              <input type="date" value={expenseForm.expenseDate} onChange={(e) => setExpenseForm({ ...expenseForm, expenseDate: e.target.value })} />
              <label className="checkbox-row">
                <input type="checkbox" checked={expenseForm.recurring} onChange={(e) => setExpenseForm({ ...expenseForm, recurring: e.target.checked })} />
                Recurring expense
              </label>
              <textarea value={expenseForm.notes} onChange={(e) => setExpenseForm({ ...expenseForm, notes: e.target.value })} placeholder="Notes" />
              <div className="button-row">
                <button type="submit" className="primary-btn">
                  {editingExpenseId ? 'Update Expense' : 'Save Expense'}
                </button>
                {editingExpenseId && (
                  <button
                    type="button"
                    className="secondary-btn"
                    onClick={() => {
                      setEditingExpenseId(null);
                      setExpenseForm(emptyExpenseForm);
                    }}
                  >
                    Cancel
                  </button>
                )}
              </div>
            </form>
          </div>

          <div className="panel">
            <h2>Monthly Expenses</h2>
            <ul>
              {expenses.length === 0 ? (
                <li className="empty-state">No expenses yet.</li>
              ) : (
                expenses.map((item) => (
                  <li key={item.id} className="list-row">
                    <div>
                      <strong>{item.category}</strong>
                      <small>{item.recurring ? 'Recurring' : 'One-time'}</small>
                    </div>
                    <div className="list-actions">
                      <span>₹{Number(item.amount || 0).toLocaleString('en-IN')}</span>
                      <button className="secondary-btn" onClick={() => beginEditExpense(item)}>Edit</button>
                      <button className="danger-btn" onClick={() => handleDeleteExpense(item.id)}>Delete</button>
                    </div>
                  </li>
                ))
              )}
            </ul>
          </div>
        </section>
      )}

      {activeTab === 'reports' && (
        <section className="reports-grid">
          <div className="panel">
            <h2>Portfolio allocation</h2>
            <div className="chart-list">
              {investmentBreakdown.length === 0 ? (
                <div className="empty-state">No investment data yet.</div>
              ) : (
                investmentBreakdown.map((item) => (
                  <div key={item.name} className="chart-row">
                    <div className="chart-meta">
                      <span>{item.name}</span>
                      <strong>₹{Number(item.value || 0).toLocaleString('en-IN')}</strong>
                    </div>
                    <div className="bar-track">
                      <div className="bar-fill blue" style={{ width: `${Math.min(item.percent, 100)}%` }} />
                    </div>
                    <small>{item.percent.toFixed(1)}%</small>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="panel">
            <h2>Expense breakdown</h2>
            <div className="chart-list">
              {expenseBreakdown.length === 0 ? (
                <div className="empty-state">No expense data yet.</div>
              ) : (
                expenseBreakdown.map((item) => (
                  <div key={item.name} className="chart-row">
                    <div className="chart-meta">
                      <span>{item.name}</span>
                      <strong>₹{Number(item.value || 0).toLocaleString('en-IN')}</strong>
                    </div>
                    <div className="bar-track">
                      <div className="bar-fill orange" style={{ width: `${Math.min(item.percent, 100)}%` }} />
                    </div>
                    <small>{item.percent.toFixed(1)}%</small>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="panel wide-panel">
            <div className="panel-header">
              <h2>Savings goals</h2>
              <button type="button" className="primary-btn" onClick={handleExportCsv}>Export CSV</button>
            </div>
            <div className="goal-stack">
              {savingsGoals.map((goal) => {
                const pct = Math.min((goal.current / goal.target) * 100, 100);
                return (
                  <div key={goal.label} className="goal-item">
                    <div className="goal-header">
                      <span>{goal.label}</span>
                      <strong>₹{Number(goal.current || 0).toLocaleString('en-IN')} / ₹{Number(goal.target || 0).toLocaleString('en-IN')}</strong>
                    </div>
                    <div className="bar-track">
                      <div className={`bar-fill ${goal.tone}`} style={{ width: `${pct}%` }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="panel wide-panel">
            <div className="panel-header">
              <h2>Monthly budget planner</h2>
              <span className="status-pill">{budgetSummary.remaining >= 0 ? 'On track' : 'Over budget'}</span>
            </div>
            <div className="budget-grid">
              {Object.entries({
                monthlyIncome: 'Monthly income',
                housing: 'Housing',
                groceries: 'Groceries',
                transport: 'Transport',
                utilities: 'Utilities',
                leisure: 'Leisure',
                investments: 'Investments',
                savings: 'Savings'
              }).map(([field, label]) => (
                <label key={field} className="budget-field">
                  <span>{label}</span>
                  <input
                    type="number"
                    value={budgetPlan[field]}
                    onChange={(event) => handleBudgetChange(field, event.target.value)}
                  />
                </label>
              ))}
            </div>
            <div className="budget-summary">
              <div className="mini-metric">
                <label>Planned spend</label>
                <strong>₹{Number(budgetSummary.plannedExpense || 0).toLocaleString('en-IN')}</strong>
              </div>
              <div className="mini-metric">
                <label>Remaining</label>
                <strong>₹{Number(budgetSummary.remaining || 0).toLocaleString('en-IN')}</strong>
              </div>
              <div className="mini-metric">
                <label>Budget used</label>
                <strong>{budgetSummary.usedPercent.toFixed(1)}%</strong>
              </div>
            </div>
          </div>
        </section>
      )}

      {activeTab === 'insights' && (
        <section className="panel-grid">
          <div className="panel">
            <h2>Forecast / Analytics</h2>
            <div className="notification-list">
              <div className="notification alert">Risk Level: {forecast.riskLevel || 'Loading...'}</div>
              <div className="notification info">Projected next month cash flow: ₹{Number(forecast.projectedCashFlowNextMonth || 0).toLocaleString('en-IN')}</div>
              <div className="notification info">Liquidity coverage ratio: {Number(forecast.liquidityCoverageRatio || 0).toFixed(2)}</div>
              {forecast.suggestions && forecast.suggestions.map((suggestion, index) => (
                <div key={index} className="notification info">{suggestion}</div>
              ))}
            </div>
          </div>

          <div className="panel">
            <h2>Notification Center</h2>
            <div className="notification-list">
              {notifications.length === 0 ? (
                <div className="notification info">Kafka integration ready for async notifications.</div>
              ) : (
                notifications.map((item) => (
                  <div key={item.id} className="notification info">
                    <strong>{item.type}</strong> — {item.message}
                  </div>
                ))
              )}
            </div>
          </div>
        </section>
      )}
    </div>
  );
}

export default App;
