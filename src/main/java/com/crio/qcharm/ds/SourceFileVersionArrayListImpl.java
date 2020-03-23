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

  // TODO: CRIO_TASK_MODULE_SEARCH
  // Input:
  // SearchRequest - contains following information
  // 1. pattern - pattern you want to search
  // 2. File name - file where you want to search for the pattern
  // Description:
  // 1. Find all occurrences of the pattern in the SourceFile
  // 2. Create an empty list of cursors
  // 3. For each occurrence starting position add to the list of cursors
  // 4. return the list of cursors
  // Recommendation:
  // 1. Use the simplest string search algorithm that you know.
  // Reference:
  // https://www.geeksforgeeks.org/naive-algorithm-for-pattern-searching/

  // TODO: CRIO_TASK_MODULE_IMPROVING_SEARCH
  // Input:
  // SearchRequest - contains following information
  // 1. pattern - pattern you want to search
  // 2. File name - file where you want to search for the pattern
  // Description:
  // 1. Find all occurrences of the pattern in the SourceFile
  // 2. Create an empty list of cursors
  // 3. For each occurrence starting position add to the list of cursors
  // 4. return the list of cursors
  // Recommendation:
  // 1. Use FASTER string search algorithm.
  // 2. Feel free to try any other algorithm/data structure to improve search
  // speed.
  // Reference:
  // https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/

  @Override
  public List<Cursor> getCursors(SearchRequest searchRequest) {
    List<Cursor> search = new ArrayList<Cursor>();
    int start = searchRequest.getStartingLineNo();
    String pat = searchRequest.getPattern();
    int M = pat.length();
    int lps[] = new int[M];
    LPS_Array(pat, M, lps);
    for (int k = start; k < fileData.size(); k++) {
      String txt = fileData.get(k);
      KMP_search(pat, txt, search, lps, k, M);
    }
    return search;
  }

  void KMP_search(String pat, String txt, List<Cursor> search, int lps[], int k, int M) {
    int N = txt.length();
    int i = 0;
    int j = 0;

    while (i < N) {
      if (pat.charAt(j) == txt.charAt(i)) {
        i++;
        j++;
      }
      if (j == M) {
        search.add(new Cursor(k, i - j));
        j = lps[j - 1];
      } else if (i < N && pat.charAt(j) != txt.charAt(i)) {
        if (j != 0) {
          j = lps[j - 1];
        } else {
          i++;
        }
      }
    }

  }

  void LPS_Array(String pat, int M, int lps[]) {
    int len = 0;
    int i = 1;
    lps[0] = 0;

    while (i < M) {
      if (pat.charAt(i) == pat.charAt(len)) {
        len++;
        lps[i] = len;
        i++;
      } else {
        if (len != 0) {
          len = lps[len - 1];
        } else {
          lps[i] = len;
          i++;
        }
      }
    }
  }

}
