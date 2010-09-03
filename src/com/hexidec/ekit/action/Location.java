package com.hexidec.ekit.action;

import javax.swing.JTextPane;

public class Location {
   public final int offset;
   public final int length;
   
   public Location(int offset, String text) {
      this.offset = offset;
      this.length = text == null ? -1 : text.length();
   }
   
   public boolean isEmpty() {
      return length <= 0;
   }

   void select(JTextPane parentTextPane) {
      parentTextPane.select(offset, offset + length);
   }

   int end() {
      return offset + length;
   }
   
}   
