package com.aistudio.smarttoolkit.tyvz

import com.aistudio.smarttoolkit.tyvz.model.Calculators
import com.aistudio.smarttoolkit.tyvz.model.EmiTenureType
import com.aistudio.smarttoolkit.tyvz.model.GstMode
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testGstAddition() {
    val result = Calculators.calculateGst(1000.0, 18.0, GstMode.ADD_GST)
    assertEquals(1000.0, result.netAmount, 0.01)
    assertEquals(90.0, result.cgst, 0.01)
    assertEquals(90.0, result.sgst, 0.01)
    assertEquals(180.0, result.totalGst, 0.01)
    assertEquals(1180.0, result.totalAmount, 0.01)
  }

  @Test
  fun testGstRemoval() {
    val result = Calculators.calculateGst(1180.0, 18.0, GstMode.REMOVE_GST)
    assertEquals(1000.0, result.netAmount, 0.01)
    assertEquals(90.0, result.cgst, 0.01)
    assertEquals(90.0, result.sgst, 0.01)
    assertEquals(180.0, result.totalGst, 0.01)
    assertEquals(1180.0, result.totalAmount, 0.01)
  }

  @Test
  fun testLoanEmiCalculation() {
    val result = Calculators.calculateEmi(100000.0, 12.0, 1, EmiTenureType.YEARS)
    // 100k at 12% for 1 year has EMI approx 8884.88
    assertEquals(8884.88, result.monthlyEmi, 0.1)
    assertEquals(6618.55, result.totalInterest, 1.0)
    assertEquals(106618.55, result.totalPayment, 1.0)
    assertTrue(result.principalPercentage > 0)
    assertTrue(result.interestPercentage > 0)
  }

  @Test
  fun testDiscountCalculation() {
    val result = Calculators.calculateDiscount(2000.0, 25.0)
    assertEquals(2000.0, result.originalPrice, 0.01)
    assertEquals(25.0, result.discountPercent, 0.01)
    assertEquals(500.0, result.discountAmount, 0.01)
    assertEquals(1500.0, result.finalPrice, 0.01)
  }

  @Test
  fun testAgeCalculation() {
    val birthCal = Calendar.getInstance().apply {
      set(2000, Calendar.JANUARY, 15, 0, 0, 0)
    }
    val todayCal = Calendar.getInstance().apply {
      set(2025, Calendar.JANUARY, 15, 0, 0, 0)
    }
    val result = Calculators.calculateAge(birthCal.timeInMillis, todayCal.timeInMillis)
    assertEquals(25, result.years)
    assertEquals(0, result.months)
    assertEquals(0, result.days)
  }
}

