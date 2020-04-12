package com.crio.qcharm.ds;

import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class SourceFileVersionLinkedListImpl implements SourceFileVersion {
  private LinkedList<String> fileData = new LinkedList<String>();
  private String filename;

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  // FileInfo - contains following information
  // 1. fileName
  // 2. List of lines
  // Steps:
  // You task here is to construct SourceFileVersionLinkedListImpl object by
  // 1. Storing the lines received from fileInfo object
  // 2. Storing the fileName received from fileInfo object.
  // Recommendations:
  // 1. Use Java LinkedList to store the lines received from fileInfo

  SourceFileVersionLinkedListImpl(FileInfo fileInfo) {
    //this.fileData = fileInfo.getLines().stream().collect(Collectors.toCollection(LinkedList::new));
    this.filename = fileInfo.getFileName();
    List<String> temp = fileInfo.getLines();
    for (String p : temp) {
      this.fileData.add(p);
    }
  }

  public SourceFileVersionLinkedListImpl() {
  }

  public SourceFileVersionLinkedListImpl(SourceFileVersionLinkedListImpl obj) {
    this.filename = obj.filename;
    this.fileData = obj.getAllLines().stream().collect(Collectors.toCollection(LinkedList::new));
  }

  @Override
  public SourceFileVersion apply(List<Edits> edits) {
    List<String> lines = new LinkedList<>();
    lines.addAll(lines);

    //SourceFileVersionLinkedListImpl latest = new SourceFileVersionLinkedListImpl();

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

  // TODO: CRIO_TASK_MODULE_SEARCH_REPLACE
  // Input:
  // SearchReplace
  // 1. pattern - pattern to be found
  // 2. newPattern - pattern to be replaced with
  // Description:
  // Find every occurrence of the pattern and replace it newPattern.

  @Override
  public void apply(SearchReplace searchReplace) {
    String pattern = searchReplace.getPattern();
    String new_pattern = searchReplace.getNewPattern();
    List<Cursor> cursors = searchPattern(searchReplace.getPattern().toCharArray(), this.fileData);
    TreeSet<Cursor> ts = new TreeSet<>(Comparator.comparing(Cursor::getLineNo));
    ts.addAll(cursors);
    for (Cursor c : ts) {
      String temp = this.fileData.get(c.getLineNo());
      temp = StringUtils.replace(temp, pattern, new_pattern);
      this.fileData.set(c.getLineNo(), temp);
    }
    /*int num = -1;

    for (Cursor cursor : cursors) {
      if (num == cursor.getLineNo()) {
        continue;
      }
      num = cursor.getLineNo();
      this.fileData.set(num, StringUtils.replace(this.fileData.get(num), pattern, new_pattern));
    }
    for(String s : this.fileData) {
      s=StringUtils.replaceAll(s, pattern, new_pattern);
    }*/
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
  // Input:
  // UpdateLines
  // 1. startingLineNo - starting line number of last time it received page from
  // backend
  // 2. numberOfLines - number of lines received from backend last time.
  // 3. lines - present view of lines in
  // range(startingLineNo,startingLineNo+numberOfLines)
  // 4. cursor
  // Description:
  // 1. Remove the line numbers in the range(starting line no, ending line no)
  // 2. Inserting the lines in new content starting position starting line no
  // Example:
  // UpdateLines looks like this
  // 1. start line no - 50
  // 2. numberOfLines - 10
  // 3. lines - ["Hello world"]
  //
  // Assume the file has 100 lines in it
  //
  // File contents before edit:
  // ==========================
  // line no 0
  // line no 1
  // line no 2
  // .....
  // line no 99
  //
  // File contents After Edit:
  // =========================
  // line no 0
  // line no 1
  // line no 2
  // line no 3
  // .....
  // line no 49
  // Hello World
  // line no 60
  // line no 61
  // ....
  // line no 99
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
    if (lineNumber == 0) {
      return new Page(new LinkedList<String>(), 0, pageRequest.getFileName(), pageRequest.getCursorAt());
    }else if (lineNumber - numberOfLines < 0) {
      return new Page(this.fileData.subList(0, lineNumber), 0, pageRequest.getFileName(), pageRequest.getCursorAt());
    }
    return new Page(fileData.subList(lineNumber - numberOfLines, lineNumber), lineNumber - numberOfLines,
        pageRequest.getFileName(), pageRequest.getCursorAt());
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
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
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    if (lineNumber >= this.fileData.size()) {
      return new Page(new LinkedList<String>(), lineNumber, pageRequest.getFileName(), pageRequest.getCursorAt());
    }else if (lineNumber + numberOfLines + 1 > fileData.size()) {
      return new Page(this.fileData.subList(lineNumber + 1, this.fileData.size()), lineNumber + 1,
          pageRequest.getFileName(), pageRequest.getCursorAt());
    }
    return new Page(fileData.subList(lineNumber + 1, Math.min(lineNumber + numberOfLines + 1, this.fileData.size())),
        lineNumber + 1, pageRequest.getFileName(), pageRequest.getCursorAt());
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
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
    return new Page(
        this.fileData.subList(lineNumber,
            this.fileData.size() < lineNumber + numberOfLines ? this.fileData.size() : lineNumber + numberOfLines),
        lineNumber, pageRequest.getFileName(), new Cursor(lineNumber, 0));
  }

  // TODO: CRIO_TASK_MODULE_IMPROVING_EDITS
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
/*
  @Override
  public List<Cursor> getCursors(SearchRequest searchRequest) {
    try {
      String pattern = searchRequest.getPattern();
      if (pattern.isEmpty()) {
        return new LinkedList<Cursor>();
      }
      List<Cursor> searchResults = new LinkedList<Cursor>();
      int sizeOfPattern = pattern.length();
      int longestProperSuffix[] = new int[sizeOfPattern];
      computeLongestProperSuffix(pattern, sizeOfPattern, longestProperSuffix);
      for (int i = 0; i < this.fileData.size(); i++) {
        String line = this.fileData.get(i);
        int indexOfPat = 0, indexOfStr = 0;
        while (indexOfStr < line.length()) {
          if (line.charAt(indexOfStr) == pattern.charAt(indexOfPat)) {
            indexOfStr++;
            indexOfPat++;
          }
          if (indexOfPat == sizeOfPattern) {
            Cursor cursor = new Cursor(i, indexOfStr - indexOfPat);
            searchResults.add(cursor);
            indexOfPat = longestProperSuffix[indexOfPat - 1];
          } else if (pattern.charAt(indexOfPat) != line.charAt(indexOfStr)) {
            if (indexOfPat != 0) {
              indexOfPat = longestProperSuffix[indexOfPat - 1];
            } else {
              indexOfStr++;
            }
          }
        }
      }
      return searchResults;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  void computeLongestProperSuffix(String pattern, int sizeOfPattern, int[] longestProperSuffix) {
    try {
      longestProperSuffix[0] = 0;
      int i = 1, length = 0;
      while (i < sizeOfPattern) {
        if (pattern.charAt(i) == pattern.charAt(length)) {
          length++;
          longestProperSuffix[i] = length;
          i++;
        } else {
          if (length > 0) {
            length = longestProperSuffix[length - 1];
          } else {
            longestProperSuffix[i] = length;
            i++;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
*/


  public List<Cursor> getCursors(SearchRequest searchRequest) {
    try {
      String pattern = searchRequest.getPattern();
      if (pattern.isEmpty()) {
        return new LinkedList<Cursor>();
      }
      List<Cursor> search = new LinkedList<>();
      search = searchPattern(pattern.toCharArray(), this.fileData);
      return search;
    } catch (Exception e) {
      return null;
    }
  }

  static void computeLPSArray(char[] pat, int M, int lps[]) {
    int len = 0;
    int i = 1;
    lps[0] = 0;

    while (i < M) {
      if (pat[i] == pat[len]) {
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

  static LinkedList<Cursor> searchPattern(char[] pat, List<String> s) {
    LinkedList<Cursor> ans = new LinkedList<>();
    int M = pat.length;
    int lps[] = new int[M];
    int count = 0;
    for (String txt : s) {
      int N = txt.length();
      char[] txtaray = txt.toCharArray();
      int j = 0;
      computeLPSArray(pat, M, lps);
      int i = 0;
      while (i < N) {
        if (pat[j] == txtaray[i]) {
          j++;
          i++;
        }
        if (j == M) {
          ans.add(new Cursor(count, i - j));
          j = lps[j - 1];
        } else if (i < N && pat[j] != txtaray[i]) {
          if (j != 0)
            j = lps[j - 1];
          else
            i = i + 1;
        }
      }
      count++;
    }
    return ans;
  }

  @Override
  public Page getCursorPage() {
    return null;
  }

}
