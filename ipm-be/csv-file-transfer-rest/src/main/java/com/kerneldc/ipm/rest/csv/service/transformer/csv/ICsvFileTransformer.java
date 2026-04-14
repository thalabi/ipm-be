package com.kerneldc.ipm.rest.csv.service.transformer.csv;

import java.io.IOException;
import java.nio.file.Path;

import com.kerneldc.common.enums.IEntityEnum;
import com.kerneldc.ipm.rest.csv.service.transformer.FileProcessingContext;
import com.kerneldc.ipm.rest.csv.service.transformer.TransformationStageEnum;
import com.kerneldc.ipm.rest.csv.service.transformer.exception.AbortFileProcessingException;
import com.kerneldc.ipm.util.AppFileUtils;

public interface ICsvFileTransformer {

	void transform(FileProcessingContext context) throws AbortFileProcessingException;
	
	boolean canHandle(IEntityEnum entityEnum, TransformationStageEnum transformationStageEnum);

	String getTransformerName();
	
//	static FileProcessingContext getContext() {
//		return FileProcessingContext.get();
//	}
	default void setUpFiles(FileProcessingContext context) throws AbortFileProcessingException {
		try {
			context.setInputFilePath(context.getWorkInProgressFile());
			context.setOutputFilePath(AppFileUtils.createTempFile());
		} catch (IOException e) {
			throw new AbortFileProcessingException(getTransformerName(), e);
		}
		context.setWorkInProgressFile(context.getOutputFilePath());
	}
}
