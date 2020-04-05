package com.crio.qcharm.ds;

import com.crio.qcharm.request.EditRequest;
import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchReplaceRequest;
import com.crio.qcharm.request.SearchRequest;
import com.crio.qcharm.request.UndoRequest;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SourceFileHandlerArrayListImpl implements SourceFileHandler {
  private SourceFileVersionArrayListImpl obj;
  private SourceFileVersion sfh = new SourceFileVersionArrayListImpl();
  private CopyBuffer CpyBuf;

  public SourceFileHandlerArrayListImpl(String fileName) {

  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // FileName
  // Steps:
  // 1. Given SourceFile name get the latest version of the it.
  // Description:
  // After loading the file the SourceFile would have gone through multiple
  // changes. When we say "Latest version of the SourceFile" it means the
  // SourceFile's present
  // view after applying all the changes.

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // FileInfo
  // Steps:
  // 1. Create an object of the SourceFileVersionArrayListImpl class using the
  // given fileInfo.
  // 2. Using that object get the first 50 lines of this file.
  // 3. Create Page object using the lines received and return the same.
  // How to construct Page object ?
  // 1. lines should be the first 50 lines of the SourceFile
  // 2. cursorAt should be new Cursor(0,0)
  // 3. StartingLineNo is set to 0
  // 4. fileName should be same as the fileInfo.fileName
  //
  // What is Cursor?
  // It represents position of the cursor in the editor.
  // Cursor is represented using (lineNumber, columnNumber).

  @Override
  public Page loadFile(FileInfo fileInfo) {
    SourceFileVersionArrayListImpl startFifty = new SourceFileVersionArrayListImpl(fileInfo);
    this.obj = startFifty;
    int min = Math.min(50, fileInfo.getLines().size());
    Page one = new Page(startFifty.getAllLines().subList(0, min), 0, fileInfo.getFileName(), new Cursor(0, 0));
    return one;
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // PageRequest - contains following information
  // 1. Starting line number
  // 2. File name;
  // 3. requested number of Lines
  // 4. Cursor position
  // Steps:
  // 1. After loadFile SourceFileVersionArrayListImpl has all the file information
  // 2. Using that get "requested number of lines" above "the given line number".
  // 3. Construct Page object and return
  // How to construct Page object ?
  // 1. lines - lines you got in step 2
  // 2. cursorAt should be same as pageRequest.cursorAt
  // 3. StartingLineNo should be same as first line number of lines
  // 4. fileName should be same as the pageRequest.fileName

  @Override
  public Page getPrevLines(PageRequest pageRequest) {
    return this.obj.getLinesBefore(pageRequest);
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // PageRequest - contains following information
  // 1. Starting line number
  // 2. File name;
  // 3. requested number of Lines
  // 4. Cursor position
  // Steps:
  // 1. After loadFile SourceFileVersionArrayListImpl has all the file information
  // 2. Using that get "requested number of lines" below "the given line number".
  // 3. Construct Page object and return
  // How to construct Page object ?
  // 1. lines - lines you got in step 2
  // 2. cursorAt should be same as pageRequest.cursorAt
  // 3. StartingLineNo should be same as first line number of lines
  // 4. fileName should be same as the pageRequest.fileName

  @Override
  public Page getNextLines(PageRequest pageRequest) {
    return this.obj.getLinesAfter(pageRequest);
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  // PageRequest - contains following information
  // 1. Starting line number
  // 2. File name;
  // 3. requested number of Lines
  // 4. Cursor position
  // Steps:
  // 1. After loadFile SourceFileVersionArrayListImpl has all the file information
  // 2. Using the object get "requested number of lines" starting from "the given
  // line number".
  // 3. Construct Page object and return
  // How to construct Page object ?
  // 1. lines - lines you got in step 2
  // 2. cursorAt should be same be set to (startingLineNo, 0);
  // 3. StartingLineNo should be same as first line number of lines
  // 4. fileName should be same as the pageRequest.fileName

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
    return this.obj.getLinesFrom(pageRequest);
  }

  @Override
  public SourceFileVersion getLatestSourceFileVersion(String fileName) {
    return obj;
  }

  // TODO: CRIO_TASK_MODULE_SEARCH
  // Input:
  // SearchRequest - contains following information
  // 1. pattern - pattern you want to search
  // 2. File name - file where you want to search for the pattern
  // Description:
  // 1. For the given SourceFile use SourceFileVersionArrayListImpl
  // .getCursors() to find all occurrences of the pattern in the SourceFile.
  // 2. return the all occurrences starting position in a list.

  @Override
  public List<Cursor> search(SearchRequest searchRequest) {
    return this.obj.getCursors(searchRequest);
  }

  // TODO: CRIO_TASK_MODULE_CUT_COPY_PASTE
  // Input:
  // CopyBuffer - contains following information
  // 1. List of lines
  // Description:
  // Store the incoming copy buffer

  @Override
  public void setCopyBuffer(CopyBuffer copyBuffer) {
    this.CpyBuf = copyBuffer;
  }

  // TODO: CRIO_TASK_MODULE_CUT_COPY_PASTE
  // Input:
  // None
  // Description:
  // return the previously stored copy buffer
  // if nothing is stored return copy buffer containing empty lines.

  @Override
  public CopyBuffer getCopyBuffer() {
    return this.CpyBuf;
  }

  // TODO: CRIO_TASK_MODULE_CUT_COPY_PASTE
  // Input:
  // Object of type SourceFileVersionArrayListImpl
  // Description:
  // make a copy of the the given SourceFileVersionArrayListImpl object return new
  // object
  // NOTE:
  // DON'T CHANGE THE SIGNATURE OF THIS FUNCTION

  @Override
  public SourceFileVersion cloneObj(SourceFileVersion ver) {
    this.sfh = ver;
    return sfh;
  }

  // TODO: CRIO_TASK_MODULE_CUT_COPY_PASTE
  // Input:
  // EditRequest
  // 1. starting line no - starting line number of last time it received page from
  // backend
  // 2. ending line no - ending line no of the last time it received page from
  // backend;
  // 3. new content - list of lines present view of lines(starting line no, ending
  // line no)
  // 4. file name
  // 5. cursor
  // Description:
  // 1. Remove the line numbers in the range(starting line no, ending line no)
  // 2. Inserting the lines in new content starting position starting line no
  // Example:
  // EditRequest looks like this
  // 1. start line no - 50
  // 2. ending line no - 60
  // 3. new content - ["Hello world"]
  //
  // Assume the file has 100 lines in it
  //
  // File contents before edit:
  // ==========================
  // line no 1
  // line no 2
  // .....
  // line no 100
  //
  // File contents After Edit:
  // =========================
  // line no 1
  // line no 2
  // line no 3
  // .....
  // line no 49
  // Hello World
  // line no 61
  // line no 62
  // ....
  // line no 100
  //

  @Override
  public void editLines(EditRequest editRequest) {
    UpdateLines u = new UpdateLines(editRequest.getStartingLineNo(),
        editRequest.getEndingLineNo() - editRequest.getStartingLineNo(), editRequest.getNewContent(),
        editRequest.getCursorAt());
    List<Edits> e = new ArrayList<>();
    e.add(u);
    this.obj.apply(e);
  }

}
