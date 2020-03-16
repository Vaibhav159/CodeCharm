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


  public SourceFileHandlerArrayListImpl(String fileName) {
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
  public void setCopyBuffer(CopyBuffer copyBuffer)
  {
  }


  @Override
  public CopyBuffer getCopyBuffer() {
  }


  @Override
  public SourceFileVersion cloneObj(SourceFileVersion ver) {
  }



  @Override
  public void editLines(EditRequest editRequest) {
  }



  @Override
  public void searchReplace(SearchReplaceRequest searchReplaceRequest) {
  }

  // TODO: CRIO_TASK_MODULE_UNDO_REDO
  // Input:
  //      UndoRequest
  //        1. fileName
  // Description:
  //      1. For the given file go back by one edit.
  //      2. If the file is already at its oldest change do nothing

  @Override
  public void undo(UndoRequest undoRequest) {
  }

  // TODO: CRIO_TASK_MODULE_UNDO_REDO
  // Input:
  //      UndoRequest
  //        1. fileName
  // Description:
  //      1. Re apply the last undone change. Basically reverse the last last undo.
  //      2. If there was no undo done earlier do nothing.

  @Override
  public void redo(UndoRequest undoRequest) {
  }

  // TODO: CRIO_TASK_MODULE_UNDO_REDO
  // Input:
  //      None
  // Description:
  //      Return the page that was in view as of this edit.

  public Page getCursorPage() {
  }

}
