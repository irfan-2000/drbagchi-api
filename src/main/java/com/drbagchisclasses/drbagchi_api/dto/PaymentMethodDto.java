package com.drbagchisclasses.drbagchi_api.dto;

import java.util.List;

public class PaymentMethodDto
{
    public String CourseId;
    public String PaymentType;
    public String fixed_paymentMode;
    public String Totalprice;

    public List<InstallmentDto> Installments;
    public String MonthlyAmount;
    public String QuarterlyAmount;
    public String HalfYearlyAmount;
    public String YearlyAmount;
}
