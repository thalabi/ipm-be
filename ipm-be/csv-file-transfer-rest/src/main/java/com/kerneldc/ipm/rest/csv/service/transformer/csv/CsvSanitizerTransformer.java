package com.kerneldc.ipm.rest.csv.service.transformer.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Component;

import com.kerneldc.common.enums.IEntityEnum;
import com.kerneldc.common.enums.UploadTableEnum;
import com.kerneldc.ipm.rest.csv.service.transformer.FileProcessingContext;
import com.kerneldc.ipm.rest.csv.service.transformer.TransformationStageEnum;
import com.kerneldc.ipm.rest.csv.service.transformer.exception.AbortFileProcessingException;
import com.kerneldc.ipm.util.AppFileUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CsvSanitizerTransformer implements ICsvFileTransformer {

	private static final String TRANSFORMER_NAME = "CsvSanitizerTransformer";

	@Override
	public void transform(FileProcessingContext context)
			throws AbortFileProcessingException {

		setUpFiles(context);
		
		try (var reader = new BufferedReader(new FileReader(context.getInputFile()));
				var writer = new BufferedWriter(new FileWriter(context.getOutputFile()))) {

			    var sb = new StringBuilder(1024); // Start with a reasonable capacity
			    String line;
			    var lineNumber = 0;
			    var target = "\\,";
			    var replacement = "\\\\\\\\,";

			    while ((line = reader.readLine()) != null) {
			    	lineNumber++;
			        sb.setLength(0); // Clear the buffer without discarding the internal array
			        sb.append(line);
			        
			        var modified = false;
			        var index = 0;

			        while ((index = sb.indexOf(target, index)) != -1) {
			            sb.replace(index, index + target.length(), replacement);
			            modified = true;
			            index += replacement.length(); // Move past the new replacement
			        }

			        if (modified) {
			        	LOGGER.info("Transformed line #{} [{}] to [{}]", lineNumber, line, sb.toString());
			        }

			        writer.write(sb.toString());
			        writer.newLine();
			    }
		} catch (IOException e) {
			throw new AbortFileProcessingException(getTransformerName(), e);
		}

		
	}

	@Override
	public boolean canHandle(IEntityEnum uploadTableEnum, TransformationStageEnum transformationStageEnum) {

		return uploadTableEnum.equals(UploadTableEnum.SUNSHINE_LIST)
				&& transformationStageEnum.equals(TransformationStageEnum.STAGE_ONE);
	}

	@Override
	public String getTransformerName() {
		return TRANSFORMER_NAME;
	}

}
