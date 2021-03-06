/*
GNU Lesser General Public License

CustomAction
Copyright (C) 2000 Howard Kistler

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.hexidec.ekit.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;

import com.hexidec.ekit.Mutator;
import com.hexidec.ekit.component.SimpleInfoDialog;
import com.hexidec.util.Translatrix;

/** Class for implementing custom HTML insertion actions */
public class CustomAction extends StyledEditorKit.StyledTextAction {
  private static final long serialVersionUID = 7752928122828347312L;
  protected MyEkitCore parentEkit;
  private HTML.Tag htmlTag;
  private Hashtable<?, ?> htmlAttribs;
  private Mutator mutator;

  public CustomAction(MyEkitCore ekit, String actionName, HTML.Tag inTag,
    Hashtable<?, ?> attribs) {
    super(actionName);
    parentEkit = ekit;
    htmlTag = inTag;
    htmlAttribs = attribs;
  }

  public CustomAction(MyEkitCore ekit, String actionName, HTML.Tag inTag) {
    this(ekit, actionName, inTag, new Hashtable<Object, Object>());
  }

  public void actionPerformed(ActionEvent ae) {
    mutator = parentEkit.getMutator();
    if (!this.isEnabled()) 
      return;
      
    MyJTextPane parentTextPane = parentEkit.getTextPane();
    String selText = parentTextPane.getSelectedText();
    
    if (selText == null || selText.length() < 1) {
      parentEkit.showInfoDialog(Translatrix.getTranslationString("Error"),
        true, Translatrix.getTranslationString("ErrorNoTextSelected"),
        SimpleInfoDialog.ERROR);
      
      return;
    } 
    
    something(ae, parentTextPane, selText);
  }

  private void something(ActionEvent ae, MyJTextPane parentTextPane,
    String selText) {
    int caretOffset = parentTextPane.getSelectionStart();
    int internalTextLength = selText.length();
    // Somewhat ham-fisted code to obtain the first HREF in the selected
    // text,
    // which (if found) is passed to the URL HREF request dialog.
    String currentAnchor = "";
    if (htmlTag.toString().equals(HTML.Tag.A.toString())) {
      currentAnchor = findCurrentAnchor(parentTextPane, caretOffset,
        internalTextLength);
    }

    Hashtable<String, String> htmlAttribs2 = new Hashtable<String, String>();

    parentTextPane.select(caretOffset, caretOffset + internalTextLength);
    if (htmlTag.toString().equals(HTML.Tag.A.toString())) {
      boolean shouldExit = something6(currentAnchor, htmlAttribs2);      
      if(shouldExit)
        return;
      

    } else if (htmlTag.toString().equals(HTML.Tag.FONT.toString())) {
      something2(ae);
    }
    
    if (htmlAttribs2.size() > 0) {
      something3(parentTextPane, htmlAttribs2);
    }
    
    parentTextPane.select(caretOffset, caretOffset + internalTextLength);
    parentTextPane.requestFocus();
  }

  private String findCurrentAnchor(MyJTextPane parentTextPane, int caretOffset,
    int internalTextLength) {
    for (int i = caretOffset; i < caretOffset + internalTextLength; ++i) {
      parentTextPane.select(i, i + 1);
      SimpleAttributeSet sasText = parentTextPane.newSimpleAttributeSet();
      Enumeration<?> attribEntries1 = sasText.getAttributeNames();
      while (attribEntries1.hasMoreElements()) {
        String result = findCurrentAnchor(sasText, attribEntries1);
        if(!result.isEmpty())
          return result;
      }
    }
    return "";
  }

  private boolean something6(String currentAnchor,
    Hashtable<String, String> htmlAttribs2) {
    boolean shouldExit = false;
    if (!htmlAttribs.containsKey("href")) {
      MyUserInputAnchorDialog uidInput = parentEkit
        .newUserInputAnchorDialog(parentEkit,
          Translatrix.getTranslationString("AnchorDialogTitle"), true,
          currentAnchor);
      String newAnchor = uidInput.getInputText();
      uidInput.dispose();
      if (newAnchor != null) {
        htmlAttribs2.put("href", newAnchor);
      } else {
        parentEkit.repaint();
        shouldExit = true;
      }
    }
    return shouldExit;
  }

  private String findCurrentAnchor(SimpleAttributeSet sasText, Enumeration<?> attribEntries1) {
    String currentAnchor = "";
    Object entryKey = attribEntries1.nextElement();
    Object entryValue = sasText.getAttribute(entryKey);
    if (entryKey.toString().equals(HTML.Tag.A.toString())) {
      if (entryValue instanceof SimpleAttributeSet) {
        Enumeration<?> subAttributes = ((SimpleAttributeSet) entryValue)
          .getAttributeNames();
        while (subAttributes.hasMoreElements()
          && currentAnchor.equals("")) {
          Object subKey = subAttributes.nextElement();
          if (subKey.toString().toLowerCase().equals("href")) {
            currentAnchor = ((SimpleAttributeSet) entryValue)
              .getAttribute(subKey).toString();
            break;
          }
        }
      }
    }
    return currentAnchor;
  }

  private void something3(MyJTextPane parentTextPane,
    Hashtable<String, String> htmlAttribs2) {
    SimpleAttributeSet sasTag = new SimpleAttributeSet();
    SimpleAttributeSet sasAttr = new SimpleAttributeSet();

    Enumeration<String> attribEntries = htmlAttribs2.keys();
    while (attribEntries.hasMoreElements()) {
      Object entryKey = attribEntries.nextElement();
      Object entryValue = htmlAttribs2.get(entryKey);
      insertAttribute(sasAttr, entryKey, entryValue);
    }
    SimpleAttributeSet baseAttrs = parentEkit.getTextPane()
      .newSimpleAttributeSet();
    mutator.mutate(baseAttrs);

    Enumeration<?> attribEntriesOriginal = baseAttrs.getAttributeNames();
    while (attribEntriesOriginal.hasMoreElements()) {
      Object entryKey = attribEntriesOriginal.nextElement();
      Object entryValue = baseAttrs.getAttribute(entryKey);
      insertAttribute(sasAttr, entryKey, entryValue);
    }
    sasTag.addAttribute(htmlTag, sasAttr);
    parentTextPane.setCharacterAttributes(sasTag, false);
    parentEkit.refreshOnUpdate();
  }

  private void something2(ActionEvent ae) {
    if (htmlAttribs.containsKey("color")) {
      Color color = parentEkit.chooseColor(
        Translatrix.getTranslationString("CustomColorDialog"),
        Color.black);
      if (color != null) {
        parentEkit.setForegroundColor(color, ae);
      }
    }
  }

  private void insertAttribute(SimpleAttributeSet attrs, Object key,
    Object value) {
    if (value instanceof AttributeSet) {
      AttributeSet subSet = (AttributeSet) value;
      Enumeration<?> attribEntriesSub = subSet.getAttributeNames();
      while (attribEntriesSub.hasMoreElements()) {
        Object subKey = attribEntriesSub.nextElement();
        Object subValue = subSet.getAttribute(subKey);
        insertAttr(attrs, subKey, subValue);
      }
    } else {
      insertAttr(attrs, key, value);
    }
    // map CSS font-family declarations to FONT tag face declarations
    if (key.toString().toLowerCase().equals("font-family")) {
      if (attrs.isDefined("face")) {
        insertAttr(attrs, "face", attrs.getAttribute("face"));
        insertAttr(attrs, "font-family", attrs.getAttribute("face"));
      } else {
        insertAttr(attrs, "face", value);
      }
    }
    // map CSS font-size declarations to FONT tag size declarations
    /*
     * if(key.toString().toLowerCase().equals("font-size")) {
     * if(attrs.isDefined("size")) { insertAttr(attrs, "size",
     * attrs.getAttribute("size")); insertAttr(attrs, "font-size",
     * attrs.getAttribute("size")); } else { insertAttr(attrs, "size", value); }
     * }
     */
  }

  private void insertAttr(SimpleAttributeSet attrs, Object key, Object value) {
    while (attrs.isDefined(key)) {
      attrs.removeAttribute(key);
      break;
    }
    attrs.addAttribute(key, value);
  }
}
