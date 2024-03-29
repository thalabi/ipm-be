package com.kerneldc.common.domain.converter;

import com.google.common.base.Preconditions;
import com.kerneldc.common.enums.ShortMonthEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ShortMonthEnumConverter implements AttributeConverter<ShortMonthEnum, Integer> {

	@Override
	public Integer convertToDatabaseColumn(ShortMonthEnum attribute) {

		Preconditions.checkArgument(attribute != null, "ShortMonthEnum value cannot be null");
		
        return ShortMonthEnum.getNumericValue(attribute);
	}

	@Override
	public ShortMonthEnum convertToEntityAttribute(Integer dbData) {
		
		Preconditions.checkArgument(dbData >= 1 && dbData <= 12, "Month integer column value [%s] is not between 1 & 12", dbData);
		
		return ShortMonthEnum.values()[dbData-1];
    }
}
