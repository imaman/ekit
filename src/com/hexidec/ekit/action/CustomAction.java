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

import javax.swing.JColorChooser;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;

import com.hexidec.ekit.EkitCore;
import com.hexidec.ekit.Mutator;
import com.hexidec.ekit.component.SimpleInfoDialog;
import com.hexidec.ekit.component.UserInputAnchorDialog;
import com.hexidec.util.Translatrix;

/** Class for implementing custom HTML insertion actions
*/
public class CustomAction extends StyledEditorKit.StyledTextAction
{
	protected EkitCore parentEkit;
	HTML.Tag htmlTag;
	private   Hashtable htmlAttribs;
   private Mutator mutator;

	public CustomAction(EkitCore ekit, String actionName, HTML.Tag inTag, Hashtable attribs)
	{
		super(actionName);
		parentEkit  = ekit;
		htmlTag     = inTag;
		htmlAttribs = attribs;
	}

	public CustomAction(EkitCore ekit, String actionName, HTML.Tag inTag)
	{
		this(ekit, actionName, inTag, new Hashtable());
	}

	public void actionPerformed(ActionEvent ae)
	{
      mutator = parentEkit.getMutator();
		if(!this.isEnabled())
		   return;
		
		JTextPane textPane = parentEkit.getTextPane();
		
		Location location = new Location(textPane.getSelectionStart(), 
         textPane.getSelectedText());
      
		if(location.isEmpty())
		{
			new SimpleInfoDialog(parentEkit.getFrame(), 
			   Translatrix.getTranslationString("Error"), true, 
			   Translatrix.getTranslationString("ErrorNoTextSelected"), 
			   SimpleInfoDialog.ERROR);
			return;
		}
				
		String currentAnchor = "";
      if(htmlTag.toString().equals(Tag.A.toString()))
         currentAnchor = new AnchorFinder().getCurrentAnchor(textPane, location);
      
      location.select(textPane);      
		String newAnchor = getNewAnchor(ae, currentAnchor);

      if(newAnchor != null) 
         applyAttributes(textPane, generateHrefAttribute(newAnchor));
		
      location.select(textPane);
      textPane.requestFocus();		
	}

   private String getNewAnchor(ActionEvent ae, String currentAnchor) 
   {
      if(htmlTag.toString().equals(HTML.Tag.FONT.toString()))
      {
         processColor(ae);
         return null;
      }
      
      if(!htmlTag.toString().equals(HTML.Tag.A.toString()))
         return null;
      
	   return getNewAnchor(currentAnchor);
   }

   private void applyAttributes(JTextPane parentTextPane,
      SimpleAttributeSet sasAttr) {
      SimpleAttributeSet sasTag  = new SimpleAttributeSet();
      sasTag.addAttribute(htmlTag, sasAttr);
      parentTextPane.setCharacterAttributes(sasTag, false);
      parentEkit.refreshOnUpdate();
   }

   private SimpleAttributeSet generateHrefAttribute(String newAnchor) {
      SimpleAttributeSet sasAttr = new SimpleAttributeSet();
      Object entryKey1   = "href";
      Object entryValue1 = newAnchor;
      insertAttribute(sasAttr, entryKey1, entryValue1);

      SimpleAttributeSet baseAttrs = new SimpleAttributeSet(
         parentEkit.getTextPane().getCharacterAttributes());
      mutator.mutate(baseAttrs);
      
      copy(baseAttrs, sasAttr);
      return sasAttr;
   }

   private void copy(SimpleAttributeSet source, SimpleAttributeSet target) {
      Enumeration<?> attribEntriesOriginal = source.getAttributeNames();
      while(attribEntriesOriginal.hasMoreElements())
      {
      	Object entryKey   = attribEntriesOriginal.nextElement();
      	Object entryValue = source.getAttribute(entryKey);
      	insertAttribute(target, entryKey, entryValue);
      }
   }

   private void processColor(ActionEvent ae) {
      if(htmlAttribs.containsKey("color"))
      {
         Color color = JColorChooser.showDialog(parentEkit.getFrame(), Translatrix.getTranslationString("CustomColorDialog"), Color.black);
          if(color != null)
      	{
      		StyledEditorKit.ForegroundAction customColorAction = new StyledEditorKit.ForegroundAction("CustomColor", color);
      		customColorAction.actionPerformed(ae);
      	}
      }
   }

   private String getNewAnchor(String currentAnchor) {
      
      if(htmlAttribs.containsKey("href"))
         return null;
         
   	UserInputAnchorDialog uidInput = new UserInputAnchorDialog(parentEkit, Translatrix.getTranslationString("AnchorDialogTitle"), true, currentAnchor);
   	String newAnchor = uidInput.getInputText();
   	uidInput.dispose();
   	if(newAnchor == null)
   	{
         parentEkit.repaint();
         return null;
   	}
   	
   	return newAnchor;
   }

   private void insertAttribute(SimpleAttributeSet attrs, Object key, 
      Object value)
	{
		if(value instanceof AttributeSet)
			insertAttributeSet(attrs, value);
		else
			insertAttr(attrs, key, value);
		
		insertFontAttributes(attrs, key, value);
	}

   private void insertAttributeSet(SimpleAttributeSet attrs, Object value) {
      AttributeSet subSet = (AttributeSet)value;
      Enumeration<?> attribEntriesSub = subSet.getAttributeNames();
      while(attribEntriesSub.hasMoreElements())
      {
      	Object subKey   = attribEntriesSub.nextElement();
      	Object subValue = subSet.getAttribute(subKey);
      	insertAttr(attrs, subKey, subValue);
      }
   }

   private void insertFontAttributes(SimpleAttributeSet attrs, Object key,
      Object value) {
      if(!key.toString().toLowerCase().equals("font-family"))
         return;
      
		if(attrs.isDefined("face"))			   
		{
			insertAttr(attrs, "face", attrs.getAttribute("face"));
			insertAttr(attrs, "font-family", attrs.getAttribute("face"));
			return;
		}
		
		insertAttr(attrs, "face", value);
   }

	private void insertAttr(SimpleAttributeSet attrs, Object key, Object value)
	{
		while(attrs.isDefined(key))
			attrs.removeAttribute(key);
		attrs.addAttribute(key, value);
	}
}
