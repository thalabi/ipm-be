package com.kerneldc.ipm.rest.csv.service.transformer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collection;

import org.springframework.stereotype.Service;

import com.kerneldc.ipm.rest.csv.service.GenericFileTransferService;
import com.kerneldc.ipm.rest.csv.service.transformer.csv.ICsvFileTransformer;
import com.kerneldc.ipm.rest.csv.service.transformer.exception.AbortFileProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvFileTransformerService {

	public static final String SOURCE_CSV_LINE_NUMBER = "sourceCsvLineNumber";
	private final Collection<ICsvFileTransformer> csvFileTransformerCollection;
	
//	public record CsvFileTransformerResult(Path transformedFile, long sourceCsvDataRowsCount, String[] sourceCsvHeaderColumns,
//			List<CsvTransformerException> csvTransformerExceptionList) {
//		public CsvFileTransformerResult() {
//			this(Paths.get(""), 0, new String[0], new ArrayList<CsvTransformerException>());
//		}
//		public CsvFileTransformerResult(Path transformedFile, long sourceCsvDataRowsCount) {
//			this(transformedFile, sourceCsvDataRowsCount, new String[0], new ArrayList<CsvTransformerException>());
//		}
//		public CsvFileTransformerResult(Path transformedFile, List<CsvTransformerException> csvTransformerExceptionList) {
//			this(transformedFile, 0, new String[0], csvTransformerExceptionList);
//		}
//		public CsvFileTransformerResult withTransformedFileAndCsvTransformerExceptionList(Path transformedFile, List<CsvTransformerException> csvTransformerExceptionList) {
//			return new CsvFileTransformerResult(transformedFile, sourceCsvDataRowsCount(), sourceCsvHeaderColumns(), csvTransformerExceptionList);
//		}
//		public CsvFileTransformerResult withSourceCsvDataRowsCount(long sourceCsvDataRowsCount) {
//			return new CsvFileTransformerResult(transformedFile(), sourceCsvDataRowsCount, sourceCsvHeaderColumns(), csvTransformerExceptionList());
//		}
//	}
	
	public void applyTransformers(InputStream inputStream, FileProcessingContext context) throws IOException, AbortFileProcessingException {
		
		//var context = FileProcessingContext.get();
		var pathAndLineCount = copyToFilePrefixingLineNumber(inputStream);
		context.setWorkInProgressFile(pathAndLineCount.filePath());
		context.setSourceCsvDataRowsCount(pathAndLineCount.lineCount());
		//var csvFileTransformerResult = context.getCsvFileTransformerResult();
		//csvFileTransformerResult = new CsvFileTransformerResult(pathAndLineCount.filePath(), pathAndLineCount.lineCount());

		for (ICsvFileTransformer transformer: csvFileTransformerCollection) {
			for (TransformationStageEnum stage : TransformationStageEnum.values())
				if (transformer.canHandle(context.getUploadTableEnum(), stage)) {
						LOGGER.info("Applying {} to transform csv file for {} table.", transformer.getTransformerName(), context.getUploadTableEnum());
						transformer.transform(context);
						LOGGER.info("transformedFile: {}", context.getWorkInProgressFile().toString());
				}
		}
		//return csvFileTransformerResult;
	}
//	private FileProcessingContext getContext() {
//		return FileProcessingContext.get();
//	}
	
	public record PathAndLineCount(Path filePath, long lineCount) {}
	/**
	 * Copies the contents of input stream to a temp file,
	 * adds and sets the sourceCsvLineNumber column to the line number in the source csv file
	 * 
	 * @param inputStream
	 * @return returns the path of the temp file and the line count of data lines
	 * @throws IOException
	 */
	private PathAndLineCount copyToFilePrefixingLineNumber(InputStream inputStream) throws IOException {
		var path = GenericFileTransferService.createTempFile();
		long lineNumber = 0;
		try (var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				var bufferedWriter = new BufferedWriter(new FileWriter(path.toFile()));) {
			while (bufferedReader.ready()) {
				lineNumber++;
				var inputLine = bufferedReader.readLine();
				if (lineNumber == 1) {
					bufferedWriter.write(SOURCE_CSV_LINE_NUMBER + "," + inputLine);
				} else {
					bufferedWriter.write(lineNumber + "," + inputLine);
				}
				bufferedWriter.newLine();
			}
		}
		return new PathAndLineCount(path, lineNumber);
	}
}
