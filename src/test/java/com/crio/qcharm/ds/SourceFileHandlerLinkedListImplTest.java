package com.crio.qcharm.ds;

import com.crio.qcharm.log.UncaughtExceptionHandler;
import com.crio.qcharm.request.EditRequest;
import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchReplaceRequest;
import com.crio.qcharm.request.SearchRequest;
import com.crio.qcharm.request.UndoRequest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceFileHandlerLinkedListImplTest {

  @BeforeEach
  public void setupUncaughtExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
  }

  static FileInfo inefficientSearch;
  static List<Cursor> expectedCursorPositions = new ArrayList<>();
  static String pattern;

  private SourceFileHandlerLinkedListImpl getSourceFileHandler(String fileName) {
    return new SourceFileHandlerLinkedListImpl(fileName);
  }
  @BeforeAll
  static void setup() {

    StringBuffer prefix = new StringBuffer("");

    for (int i = 0; i < 30; ++i) {
      prefix.append("ab");
    }

    pattern = prefix.toString() + "aa";
    patternGenerator(prefix.toString() + "ab", pattern);
  }

  @AfterAll
  static void teardown() {
    inefficientSearch.getLines().clear();
  }

  static void patternGenerator(String pattern1, String pattern2) {
    StringBuffer buffer1 = new StringBuffer("");
    StringBuffer buffer2 = new StringBuffer("");

    int K = 250;
    for (int i = 0; i < K; ++i) {
      buffer1.append(pattern1);
    }
    buffer1.append(pattern2);

    for (int  i = 0; i < K; ++i) {
      buffer1.append(pattern1);
    }

    List<String> lines = new ArrayList<>();

    int N = 1000;

    String s1 = buffer1.toString();
    for (int i = 0; i < N; ++i) {
      lines.add(s1 + new Integer(i).toString() );
    }

    int len1 = pattern1.length();
    for (int i = 0; i < N; ++i) {
      expectedCursorPositions.add(new Cursor(i, K * len1));
    }

    inefficientSearch =  new FileInfo("testfile", lines);
  }

  FileInfo getLargeSampleFileInfo(String fileName, int n) {
    List<String> testLines = new ArrayList<>();

    for (int i = 0; i < n; ++i) {
      StringBuffer buffer = new StringBuffer("lineno");
      buffer.append(i);
      testLines.add(buffer.toString());
    }
    return new FileInfo(fileName, testLines);
  }
  
  @Test
  void largeFileLoadingReturnsFiftyLinesOfDataLinkedList() {
    SourceFileHandler sourceFileHandler = getSourceFileHandler("testfile");

    FileInfo fileInfo = getLargeSampleFileInfo("largeFile", 1000000);
    Page page = sourceFileHandler.loadFile(fileInfo);

    assertEquals(page.getLines(), fileInfo.getLines().subList(0, 50));
  }

  @Test
  void getNextLinesReturnsEmptyPageIfThereIsNoLinesAfter() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    int startingLineNo = 100;
    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(startingLineNo, fileName, length, cursor);
    Page emptyPage = sourceFileHandler.getNextLines(pageRequest);

    assertEquals(new LinkedList<String>(), emptyPage.getLines());
    assertEquals(100, emptyPage.getStartingLineNo());
    assertEquals(cursor, emptyPage.getCursorAt());
  }

  @Test
  void getNextLinesReturnsLessThanRequestedNumberOfLines() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    int startingLine = 90;

    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(startingLine, fileName, length, cursor);
    Page page = sourceFileHandler.getNextLines(pageRequest);

    assertEquals(fileInfo.getLines().subList(startingLine+1, 100), page.getLines());
    assertEquals(startingLine+1, page.getStartingLineNo());
    assertEquals(cursor, page.getCursorAt());
  }

  @Test
  void getNextLinesReturnsRequestedNumberOfLines() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    int startingLine = 35;

    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(startingLine, fileName, length, cursor);
    Page page = sourceFileHandler.getPrevLines(pageRequest);

    assertEquals(fileInfo.getLines().subList(0, length), page.getLines());
    assertEquals(0, page.getStartingLineNo());
    assertEquals(cursor, page.getCursorAt());
  }

  @Test
  void getPrevLinesReturnsEmptyPageIfThereIsNoLinesBefore() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(0, fileName, length, cursor);
    Page emptyPage = sourceFileHandler.getPrevLines(pageRequest);

    assertEquals(new LinkedList<String>(), emptyPage.getLines());
    assertEquals(0, emptyPage.getStartingLineNo());
    assertEquals(cursor, emptyPage.getCursorAt());
  }

  @Test
  void getPrevLinesReturnsLessThanRequestedNumberOfLines() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    int startingLine = 10;

    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(startingLine, fileName, length, cursor);
    Page page = sourceFileHandler.getPrevLines(pageRequest);

    assertEquals(fileInfo.getLines().subList(0, 10), page.getLines());
    assertEquals(0, page.getStartingLineNo());
    assertEquals(cursor, page.getCursorAt());
  }

  @Test
  void getPrevLinesReturnsRequestedNumberOfLinesLinked() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    int startingLine = 35;

    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(startingLine, fileName, length, cursor);
    Page page = sourceFileHandler.getPrevLines(pageRequest);

    assertEquals(fileInfo.getLines().subList(0, 35), page.getLines());
    assertEquals(0, page.getStartingLineNo());
    assertEquals(cursor, page.getCursorAt());
  }

  @Test
  void getLinesFromReturnsRequestedNumberOfLines() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    FileInfo fileInfo = getLargeSampleFileInfo(fileName, 100);
    sourceFileHandler.loadFile(fileInfo);

    int length = 35;
    int startingLine = 0;

    Cursor cursor = new Cursor(0, 0);
    PageRequest pageRequest = new PageRequest(startingLine, fileName, length, cursor);
    Page page = sourceFileHandler.getLinesFrom(pageRequest);

    assertEquals(fileInfo.getLines().subList(startingLine, length), page.getLines());
    assertEquals(0, page.getStartingLineNo());
    assertEquals(cursor, page.getCursorAt());
  }

  @Test
  void efficientSearchTest() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    sourceFileHandler.loadFile(inefficientSearch);
    SearchRequest searchRequest = new SearchRequest(0, pattern, fileName);
    long timeInNs = 0;
    for (int i = 0; i < 10; ++i) {
      long startTime = System.nanoTime();
      List<Cursor> cursors = sourceFileHandler.search(searchRequest);
      timeInNs += System.nanoTime() - startTime;
      assertEquals(expectedCursorPositions, cursors);
    }
    System.out.println(timeInNs);
    assert (timeInNs < 1700 * 1000 * 1000);
  }

  @Test
  void search() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    int N = 100;
    FileInfo fileInfo = getLargeSampleFileInfo(fileName, N);
    sourceFileHandler.loadFile(fileInfo);

    SearchRequest searchRequest = new SearchRequest(0, "lineno", fileName);

    List<Cursor> cursors = sourceFileHandler.search(searchRequest);
    List<Cursor> expected = new ArrayList<>();

    for (int i = 0; i < N; ++i) {
      expected.add(new Cursor(i, 0));
    }

    assertEquals(expected, cursors);
  }


  @Test
  void editLines() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    int N = 100;
    FileInfo fileInfo = getLargeSampleFileInfo(fileName, N);
    sourceFileHandler.loadFile(fileInfo);

    List<String> changedLines = new ArrayList<>();
    for (int i = 0; i < 35; ++i) {
      StringBuffer buffer = new StringBuffer("LineNumber");
      buffer.append(i);
      changedLines.add(buffer.toString());
    }
    
    EditRequest editRequest = new EditRequest(35, 70, changedLines, fileName, new Cursor(0,0));
    sourceFileHandler.editLines(editRequest);

    PageRequest pageRequest = new PageRequest(0, fileName, N, new Cursor(0,0));
    Page page = sourceFileHandler.getLinesFrom(pageRequest);

    assertEquals(fileInfo.getLines().subList(0, 35), page.getLines().subList(0,35));
    assertEquals(changedLines, page.getLines().subList(35, 70));
    assertEquals(fileInfo.getLines().subList(70, N), page.getLines().subList(70,N));
  }



  @Test
  void searchReplace() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    int N = 100;
    FileInfo fileInfo = getLargeSampleFileInfo(fileName, N);
    sourceFileHandler.loadFile(fileInfo);

    SearchReplaceRequest searchReplaceRequest = new SearchReplaceRequest(0, 0, "lineno",
        "LineNumber", fileName);

    sourceFileHandler.searchReplace(searchReplaceRequest);

    List<String> expected = new ArrayList<>();
    for (int i = 0; i < N; ++i) {
      StringBuffer buffer = new StringBuffer("LineNumber");
      buffer.append(i);
      expected.add(buffer.toString());
    }

    PageRequest pageRequest = new PageRequest(0, fileName, 100, new Cursor(0,0));
    Page page = sourceFileHandler.getLinesFrom(pageRequest);

    assertEquals(expected, page.getLines());
  }



  @Test
  void undoSearchReplace() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    int N = 100;
    FileInfo fileInfo = getLargeSampleFileInfo(fileName, N);
    sourceFileHandler.loadFile(fileInfo);

    SearchReplaceRequest searchReplaceRequest = new SearchReplaceRequest(0, 0, "lineno",
        "LINENO", fileName);

    sourceFileHandler.searchReplace(searchReplaceRequest);

    List<String> expected = new ArrayList<>();
    for (int i = 0; i < N; ++i) {
      StringBuffer buffer = new StringBuffer("LINENO");
      buffer.append(i);
      expected.add(buffer.toString());
    }

    PageRequest pageRequest = new PageRequest(0, fileName, 100, new Cursor(0,0));
    Page page = sourceFileHandler.getLinesFrom(pageRequest);

    assertEquals(expected, page.getLines());

    UndoRequest undoRequest = new UndoRequest(fileName);

    sourceFileHandler.undo(undoRequest);

    Page pageAfterUndo = sourceFileHandler.getLinesFrom(pageRequest);
    assertEquals(pageAfterUndo.getLines(), fileInfo.getLines());
  }

  @Test
  void undoEditLines() {
    String fileName = "testfile";
    SourceFileHandler sourceFileHandler = getSourceFileHandler(fileName);

    int N = 100;
    FileInfo fileInfo = getLargeSampleFileInfo(fileName, N);
    sourceFileHandler.loadFile(fileInfo);

    List<String> changedLines = new ArrayList<>();
    for (int i = 0; i < 35; ++i) {
      StringBuffer buffer = new StringBuffer("LineNumber");
      buffer.append(i);
      changedLines.add(buffer.toString());
    }

    EditRequest editRequest = new EditRequest(35, 70, changedLines, fileName, new Cursor(0,0));
    sourceFileHandler.editLines(editRequest);

    PageRequest pageRequest = new PageRequest(0, fileName, N, new Cursor(0,0));
    Page page = sourceFileHandler.getLinesFrom(pageRequest);

    assertEquals(fileInfo.getLines().subList(0, 35), page.getLines().subList(0,35));
    assertEquals(changedLines, page.getLines().subList(35, 70));
    assertEquals(fileInfo.getLines().subList(70, N), page.getLines().subList(70,N));

    UndoRequest undoRequest = new UndoRequest(fileName);
    sourceFileHandler.undo(undoRequest);
    Page pageAfterUndo = sourceFileHandler.getLinesFrom(pageRequest);

    assertEquals(fileInfo.getLines(), pageAfterUndo.getLines());
  }


}

