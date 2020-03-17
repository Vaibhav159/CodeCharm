package com.crio.qcharm.ds;

import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchRequest;
import java.util.List;

public interface SourceFileVersion {


  List<String> getAllLines();

  Page getLinesBefore(PageRequest pageRequest);

  Page getLinesAfter(PageRequest pageRequest);

  Page getLinesFrom(PageRequest pageRequest);

  //TODO:
  // You are given searchPattern return.
  // Return starting cursorAt position for all string occurance


  //TODO:
  // You are given searchPattern and replacePattern.
  // Replace every occurance for searchPattern by replacePattern


}