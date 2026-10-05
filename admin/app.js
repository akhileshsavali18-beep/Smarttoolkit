(() => {
  "use strict";

  // Firebase REST configuration. This uses the existing Firebase project used by APS TOOLS.
  // Admin access is still enforced by the Firestore "admins/{uid}" allowlist/rules.
  const FIREBASE = {
    apiKey: "AIzaSyBhu35r2I63ofPgwLO4RshlYoH8g9v_qmA",
    projectId: "aps-tools"
  };

  const state = {
    idToken: sessionStorage.getItem("aps_admin_id_token") || "",
    uid: sessionStorage.getItem("aps_admin_uid") || "",
    email: sessionStorage.getItem("aps_admin_email") || "",
    range: "today"
  };

  const $ = (id) => document.getElementById(id);

  function setStatus(message, error = false) {
    const el = $("status");
    el.textContent = message || "";
    el.className = error ? "status error" : "status";
  }

  function setAuthed(authed) {
    $("loginView").hidden = authed;
    $("dashboardView").hidden = !authed;
    $("signOutBtn").hidden = !authed;
    $("adminEmail").textContent = state.email || "";
  }

  function authStore(data) {
    state.idToken = data.idToken || "";
    state.uid = data.localId || "";
    state.email = data.email || "";
    sessionStorage.setItem("aps_admin_id_token", state.idToken);
    sessionStorage.setItem("aps_admin_uid", state.uid);
    sessionStorage.setItem("aps_admin_email", state.email);
  }

  function clearAuth() {
    state.idToken = "";
    state.uid = "";
    state.email = "";
    sessionStorage.removeItem("aps_admin_id_token");
    sessionStorage.removeItem("aps_admin_uid");
    sessionStorage.removeItem("aps_admin_email");
  }

  async function signIn(email, password) {
    const url = `https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${encodeURIComponent(FIREBASE.apiKey)}`;
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, returnSecureToken: true })
    });
    const data = await res.json();
    if (!res.ok) {
      const code = data?.error?.message || "LOGIN_FAILED";
      throw new Error(code.replaceAll("_", " ").toLowerCase());
    }
    authStore(data);
    return data;
  }

  async function getAdminDoc() {
    if (!state.idToken || !state.uid) throw new Error("Not signed in");
    const url = `https://firestore.googleapis.com/v1/projects/${FIREBASE.projectId}/databases/(default)/documents/admins/${encodeURIComponent(state.uid)}`;
    const res = await fetch(url, { headers: { Authorization: `Bearer ${state.idToken}` } });
    if (res.status === 404) return null;
    const data = await res.json();
    if (!res.ok) throw new Error(data?.error?.message || "Admin check failed");
    return data;
  }

  function firestoreValue(v) {
    if (Object.prototype.hasOwnProperty.call(v, "stringValue")) return v.stringValue;
    if (Object.prototype.hasOwnProperty.call(v, "integerValue")) return Number(v.integerValue);
    if (Object.prototype.hasOwnProperty.call(v, "doubleValue")) return Number(v.doubleValue);
    if (Object.prototype.hasOwnProperty.call(v, "booleanValue")) return !!v.booleanValue;
    if (Object.prototype.hasOwnProperty.call(v, "timestampValue")) return new Date(v.timestampValue);
    if (Object.prototype.hasOwnProperty.call(v, "nullValue")) return null;
    if (v.arrayValue) return (v.arrayValue.values || []).map(firestoreValue);
    if (v.mapValue) return Object.fromEntries(Object.entries(v.mapValue.fields || {}).map(([k, x]) => [k, firestoreValue(x)]));
    return null;
  }

  function docToObject(doc) {
    return Object.fromEntries(Object.entries(doc?.fields || {}).map(([k, v]) => [k, firestoreValue(v)]));
  }

  async function runQuery(collectionId, from, to, orderField = "timestamp") {
    if (!state.idToken) throw new Error("Session expired");
    const structuredQuery = {
      from: [{ collectionId }],
      orderBy: [{ field: { fieldPath: orderField }, direction: "DESCENDING" }],
      limit: 500
    };

    if (from && to) {
      structuredQuery.where = {
        compositeFilter: {
          op: "AND",
          filters: [
            { fieldFilter: { field: { fieldPath: "timestamp" }, op: "GREATER_THAN_OR_EQUAL", value: { timestampValue: from.toISOString() } } },
            { fieldFilter: { field: { fieldPath: "timestamp" }, op: "LESS_THAN", value: { timestampValue: to.toISOString() } } }
          ]
        }
      };
    }

    const url = `https://firestore.googleapis.com/v1/projects/${FIREBASE.projectId}/databases/(default)/documents:runQuery`;
    const res = await fetch(url, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${state.idToken}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ structuredQuery })
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data?.error?.message || `Failed to query ${collectionId}`);
    }
    return (Array.isArray(data) ? data : [])
      .filter(x => x.document)
      .map(x => ({ name: x.document.name, ...docToObject(x.document) }));
  }

  function rangeBounds(key) {
    const now = new Date();
    const start = new Date(now);
    start.setHours(0, 0, 0, 0);

    if (key === "yesterday") {
      const from = new Date(start);
      from.setDate(from.getDate() - 1);
      return [from, start];
    }
    if (key === "7d") {
      const from = new Date(start);
      from.setDate(from.getDate() - 6);
      return [from, new Date(now.getTime() + 1000)];
    }
    if (key === "30d") {
      const from = new Date(start);
      from.setDate(from.getDate() - 29);
      return [from, new Date(now.getTime() + 1000)];
    }
    if (key === "month") {
      return [new Date(now.getFullYear(), now.getMonth(), 1), new Date(now.getTime() + 1000)];
    }
    return [start, new Date(now.getTime() + 1000)];
  }

  function dateLabel(key) {
    return ({
      today: "Today",
      yesterday: "Yesterday",
      "7d": "7 Days",
      "30d": "30 Days",
      month: "This Month",
      custom: "Custom"
    })[key] || "Today";
  }

  function setCard(id, value) {
    const el = $(id);
    if (el) el.textContent = value;
  }

  function renderList(id, rows, emptyText) {
    const box = $(id);
    if (!rows.length) {
      box.innerHTML = `<p>${emptyText}</p>`;
      return;
    }
    box.innerHTML = rows.slice(0, 10).map(r => {
      const when = r.timestamp instanceof Date ? r.timestamp.toLocaleString() : "—";
      return `<div class="row"><b>${escapeHtml(r.tool_name || r.tool || r.event || "Event")}</b><small>${escapeHtml(r.category || r.action || "")} • ${escapeHtml(when)}</small></div>`;
    }).join("");
  }

  function escapeHtml(s) {
    return String(s ?? "").replace(/[&<>"']/g, c => ({ "&":"&amp;", "<":"&lt;", ">":"&gt;", '"':"&quot;", "'":"&#39;" }[c]));
  }

  async function loadDashboard() {
    setStatus("Loading analytics…");
    const [from, to] = state.range === "custom"
      ? [new Date($("fromDate").value + "T00:00:00"), new Date($("toDate").value + "T00:00:00")]
      : rangeBounds(state.range);

    const [users, events] = await Promise.all([
      runQuery("analytics_users", null, null, "last_seen").catch(() => []),
      runQuery("analytics_events", from, to).catch(() => [])
    ]);

    const newUsers = users.filter(u => u.created_at instanceof Date && u.created_at >= from && u.created_at < to).length;
    const activeIds = new Set(
      events.filter(e => ["app_open", "app_opens", "app_opened"].includes(e.event_name)).map(e => e.user_id || e.uid).filter(Boolean)
    );
    const toolEvents = events.filter(e => ["tool_opened", "tool_used", "tool_action"].includes(e.event_name || e.type));
    const appOpens = events.filter(e => ["app_open", "app_opens", "app_opened"].includes(e.event_name || e.type));
    const counts = {};
    for (const e of toolEvents) {
      const name = e.tool_name || e.tool || "Unknown tool";
      counts[name] = (counts[name] || 0) + 1;
    }
    const topTools = Object.entries(counts).sort((a,b) => b[1]-a[1]);
    const top = topTools[0]?.[0] || "—";

    setCard("totalUsers", users.length || "0");
    setCard("activeUsers", activeIds.size || "0");
    setCard("newUsers", newUsers || "0");
    setCard("toolUses", toolEvents.length || "0");
    setCard("appOpens", appOpens.length || "0");
    setCard("mostUsedTool", top);

    renderList("toolAnalytics", topTools.map(([name, count]) => ({ tool_name: name, action: `${count} uses` })), "No tool analytics data yet.");
    const cat = {};
    for (const e of toolEvents) {
      const name = e.category || "Uncategorized";
      cat[name] = (cat[name] || 0) + 1;
    }
    renderList("categoryAnalytics", Object.entries(cat).sort((a,b) => b[1]-a[1]).map(([name, count]) => ({ tool_name: name, action: `${count} uses` })), "No category analytics data yet.");
    renderList("recentActivity", events, "No activity data yet.");
    setStatus(`Updated • ${dateLabel(state.range)}`);
  }

  async function bootstrap() {
    if (!state.idToken) {
      setAuthed(false);
      return;
    }
    try {
      const admin = await getAdminDoc();
      const fields = admin?.fields || {};
      const active = fields.active?.booleanValue !== false;
      if (!admin || !active) throw new Error("This account is not authorized as an APS TOOLS admin.");
      setAuthed(true);
      await loadDashboard();
    } catch (err) {
      clearAuth();
      setAuthed(false);
      setStatus(err.message || "Admin access check failed", true);
    }
  }

  $("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = $("email").value.trim();
    const password = $("password").value;
    if (!email || !password) return;
    $("loginBtn").disabled = true;
    setStatus("Signing in…");
    try {
      await signIn(email, password);
      const admin = await getAdminDoc();
      const active = admin?.fields?.active?.booleanValue !== false;
      if (!admin || !active) throw new Error("Login succeeded, but this account is not an authorized admin.");
      setAuthed(true);
      await loadDashboard();
    } catch (err) {
      clearAuth();
      setAuthed(false);
      setStatus(err.message || "Login failed", true);
    } finally {
      $("loginBtn").disabled = false;
    }
  });

  $("signOutBtn").addEventListener("click", () => {
    clearAuth();
    setAuthed(false);
    setStatus("Signed out.");
  });

  document.querySelectorAll(".filters button[data-range]").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".filters button").forEach(b => b.classList.remove("selected"));
      btn.classList.add("selected");
      state.range = btn.dataset.range;
      $("rangeLabel").textContent = dateLabel(state.range);
      if (state.range === "custom") $("customRange").hidden = false;
      else { $("customRange").hidden = true; loadDashboard().catch(err => setStatus(err.message, true)); }
    });
  });

  $("applyCustom").addEventListener("click", () => {
    const from = $("fromDate").value, to = $("toDate").value;
    if (!from || !to || from > to) { setStatus("Select a valid custom date range.", true); return; }
    state.range = "custom";
    loadDashboard().catch(err => setStatus(err.message, true));
  });

  // Bottom navigation is intentionally UI-first. Future pages can reuse the same authenticated session.
  document.querySelectorAll("nav a[data-tab]").forEach(a => {
    a.addEventListener("click", () => {
      document.querySelectorAll("nav a").forEach(x => x.classList.remove("on"));
      a.classList.add("on");
      setStatus(`${a.dataset.tab} section ready for the next analytics modules.`);
    });
  });

  setAuthed(!!state.idToken);
  bootstrap();
})();