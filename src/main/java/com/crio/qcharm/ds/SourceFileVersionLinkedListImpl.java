package com.crio.qcharm.ds;

import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceFileVersionLinkedListImpl implements SourceFileVersion {
  private SourceFileVersionLinkedListImpl obj;
  private LinkedList<String> fileData = new LinkedList<String>(); 
  private String filename;

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     FileInfo - contains following information
  //         1. fileName
  //         2. List of lines
  // Steps:
  //     You task here is to construct SourceFileVersionLinkedListImpl object by
  //     1. Storing the lines received from fileInfo object
  //     2. Storing the fileName received from fileInfo object.
  // Recommendations:
  //     1. Use Java LinkedList to store the lines received from fileInfo


  SourceFileVersionLinkedListImpl(FileInfo fileInfo) {
    for (String line : fileInfo.getLines()) {
      this.fileData.add( new String(line) ); 
     }
    this.filename=fileInfo.getFileName();
  }

  public SourceFileVersionLinkedListImpl() {
  }

  public SourceFileVersionLinkedListImpl(SourceFileVersionLinkedListImpl obj) {
    this.obj=obj;
  }


  @Override
  public SourceFileVersion apply(List<Edits> edits) {
    List<String> lines = new LinkedList<>();
    lines.addAll(lines);

    SourceFileVersionLinkedListImpl latest = new SourceFileVersionLinkedListImpl();

    for (Edits oneEdit : edits) {
      if (oneEdit instanceof UpdateLines) {
        apply((UpdateLines) oneEdit);
      } else {
        assert (oneEdit instanceof SearchReplace);
        apply((SearchReplace) oneEdit);
      }
    }
    return this;
  }



  @Override
  public void apply(SearchReplace searchReplace) {
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //     UpdateLines
  //        1. startingLineNo - starting line number of last time it received page from backend
  //        2. numberOfLines - number of lines received from backend last time.
  //        3. lines - present view of lines in range(startingLineNo,startingLineNo+numberOfLines)
  //        4. cursor
  // Description:
  //        1. Remove the line numbers in the range(starting line no, ending line no)
  //        2. Inserting the lines in new content starting position starting line no
  // Example:
  //        UpdateLines looks like this
  //            1. start line no - 50
  //            2. numberOfLines - 10
  //            3. lines - ["Hello world"]
  //
  //       Assume the file has 100 lines in it
  //
  //       File contents before edit:
  //       ==========================
  //       line no 0
  //       line no 1
  //       line no 2
  //          .....
  //       line no 99
  //
  //        File contents After Edit:
  //        =========================
  //        line no 0
  //        line no 1
  //        line no 2
  //        line no 3
  //         .....
  //        line no 49
  //        Hello World
  //        line no 60
  //        line no 61
  //          ....
  //        line no 99
  //

  @Override
  public void apply(UpdateLines updateLines) {
    int start = updateLines.getStartingLineNo();
    int end = start + updateLines.getNumberOfLines();
    this.fileData.subList(start, end).clear();
    this.fileData.addAll(start, updateLines.getLines());
  }

  @Override
  public List<String> getAllLines() {
    return this.fileData;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting before the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position use the value from pageRequest
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 25, line number 26 ... , line number 48, line number49)
  @Override
  public Page getLinesBefore(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    int num = numberOfLines;
    if (lineNumber - numberOfLines <= 0) {
      num = 0;
    }
    Page before = new Page(fileData.subList(num, lineNumber), num, pageRequest.getFileName(),
        pageRequest.getCursorAt());
    return before;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting after the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position use the value from pageRequest
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file  @Override
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 51, line number 52 ... , line number 74, line number75)

  @Override
  public Page getLinesAfter(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo() + 1;
    int numberOfLines = pageRequest.getNumberOfLines();
    int num = numberOfLines + lineNumber;
    if (lineNumber + numberOfLines > fileData.size()) {
      num = fileData.size();
    }
    if (lineNumber > fileData.size()) {
      lineNumber--;
    }
    Page after = new Page(fileData.subList(lineNumber, num), lineNumber, pageRequest.getFileName(),
        pageRequest.getCursorAt());
    return after;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting from the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position should be (startingLineNo, 0)
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file  @Override
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 50, line number 51 ... , line number 73, line number74)

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    if (lineNumber>this.fileData.size()){
      return new Page(Collections.<String>emptyList(), lineNumber, pageRequest.getFileName(),
      new Cursor(lineNumber, 0));
    }
    int numberOfLines = pageRequest.getNumberOfLines();
    int num = Math.min(getAllLines().size(), lineNumber + numberOfLines);
    Page from = new Page(fileData.subList(lineNumber, num), lineNumber, pageRequest.getFileName(),
        new Cursor(lineNumber, 0));
    return from;
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  //    SearchRequest - contains following information
  //        1. pattern - pattern you want to search
  //        2. File name - file where you want to search for the pattern
  // Description:
  //    1. Find all occurrences of the pattern in the SourceFile
  //    2. Create an empty list of cursors
  //    3. For each occurrence starting position add to the list of cursors
  //    4. return the list of cursors
  // Recommendation:
  //    1. Use FASTER string search algorithm.
  //    2. Feel free to try any other algorithm/data structure to improve search speed.
  // Reference:
  //     https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/

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