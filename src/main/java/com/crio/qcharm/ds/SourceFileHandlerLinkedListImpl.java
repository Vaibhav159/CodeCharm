package com.crio.qcharm.ds;

import com.crio.qcharm.request.EditRequest;
import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchReplaceRequest;
import com.crio.qcharm.request.SearchRequest;
import com.crio.qcharm.request.UndoRequest;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class SourceFileHandlerLinkedListImpl implements SourceFileHandler {


  public SourceFileHandlerLinkedListImpl(String fileName) {
  }




  @Override
  public SourceFileVersion getLatestSourceFileVersion(String fileName) {
    return latest;
  }

  @Override
  public Page loadFile(FileInfo fileInfo) {
  }

  @Override
  public Page getPrevLines(PageRequest pageRequest) {
  }

  @Override
  public Page getNextLines(PageRequest pageRequest) {
  }

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
  }

  @Override
  public List<Cursor> search(SearchRequest searchRequest) {
  }


  @Override
  public void setCopyBuffer(CopyBuffer copyBuffer) {
    this.copyBuffer = copyBuffer;
  }

  @Override
  public CopyBuffer getCopyBuffer() {
    CopyBuffer lastCopyBuffer = this.copyBuffer;
    this.copyBuffer = new CopyBuffer(new LinkedList<>());
    return lastCopyBuffer;
  }

  @Override
  public SourceFileVersion cloneObj(SourceFileVersion ver) {
    return new SourceFileVersionLinkedListImpl((SourceFileVersionLinkedListImpl) ver);
  }

  @Override
  public void editLines(EditRequest editRequest) {
  }

  @Override
  public void searchReplace(SearchReplaceRequest searchReplaceRequest) {
  }

  @Override
  public void undo(UndoRequest undoRequest) {
  }

  @Override
  public void redo(UndoRequest undoRequest) {
  }

  public Page getCursorPage() {
  }

  public int getDefaultNumberOfLinesPerPage() {
    return this.DefaultNumberOfLinesPerPage;
  }

  public SourceFileVersionLinkedListImpl getFirst() {
    return this.first;
  }

  public SourceFileVersionLinkedListImpl getLatest() {
    return this.latest;
  }

  public String getFileName() {
    return this.fileName;
  }

  public Deque<Edits> getQueue() {
    return this.queue;
  }

  public Stack<Edits> getRedoStack() {
    return this.redoStack;
  }

  public void setFirst(SourceFileVersionLinkedListImpl first) {
    this.first = first;
  }

  public void setLatest(SourceFileVersionLinkedListImpl latest) {
    this.latest = latest;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setQueue(Deque<Edits> queue) {
    this.queue = queue;
  }

  public void setRedoStack(Stack<Edits> redoStack) {
    this.redoStack = redoStack;
  }

}
