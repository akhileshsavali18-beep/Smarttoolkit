export interface Env {
  FIREBASE_PROJECT_ID: string;
  FIREBASE_WEB_API_KEY: string;
  RAZORPAY_KEY_ID: string;
  RAZORPAY_MONTHLY_PLAN_ID: string;
  RAZORPAY_YEARLY_PLAN_ID: string;
  RAZORPAY_KEY_SECRET: string;
  RAZORPAY_WEBHOOK_SECRET: string;
  FIREBASE_SERVICE_ACCOUNT_EMAIL: string;
  FIREBASE_SERVICE_ACCOUNT_PRIVATE_KEY: string;
}

type Plan = "intro" | "monthly" | "yearly";

interface OrderRecord {
  uid: string;
  plan: Plan;
  amount: number;
  currency: "INR";
  createdAt: number;
}

const JSON_HEADERS = {
  "content-type": "application/json; charset=utf-8",
  "cache-control": "no-store",
};

function json(data: unknown, status = 200): Response {
  return new Response(JSON.stringify(data), { status, headers: JSON_HEADERS });
}

function base64Url(bytes: Uint8Array): string {
  let binary = "";
  for (const byte of bytes) binary += String.fromCharCode(byte);
  return btoa(binary).replace(/=/g, "").replace(/\+/g, "-").replace(/\//g, "_");
}

function pemToBytes(pem: string): Uint8Array {
  const body = pem.replace(/-----BEGIN PRIVATE KEY-----/g, "")
    .replace(/-----END PRIVATE KEY-----/g, "").replace(/\s/g, "");
  const binary = atob(body);
  return Uint8Array.from(binary, c => c.charCodeAt(0));
}

async function hmacHex(secret: string, message: string): Promise<string> {
  const key = await crypto.subtle.importKey(
    "raw", new TextEncoder().encode(secret), { name: "HMAC", hash: "SHA-256" }, false, ["sign"]
  );
  const signature = new Uint8Array(await crypto.subtle.sign("HMAC", key, new TextEncoder().encode(message)));
  return [...signature].map(b => b.toString(16).padStart(2, "0")).join("");
}

export function constantTimeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diff === 0;
}

async function verifyFirebaseIdToken(token: string, env: Env): Promise<string> {
  const response = await fetch(`https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=${encodeURIComponent(env.FIREBASE_WEB_API_KEY)}`, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ idToken: token }),
  });
  if (!response.ok) throw new Error("UNAUTHORIZED");
  const data = await response.json() as { users?: Array<{ localId?: string; emailVerified?: boolean; disabled?: boolean }> };
  const user = data.users?.[0];
  if (!user?.localId || user.disabled) throw new Error("UNAUTHORIZED");
  return user.localId;
}

async function firebaseAccessToken(env: Env): Promise<string> {
  const now = Math.floor(Date.now() / 1000);
  const header = base64Url(new TextEncoder().encode(JSON.stringify({ alg: "RS256", typ: "JWT" })));
  const claim = base64Url(new TextEncoder().encode(JSON.stringify({
    iss: env.FIREBASE_SERVICE_ACCOUNT_EMAIL,
    scope: "https://www.googleapis.com/auth/datastore",
    aud: "https://oauth2.googleapis.com/token",
    iat: now,
    exp: now + 3600,
  })));
  const unsigned = header + "." + claim;
  const key = await crypto.subtle.importKey(
    "pkcs8", pemToBytes(env.FIREBASE_SERVICE_ACCOUNT_PRIVATE_KEY).buffer as ArrayBuffer,
    { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" }, false, ["sign"]
  );
  const signature = new Uint8Array(await crypto.subtle.sign("RSASSA-PKCS1-v1_5", key, new TextEncoder().encode(unsigned)));
  const assertion = unsigned + "." + base64Url(signature);
  const response = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: { "content-type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion,
    }),
  });
  if (!response.ok) throw new Error("FIREBASE_AUTH_FAILED");
  const data = await response.json() as { access_token?: string };
  if (!data.access_token) throw new Error("FIREBASE_AUTH_FAILED");
  return data.access_token;
}

async function firestoreGet(env: Env, token: string, uid: string): Promise<Record<string, unknown> | null> {
  const url = `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/paymentOrders/${encodeURIComponent(uid)}`;
  const response = await fetch(url, { headers: { authorization: `Bearer ${token}` } });
  if (response.status === 404) return null;
  if (!response.ok) throw new Error("FIRESTORE_READ_FAILED");
  const doc = await response.json() as { fields?: Record<string, { stringValue?: string; integerValue?: string }> };
  const fields = doc.fields ?? {};
  return {
    orderId: fields.orderId?.stringValue,
    plan: fields.plan?.stringValue,
    amount: Number(fields.amount?.integerValue ?? 0),
    createdAt: Number(fields.createdAt?.integerValue ?? 0),
    paymentId: fields.paymentId?.stringValue,
    verifiedAt: Number(fields.verifiedAt?.integerValue ?? 0),
  };
}

async function firestorePatch(env: Env, token: string, path: string, fields: Record<string, unknown>): Promise<void> {
  const encoded: Record<string, unknown> = {};
  for (const [key, value] of Object.entries(fields)) {
    if (typeof value === "string") encoded[key] = { stringValue: value };
    else if (typeof value === "number") encoded[key] = { integerValue: String(value) };
    else if (typeof value === "boolean") encoded[key] = { booleanValue: value };
  }
  const response = await fetch(
    `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/${path}`,
    {
      method: "PATCH",
      headers: { authorization: `Bearer ${token}`, "content-type": "application/json" },
      body: JSON.stringify({ fields: encoded }),
    }
  );
  if (!response.ok) throw new Error("FIRESTORE_WRITE_FAILED");
}


async function firestoreCreateOnce(
  env: Env, token: string, documentPath: string, fields: Record<string, unknown>
): Promise<boolean> {
  const encoded: Record<string, unknown> = {};
  for (const [key, value] of Object.entries(fields)) {
    if (typeof value === "string") encoded[key] = { stringValue: value };
    else if (typeof value === "number") encoded[key] = { integerValue: String(value) };
    else if (typeof value === "boolean") encoded[key] = { booleanValue: value };
  }
  const name = `projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/${documentPath}`;
  const response = await fetch(
    `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents:commit`,
    {
      method: "POST",
      headers: { authorization: `Bearer ${token}`, "content-type": "application/json" },
      body: JSON.stringify({
        writes: [{
          update: { name, fields: encoded },
          currentDocument: { exists: false },
        }],
      }),
    }
  );
  if (response.ok) return true;
  if (response.status === 409 || response.status === 400) return false;
  throw new Error("FIRESTORE_EVENT_WRITE_FAILED");
}

async function firestoreCommitOnce(
  env: Env,
  token: string,
  claimPath: string,
  claimFields: Record<string, unknown>,
  updates: Array<{ path: string; fields: Record<string, unknown> }>
): Promise<boolean> {
  const encodeFields = (values: Record<string, unknown>) => {
    const fields: Record<string, unknown> = {};
    for (const [key, value] of Object.entries(values)) {
      if (typeof value === "string") fields[key] = { stringValue: value };
      else if (typeof value === "number" && Number.isFinite(value)) fields[key] = { integerValue: String(Math.trunc(value)) };
      else if (typeof value === "boolean") fields[key] = { booleanValue: value };
    }
    return fields;
  };
  const documentName = (path: string) =>
    `projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/${path}`;
  const writes = [
    ...updates.map(item => ({
      update: { name: documentName(item.path), fields: encodeFields(item.fields) },
    })),
    {
      update: { name: documentName(claimPath), fields: encodeFields(claimFields) },
      currentDocument: { exists: false },
    },
  ];
  const response = await fetch(
    `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents:commit`,
    {
      method: "POST",
      headers: { authorization: `Bearer ${token}`, "content-type": "application/json" },
      body: JSON.stringify({ writes }),
    }
  );
  if (response.ok) return true;
  const detail = await response.text();
  if (response.status === 400 && /ALREADY_EXISTS|exists:\\s*false|precondition/i.test(detail)) return false;
  throw new Error("FIRESTORE_ATOMIC_COMMIT_FAILED");
}

async function razorpayRequest(env: Env, path: string, method: string, body?: unknown): Promise<any> {
  const auth = btoa(`${env.RAZORPAY_KEY_ID}:${env.RAZORPAY_KEY_SECRET}`);
  const response = await fetch(`https://api.razorpay.com/v1${path}`, {
    method,
    headers: {
      authorization: `Basic ${auth}`,
      "content-type": "application/json",
    },
    body: body === undefined ? undefined : JSON.stringify(body),
  });
  if (!response.ok) throw new Error("RAZORPAY_REQUEST_FAILED");
  return response.json();
}

export function amountFor(plan: Plan): number {
  if (plan === "intro") return 900;
  if (plan === "monthly") return 9900;
  return 79900;
}

export function planFrom(value: unknown): Plan | null {
  return value === "intro" || value === "monthly" || value === "yearly" ? value : null;
}

async function createOrder(request: Request, env: Env): Promise<Response> {
  const uid = await authenticatedUid(request, env);
  const body = await request.json() as { plan?: unknown };
  const plan = planFrom(body.plan);
  if (plan !== "intro") return json({ error: "ONLY_INTRO_ORDER_ALLOWED" }, 400);

  const accessToken = await firebaseAccessToken(env);
  const existing = await firestoreGet(env, accessToken, uid);
  if (existing?.paymentId || existing?.verifiedAt) {
    return json({ error: "INTRO_ALREADY_REDEEMED" }, 409);
  }
  const subscriptionUrl = `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/subscriptions/${encodeURIComponent(uid)}`;
  const subscriptionResponse = await fetch(subscriptionUrl, { headers: { authorization: `Bearer ${accessToken}` } });
  if (subscriptionResponse.ok) {
    const subscriptionDoc = await subscriptionResponse.json() as { fields?: Record<string, { stringValue?: string }> };
    if (subscriptionDoc.fields?.plan?.stringValue === "intro") {
      return json({ error: "INTRO_ALREADY_REDEEMED" }, 409);
    }
  } else if (subscriptionResponse.status !== 404) {
    throw new Error("FIRESTORE_READ_FAILED");
  }
  if (existing?.orderId && Date.now() - Number(existing.createdAt ?? 0) < 10 * 60 * 1000) {
    return json({ orderId: existing.orderId, amount: existing.amount, currency: "INR", keyId: env.RAZORPAY_KEY_ID });
  }

  const amount = amountFor(plan);
  const order = await razorpayRequest(env, "/orders", "POST", {
    amount,
    currency: "INR",
    receipt: `aps_${uid.slice(0, 20)}_${Date.now()}`,
    notes: { firebase_uid: uid, plan, app: "APS_TOOLS" },
  }) as { id: string };

  await firestorePatch(env, accessToken, `paymentOrders/${encodeURIComponent(uid)}`, {
    uid, orderId: order.id, plan, amount, createdAt: Date.now(),
  });
  return json({ orderId: order.id, amount, currency: "INR", keyId: env.RAZORPAY_KEY_ID });
}

async function authenticatedUid(request: Request, env: Env): Promise<string> {
  const auth = request.headers.get("authorization") ?? "";
  const match = auth.match(/^Bearer\s+(.+)$/i);
  if (!match) throw new Error("UNAUTHORIZED");
  return verifyFirebaseIdToken(match[1], env);
}

async function verifyPayment(request: Request, env: Env): Promise<Response> {
  const uid = await authenticatedUid(request, env);
  const body = await request.json() as {
    razorpay_order_id?: string; razorpay_payment_id?: string; razorpay_signature?: string;
  };
  const orderId = body.razorpay_order_id ?? "";
  const paymentId = body.razorpay_payment_id ?? "";
  const signature = body.razorpay_signature ?? "";
  if (!orderId || !paymentId || !signature) return json({ error: "MISSING_PAYMENT_FIELDS" }, 400);

  const accessToken = await firebaseAccessToken(env);
  const record = await firestoreGet(env, accessToken, uid);
  if (!record || record.orderId !== orderId) return json({ error: "ORDER_NOT_FOUND" }, 400);
  if (record.paymentId || record.verifiedAt) return json({ error: "PAYMENT_ALREADY_VERIFIED" }, 409);

  const expected = await hmacHex(env.RAZORPAY_KEY_SECRET, `${orderId}|${paymentId}`);
  if (!constantTimeEqual(expected, signature)) return json({ error: "INVALID_SIGNATURE" }, 400);

  const payment = await razorpayRequest(env, `/payments/${encodeURIComponent(paymentId)}`, "GET") as {
    id: string; order_id: string; status: string; amount: number; currency: string;
  };
  if (payment.order_id !== orderId || payment.status !== "captured" ||
      payment.amount !== Number(record.amount) || payment.currency !== "INR") {
    return json({ error: "PAYMENT_NOT_CAPTURED" }, 400);
  }

  const plan = planFrom(record.plan);
  if (!plan) return json({ error: "INVALID_ORDER_PLAN" }, 400);
  const now = Date.now();
  const expiresAt = now + (plan === "intro" ? 30 : plan === "monthly" ? 30 : 365) * 24 * 60 * 60 * 1000;
  const committed = await firestoreCommitOnce(
    env,
    accessToken,
    `paymentVerificationClaims/${encodeURIComponent(orderId)}`,
    { uid, orderId, paymentId, claimedAt: now },
    [
      {
        path: `subscriptions/${encodeURIComponent(uid)}`,
        fields: { uid, status: "active", plan, paymentId, orderId, startedAt: now, expiresAt, updatedAt: now, source: "razorpay" },
      },
      {
        path: `paymentOrders/${encodeURIComponent(uid)}`,
        fields: { uid, orderId, plan, amount: Number(record.amount), paymentId, paymentStatus: "captured", createdAt: Number(record.createdAt), verifiedAt: now },
      },
    ]
  );
  if (!committed) return json({ error: "PAYMENT_ALREADY_VERIFIED" }, 409);
  return json({ verified: true, status: "active", expiresAt });
}


async function createSubscription(request: Request, env: Env): Promise<Response> {
  const uid = await authenticatedUid(request, env);
  const body = await request.json() as { plan?: unknown };
  if (body.plan !== "monthly" && body.plan !== "yearly") {
    return json({ error: "INVALID_SUBSCRIPTION_PLAN" }, 400);
  }

  const accessToken = await firebaseAccessToken(env);
  // The introductory payment must have been verified and its 30-day access period
  // must have ended before a recurring plan can be created.
  const subUrl = `https://firestore.googleapis.com/v1/projects/${env.FIREBASE_PROJECT_ID}/databases/(default)/documents/subscriptions/${encodeURIComponent(uid)}`;
  const subResponse = await fetch(subUrl, { headers: { authorization: `Bearer ${accessToken}` } });
  if (!subResponse.ok) return json({ error: "INTRO_SUBSCRIPTION_REQUIRED" }, 403);
  const subDoc = await subResponse.json() as { fields?: Record<string, { stringValue?: string; integerValue?: string }> };
  const subFields = subDoc.fields ?? {};
  const currentPlan = subFields.plan?.stringValue;
  const currentStatus = subFields.status?.stringValue;
  const expiresAt = Number(subFields.expiresAt?.integerValue ?? 0);
  if (currentPlan !== "intro" || currentStatus !== "active" || !expiresAt || Date.now() < expiresAt) {
    return json({ error: "INTRO_PERIOD_NOT_FINISHED" }, 403);
  }

  const planId = body.plan === "monthly" ? env.RAZORPAY_MONTHLY_PLAN_ID : env.RAZORPAY_YEARLY_PLAN_ID;
  if (!planId || planId.startsWith("REPLACE_")) return json({ error: "PLAN_NOT_CONFIGURED" }, 503);
  const totalCount = body.plan === "monthly" ? 120 : 10;
  const created = await razorpayRequest(env, "/subscriptions", "POST", {
    plan_id: planId,
    total_count: totalCount,
    quantity: 1,
    customer_notify: 1,
    notes: { firebase_uid: uid, app: "APS_TOOLS", selected_plan: body.plan },
  }) as { id?: string; short_url?: string; status?: string };
  if (!created.id || !created.short_url) throw new Error("SUBSCRIPTION_CREATE_FAILED");

  await firestorePatch(env, accessToken, `subscriptionRequests/${encodeURIComponent(created.id)}`, {
    uid, subscriptionId: created.id, plan: body.plan, planId,
    status: "created", createdAt: Date.now(),
  });
  return json({ subscriptionId: created.id, shortUrl: created.short_url, status: created.status ?? "created" });
}

async function webhook(request: Request, env: Env): Promise<Response> {
  const raw = await request.text();
  const supplied = request.headers.get("x-razorpay-signature") ?? "";
  const expected = await hmacHex(env.RAZORPAY_WEBHOOK_SECRET, raw);
  if (!supplied || !constantTimeEqual(expected, supplied)) {
    return json({ error: "INVALID_WEBHOOK_SIGNATURE" }, 400);
  }

  let event: {
    id?: string;
    event?: string;
    payload?: { subscription?: { entity?: { id?: string } } };
  };
  try {
    event = JSON.parse(raw);
  } catch {
    return json({ error: "INVALID_JSON" }, 400);
  }

  const supported = new Set([
    "subscription.activated", "subscription.charged",
    "subscription.cancelled", "subscription.halted",
    "subscription.paused", "subscription.resumed",
    "subscription.completed",
  ]);
  if (!event.event || !supported.has(event.event)) {
    return json({ received: true, ignored: true });
  }

  const eventId = request.headers.get("x-razorpay-event-id") ?? event.id ?? "";
  if (!eventId) return json({ error: "MISSING_EVENT_ID" }, 400);
  const subscriptionId = event.payload?.subscription?.entity?.id;
  if (!subscriptionId) return json({ error: "MISSING_SUBSCRIPTION_ID" }, 400);

  // Never trust webhook entity fields alone. Fetch the current subscription
  // directly from Razorpay before changing Firebase entitlements.
  const current = await razorpayRequest(
    env, `/subscriptions/${encodeURIComponent(subscriptionId)}`, "GET"
  ) as {
    id?: string; plan_id?: string; status?: string; current_end?: number;
    notes?: Record<string, string>; paid_count?: number;
  };
  const uid = current.notes?.firebase_uid;
  if (!uid || current.id !== subscriptionId) {
    return json({ error: "SUBSCRIPTION_IDENTITY_MISMATCH" }, 400);
  }

  const plan: Plan | null =
    current.plan_id === env.RAZORPAY_MONTHLY_PLAN_ID ? "monthly" :
    current.plan_id === env.RAZORPAY_YEARLY_PLAN_ID ? "yearly" : null;
  if (!plan) return json({ error: "UNKNOWN_RAZORPAY_PLAN" }, 400);

  const accessToken = await firebaseAccessToken(env);
  const now = Date.now();
  const currentEnd = Number(current.current_end ?? 0) * 1000;
  const activated = event.event === "subscription.activated" ||
    event.event === "subscription.charged" ||
    event.event === "subscription.resumed";
  const terminal = event.event === "subscription.cancelled" ||
    event.event === "subscription.halted" ||
    event.event === "subscription.paused" ||
    event.event === "subscription.completed";

  if (activated && current.status === "active" && (!currentEnd || currentEnd <= now)) {
    return json({ error: "INVALID_SUBSCRIPTION_PERIOD" }, 400);
  }
  const subscriptionUpdate: { path: string; fields: Record<string, unknown> }[] = [];
  if (activated && current.status === "active") {
    subscriptionUpdate.push({
      path: `subscriptions/${encodeURIComponent(uid)}`,
      fields: {
        uid, status: "active", plan, razorpaySubscriptionId: subscriptionId,
        planId: current.plan_id ?? "", startedAt: now, expiresAt: currentEnd,
        paidCount: Number(current.paid_count ?? 0), updatedAt: now,
        source: "razorpay_subscription",
      },
    });
  } else if (terminal) {
    subscriptionUpdate.push({
      path: `subscriptions/${encodeURIComponent(uid)}`,
      fields: {
        uid, status: current.status ?? "inactive", plan,
        razorpaySubscriptionId: subscriptionId, planId: current.plan_id ?? "",
        paidCount: Number(current.paid_count ?? 0), updatedAt: now,
        source: "razorpay_subscription",
      },
    });
  }
  const committed = await firestoreCommitOnce(
    env,
    accessToken,
    `razorpayWebhookEvents/${encodeURIComponent(eventId)}`,
    { eventId, event: event.event, subscriptionId, uid, state: "processed", receivedAt: now, processedAt: now },
    subscriptionUpdate
  );
  if (!committed) return json({ received: true, duplicate: true, event: event.event });
  return json({ received: true, processed: true, duplicate: false, event: event.event });
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: {
        "access-control-allow-origin": "*",
        "access-control-allow-methods": "GET,POST,OPTIONS",
        "access-control-allow-headers": "authorization,content-type,x-razorpay-signature",
        "access-control-max-age": "86400",
      }});
    }
    try {
      if (request.method === "GET" && url.pathname === "/health") return json({ ok: true, service: "aps-tools-payment" });
      if (request.method === "POST" && url.pathname === "/orders") return await createOrder(request, env);
      if (request.method === "POST" && url.pathname === "/verify") return await verifyPayment(request, env);
      if (request.method === "POST" && url.pathname === "/subscriptions/create") return await createSubscription(request, env);
      if (request.method === "POST" && url.pathname === "/webhook/razorpay") return await webhook(request, env);
      return json({ error: "NOT_FOUND" }, 404);
    } catch (error) {
      const message = error instanceof Error ? error.message : "INTERNAL_ERROR";
      const status = message === "UNAUTHORIZED" ? 401 : 500;
      return json({ error: status === 401 ? "UNAUTHORIZED" : "PAYMENT_SERVICE_ERROR" }, status);
    }
  },
};
