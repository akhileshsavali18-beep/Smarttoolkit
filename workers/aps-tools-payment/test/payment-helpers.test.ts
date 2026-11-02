import { describe, expect, it } from "vitest";
import { amountFor, constantTimeEqual, planFrom } from "../src/index";

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
