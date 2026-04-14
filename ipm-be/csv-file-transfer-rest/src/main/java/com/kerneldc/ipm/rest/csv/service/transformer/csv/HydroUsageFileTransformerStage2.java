package com.kerneldc.ipm.rest.csv.service.transformer.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.kerneldc.common.enums.IEntityEnum;
import com.kerneldc.common.enums.UploadTableEnum;
import com.kerneldc.ipm.rest.csv.service.transformer.FileProcessingContext;
import com.kerneldc.ipm.rest.csv.service.transformer.TransformationStageEnum;
import com.kerneldc.ipm.rest.csv.service.transformer.exception.AbortFileProcessingException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HydroUsageFileTransformerStage2 implements ICsvFileTransformer {

	@Override
	public void transform(FileProcessingContext context)
			throws AbortFileProcessingException {
		
		setUpFiles(context);
		
		try (var csvReader = new CSVReader(new BufferedReader(new FileReader(context.getInputFile())));
				var csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(context.getOutputFile())));) {
        	
			String[] cells;
			while ((cells = csvReader.readNext()) != null) {
        		LOGGER.debug("Line: {}", String.join(", ", cells));
            	csvWriter.writeNext(cells);
			}
			// write last line again to cause a duplicate logical key
        	//csvWriter.writeNext(lastCells);
        	
		} catch (IOException | CsvValidationException e) {
			throw new AbortFileProcessingException(getTransformerName(), e);
		}

		//return csvFileTransformerResult.withTransformedFileAndCsvTransformerExceptionList(outputFilePath, csvFileTransformerResult.csvTransformerExceptionList());
	}

	@Override
	public boolean canHandle(IEntityEnum uploadTableEnum, TransformationStageEnum transformationStageEnum) {

		return uploadTableEnum.equals(UploadTableEnum.HYDRO_USAGE)
				&& transformationStageEnum.equals(TransformationStageEnum.STAGE_TWO);
	}

	@Override
	public String getTransformerName() {
		return "HydroUsageFileTransformerStage2";
	}

}
