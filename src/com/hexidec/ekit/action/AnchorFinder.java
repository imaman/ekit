package com.hexidec.ekit.action;

import java.util.Enumeration;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML.Tag;

public class AnchorFinder {

   String getCurrentAnchor(JTextPane parentTextPane, Location location) {
      for(int i = location.offset; i < location.end(); i++)
   	{
   		parentTextPane.select(i, i + 1);
   		String result = getCurrentAnchor(parentTextPane.getCharacterAttributes());				
   		if(!result.isEmpty())
   		   return result;
   	}
      
      return "";
   }

   String getCurrentAnchor(AttributeSet attributeSet) {
      SimpleAttributeSet sasText = new SimpleAttributeSet(attributeSet);
      Enumeration<?> attribEntries1 = sasText.getAttributeNames();
      while(attribEntries1.hasMoreElements())
      {
      	Object entryKey   = attribEntries1.nextElement();
      	if(!entryKey.toString().equals(Tag.A.toString()))
      	   continue;
      	
         Object entryValue = sasText.getAttribute(entryKey);
      	if(!(entryValue instanceof SimpleAttributeSet))
      	   continue;
      	
      	Enumeration<?> subAttributes = ((SimpleAttributeSet)entryValue).getAttributeNames();
      	String currentAnchor = getAnchorFromAttributes(entryValue, 
      	   subAttributes);
      	
      	if(!currentAnchor.isEmpty())
      	   return currentAnchor;
      }
      
      return "";
   }

   String getAnchorFromAttributes(Object entryValue, Enumeration<?> subAttributes) {
      String currentAnchor = ""; 
      while(subAttributes.hasMoreElements() && currentAnchor.equals(""))
      {
      	Object subKey = subAttributes.nextElement();
      	if(subKey.toString().toLowerCase().equals("href"))
      	{
      		currentAnchor = ((SimpleAttributeSet)entryValue).getAttribute(subKey).toString();
      		break;
      	}
      }
      return currentAnchor;
   }

}
