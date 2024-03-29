package com.kerneldc.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.SequenceGenerator;

import com.kerneldc.common.enums.GenderEnum;
import com.opencsv.bean.CsvBindByName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@SequenceGenerator(name = "default_seq_gen", sequenceName = "bio_stats_seq", allocationSize = 1)
@Getter @Setter

public class BioStats extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	@CsvBindByName
	@Setter(AccessLevel.NONE)
	private String name;
	@CsvBindByName(column = "sex")
	@Enumerated(EnumType.STRING)
	private GenderEnum gender;
	@CsvBindByName
	private Integer age;
	@CsvBindByName
	private Integer height;
	@CsvBindByName
	private Integer weight;

	public void setName(String name) {
		this.name = name;
		setLogicalKeyHolder();
	}
	@Override
	protected void setLogicalKeyHolder() {
		getLogicalKeyHolder().setLogicalKey(name);
	}

}
