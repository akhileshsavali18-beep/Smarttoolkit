import { describe, expect, it } from "vitest";
import { amountFor, constantTimeEqual, isCapturedPayment, isValidSubscriptionPeriod, planFrom } from "../src/index";

describe("payment plan validation", () => {
  it("accepts only the three known plan labels", () => {
    expect(planFrom("intro")).toBe("intro");
    expect(planFrom("monthly")).toBe("monthly");
    expect(planFrom("yearly")).toBe("yearly");
    expect(planFrom("premium")).toBeNull();
    expect(planFrom(null)).toBeNull();
    expect(planFrom(99)).toBeNull();
  });

  it("uses paise amounts for the configured plans", () => {
    expect(amountFor("intro")).toBe(900);
    expect(amountFor("monthly")).toBe(9900);
    expect(amountFor("yearly")).toBe(79900);
  });
});

describe("signature comparison", () => {
  it("accepts identical signatures", () => {
    expect(constantTimeEqual("a1b2c3", "a1b2c3")).toBe(true);
  });

  it("rejects changed or differently sized signatures", () => {
    expect(constantTimeEqual("a1b2c3", "a1b2c4")).toBe(false);
    expect(constantTimeEqual("abc", "abcd")).toBe(false);
    expect(constantTimeEqual("", "x")).toBe(false);
  });
});

describe("captured payment validation", () => {
  const expected = { orderId: "order_123", amount: 900 };

  it("accepts only the exact captured INR payment", () => {
    expect(isCapturedPayment({
      order_id: "order_123", status: "captured", amount: 900, currency: "INR",
    }, expected)).toBe(true);
  });

  it("rejects mismatched order, status, amount, or currency", () => {
    expect(isCapturedPayment({ order_id: "other", status: "captured", amount: 900, currency: "INR" }, expected)).toBe(false);
    expect(isCapturedPayment({ order_id: "order_123", status: "authorized", amount: 900, currency: "INR" }, expected)).toBe(false);
    expect(isCapturedPayment({ order_id: "order_123", status: "captured", amount: 899, currency: "INR" }, expected)).toBe(false);
    expect(isCapturedPayment({ order_id: "order_123", status: "captured", amount: 900, currency: "USD" }, expected)).toBe(false);
  });
});

describe("subscription period validation", () => {
  it("accepts a future expiry and rejects expired or invalid values", () => {
    expect(isValidSubscriptionPeriod(2000, 1000)).toBe(true);
    expect(isValidSubscriptionPeriod(1000, 1000)).toBe(false);
    expect(isValidSubscriptionPeriod(500, 1000)).toBe(false);
    expect(isValidSubscriptionPeriod(Number.NaN, 1000)).toBe(false);
  });
});
