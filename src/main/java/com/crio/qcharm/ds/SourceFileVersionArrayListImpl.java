package com.crio.qcharm.ds;

import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceFileVersionArrayListImpl implements SourceFileVersion {

  private SourceFileVersionArrayListImpl object;
  private List<String> fileData = new ArrayList<String>();
  private String fileName;

  public SourceFileVersionArrayListImpl(SourceFileVersionArrayListImpl obj) {
    this.object = obj;
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // FileInfo - contains following information
  // 1. fileName
  // 2. List of lines
  // Steps:
  // You task here is to construct SourceFileVersionArrayListImpl object by
  // 1. Storing the lines received from fileInfo object
  // 2. Storing the fileName received from fileInfo object.
  // Recommendations:
  // 1. Use Java ArrayList to store the lines received from fileInfo

  public SourceFileVersionArrayListImpl(FileInfo fileInfo) {
    this.fileData = fileInfo.getLines();
    this.fileName = fileInfo.getFileName();
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // 1. lineNumber - The line number
  // 2. numberOfLines - Number of lines requested
  // Expected functionality:
  // 1. Get the requested number of lines starting before the given line number.
  // 2. Make page object of this and return.
  // 3. For cursor position use the value from pageRequest
  // 4. For fileName use the value from pageRequest
  // NOTE:
  // If there less than requested number of lines, then return just those lines.
  // Zero is the first line number of the file
  // Example:
  // lineNumber - 50
  // numberOfLines - 25
  // Then lines returned is
  // (line number 25, line number 26 ... , line number 48, line number49)

  @Override
  public Page getLinesBefore(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    int num = numberOfLines;
    if (lineNumber - numberOfLines <= 0) {
      num = 0;
    }
    List<String> middle = new ArrayList<String>();
    for (int i = num; i < lineNumber; i++) {
      middle.add(fileData.get(i));
    }
    Page before = new Page(middle, num, pageRequest.getFileName(), pageRequest.getCursorAt());
    return before;

  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // 1. lineNumber - The line number
  // 2. numberOfLines - Number of lines requested
  // Expected functionality:
  // 1. Get the requested number of lines starting after the given line number.
  // 2. Make page object of this and return.
  // 3. For cursor position use the value from pageRequest
  // 4. For fileName use the value from pageRequest
  // NOTE:
  // If there less than requested number of lines, then return just those lines.
  // Zero is the first line number of the file @Override
  // Example:
  // lineNumber - 50
  // numberOfLines - 25
  // Then lines returned is
  // (line number 51, line number 52 ... , line number 74, line number75)

  @Override
  public Page getLinesAfter(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo() + 1;
    int numberOfLines = pageRequest.getNumberOfLines();
    int num = numberOfLines + lineNumber;
    if (lineNumber + numberOfLines > fileData.size()) {
      num = fileData.size();
    }
    List<String> middle = new ArrayList<String>();
    for (int i = lineNumber; i < num; i++) {
      middle.add(fileData.get(i));
    }
    if (lineNumber > fileData.size()) {
      lineNumber--;
    }
    Page after = new Page(middle, lineNumber, pageRequest.getFileName(), pageRequest.getCursorAt());
    return after;
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // 1. lineNumber - The line number
  // 2. numberOfLines - Number of lines requested
  // Expected functionality:
  // 1. Get the requested number of lines starting from the given line number.
  // 2. Make page object of this and return.
  // 3. For cursor position should be (startingLineNo, 0)
  // 4. For fileName use the value from pageRequest
  // NOTE:
  // If there less than requested number of lines, then return just those lines.
  // Zero is the first line number of the file @Override
  // Example:
  // lineNumber - 50
  // numberOfLines - 25
  // Then lines returned is
  // (line number 50, line number 51 ... , line number 73, line number74)

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    int num = numberOfLines + lineNumber;
    if (lineNumber + numberOfLines > fileData.size()) {
      num = fileData.size();
    }
    List<String> middle = new ArrayList<String>();
    for (int i = lineNumber; i < num; i++) {
      middle.add(fileData.get(i));
    }
    Page from = new Page(middle, lineNumber, pageRequest.getFileName(), new Cursor(lineNumber, 0));
    return from;
  }

  @Override
  public List<String> getAllLines() {
    return this.fileData;
  }
}
