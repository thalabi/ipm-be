package com.kerneldc.common.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class AbstractPersistableEntity extends AbstractEntity implements Serializable {
	
	protected AbstractPersistableEntity() {
		this.logicalKeyHolder = new LogicalKeyHolder();
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq_gen")
	private Long id;
	
	@Embedded
	@JsonIgnore
	private LogicalKeyHolder logicalKeyHolder;
	
	@Version
	@Column(name = "version")
	private Long version;

	@CsvBindByName
	@CsvIgnore(profiles = "csvWrite") // ignore column sourceCsvLineNumber when writing out csv file (when profile is set to csvWrite)
	@Transient
	private Long sourceCsvLineNumber;
	@Transient
	private String[] sourceCsvLine;
	
    protected abstract void setLogicalKeyHolder();
}
