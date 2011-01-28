package com.hexidec.ekit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;

import org.junit.Test;

import com.hexidec.ekit.action.CustomAction;
import com.hexidec.ekit.action.MyEkitCore;
import com.hexidec.ekit.action.MyJTextPane;
import com.hexidec.ekit.action.MyUserInputAnchorDialog;

public class Ekit_Unit_Tests  {

   private List<String> log = new ArrayList<String>();
   
   private void register(Object o, String string) {
      String objName = null;
      if(o == w)
         objName = "w";
      else if(o == x)
         objName = "x";
      else if(o == y)
         objName = "y";
      else if(o == z)
         objName = "z";
      else if(o == m)
         objName = "m";
       
      log.add(objName + ":" + string);      
   }
   
   int lastVerifiedPosition = 0;
   
   private void verify(String s) {
      assertEquals(s, log.get(lastVerifiedPosition));
      ++lastVerifiedPosition;      
   }
   
   private void verifyEndOfInteraction() {
      assertEquals(log.size(), lastVerifiedPosition);
   }
   
	private class Y implements MyJTextPane {

		public int begin;
		public String selectedText;
      public SimpleAttributeSet sas = new SimpleAttributeSet();
      private SimpleAttributeSet incomingSas;
      private boolean b;

		@Override
		public String getSelectedText() {
		   register(this, "getSelectedText");
			return selectedText;
		}

		@Override
		public int getSelectionStart() {
         register(this, "getSelectionStart");
			return begin;
		}

		@Override
		public void select(int i, int j) {
		   register(this, "select(" + i + "," + j + ")");
		}

		@Override
		public void setCharacterAttributes(SimpleAttributeSet sasTag, boolean b) {
		   register(this, "sca");
		   this.incomingSas = sasTag;
		   this.b = b;
		}

		@Override
		public void requestFocus() {
         register(this, "requestFocus");
		}

		@Override
		public SimpleAttributeSet newSimpleAttributeSet() {
         register(this, "requestFocus");
         return sas;
		}		
	}
	
	public class Z implements MyUserInputAnchorDialog {

		public String inputText;

		@Override
		public void dispose() {
		   register(this, "dispose");
		}

		@Override
		public String getInputText() {
			return inputText;
		}
		
	}
	
	private class X implements MyEkitCore {

		public String message;
		public Color color;

		@Override
		public Mutator getMutator() {
			return m;
		}

		@Override
		public MyJTextPane getTextPane() {
			return y;
		}

		@Override
		public MyUserInputAnchorDialog newUserInputAnchorDialog(MyEkitCore owner, 
		   String title, boolean modal, String defaultAnchor) {
         register(this, "nuiad(" + title + "," + modal + "," + defaultAnchor + ")");
			return w;
		}

		@Override
		public void repaint() {
         register(this, "repaint");
		}

		@Override
		public void refreshOnUpdate() {
		   register(this, "refreshOnUpdate");
		}

		@Override
		public void showInfoDialog(String title, boolean modal, String message,
				int type) {
		   register(this, "sid(" + title + "," + modal + "," + message + ")");
			this.message = message;
		}

		@Override
		public Color chooseColor(String title, Color c) {
			return color;
		}

      @Override
      public void setForegroundColor(Color color, ActionEvent ae) {
         register(this, "setForegroundColor(" + color + "," + ae + ")");
      }	   
	}
   
	private class W implements MyUserInputAnchorDialog {
      public String inputText;

      @Override
      public void dispose() {
         register(this, "dispose");
      }


      @Override
      public String getInputText() {
         return inputText;
      }	   
	}
	
	private class M implements Mutator {

      public SimpleAttributeSet sas;

      @Override
      public void mutate(SimpleAttributeSet sas) {
         register(this, "mutate");
         this.sas = sas;
      }	   
	}
	
	private M m = new M();
	private W w = new W();
	private Y y = new Y();
	private X x = new X();
	private Z z = new Z();

	private CustomAction fontAction;
   private CustomAction anchorAction;
   
	public Ekit_Unit_Tests() {
      anchorAction = new CustomAction(x, "Insert_Anchor_Translated_String", 
                                      HTML.Tag.A);
      
		Hashtable<String, String> customAttr = new Hashtable<String, String>();
		customAttr.put("color", "black");
		fontAction = new CustomAction(x, "Custom_Color_Translated_String", 
                                    HTML.Tag.FONT, customAttr);		
	}
   
   @Test
   public void shouldRejectAnchorIfNoTextIsSelected() throws InterruptedException {
	   y.selectedText = "";
	   anchorAction.actionPerformed(null);
	   assertEquals("ErrorNoTextSelected", x.message);
   }

   @Test
   public void shouldCreateAnchor() throws InterruptedException {
      y.selectedText = "andom ";
      y.begin = 6;
      
      final String URL = "some.url";
      w.inputText = URL;
      anchorAction.actionPerformed(null);
      
      verify("y:getSelectedText");
      verify("y:getSelectionStart");
      verify("y:select(6,7)");
      verify("y:requestFocus");
      verify("y:select(7,8)");
      verify("y:requestFocus");
      verify("y:select(8,9)");
      verify("y:requestFocus");
      verify("y:select(9,10)");
      verify("y:requestFocus");
      verify("y:select(10,11)");
      verify("y:requestFocus");
      verify("y:select(11,12)");
      verify("y:requestFocus");
      verify("y:select(6,12)");
      verify("x:nuiad(AnchorDialogTitle,true,)");
      verify("w:dispose");
      verify("y:requestFocus");
      verify("m:mutate");
      verify("y:sca");
      verify("x:refreshOnUpdate");
      verify("y:select(6,12)");
      verify("y:requestFocus");
      
      verifyEndOfInteraction();

      assertFalse(y.b);
      assertSame(m.sas, y.sas);
      
      checkAttribute(y.incomingSas, URL, HTML.Tag.A, "href");
   }
   

   @Test
   public void shouldNotCreateAnchorIfUserCancels() throws InterruptedException {
      // window.textBox("main").setText("Some random text");
      // window.textBox("main").selectText(6, 12);
      y.begin = 6;
      y.selectedText = "random";
      
      // window.dialog("user.input").button("cancel").click();
      w.inputText = null;
      
      anchorAction.actionPerformed(null);
      
      verify("y:getSelectedText");
      verify("y:getSelectionStart");
      verify("y:select(6,7)");
      verify("y:requestFocus");
      verify("y:select(7,8)");
      verify("y:requestFocus");
      verify("y:select(8,9)");
      verify("y:requestFocus");
      verify("y:select(9,10)");
      verify("y:requestFocus");
      verify("y:select(10,11)");
      verify("y:requestFocus");
      verify("y:select(11,12)");
      verify("y:requestFocus");
      verify("y:select(6,12)");
      verify("x:nuiad(AnchorDialogTitle,true,)");
      verify("w:dispose");
      verify("x:repaint");
      verifyEndOfInteraction();
      
      assertNull(y.incomingSas);
   }
   
   @Test
   public void shouldExtractExistingLink() throws InterruptedException {
      final String OLD_URL = "old.url";
      final String NEW_URL = "new.url";

      // window.textBox("main").setText("word1 <a href=\"" + OLD_URL + "\">link</a> word2");
      // window.textBox("main").selectText(7, 11);
      y.begin = 7;
      y.selectedText = "a hre";
      SimpleAttributeSet sub = new SimpleAttributeSet();
      sub.addAttribute("HRef", OLD_URL);
      y.sas.addAttribute(HTML.Tag.A, sub);
            
      // window.dialog("user.input").textBox().setText(NEW_URL);
      w.inputText = NEW_URL;
      
      // window.dialog("user.input").button("accept").click();
      // robot().waitForIdle();
      // window.button("button.anchor").click();
      anchorAction.actionPerformed(null);
      
      verify("y:getSelectedText");
      verify("y:getSelectionStart");
      verify("y:select(7,8)");
      verify("y:requestFocus");
      verify("y:select(7,12)");
      // window.dialog("user.input").textBox().requireText(OLD_URL);
      verify("x:nuiad(AnchorDialogTitle,true,old.url)");
      verify("w:dispose");
      verify("y:requestFocus");
      verify("m:mutate");
      verify("y:sca");
      verify("x:refreshOnUpdate");
      verify("y:select(7,12)");
      verify("y:requestFocus");
      verifyEndOfInteraction();
      
      checkAttribute(y.incomingSas, NEW_URL, HTML.Tag.A, "href");      
   }

//   private void dumpLog() {
//      for(String s: log) {
//         System.out.println("verify(\"" + s + "\");");
//      }
//      System.out.println("verifyEndOfInteraction();");         
//   }
   
   
   @Test
   public void shouldChangeFontColor() throws InterruptedException {
      // window.textBox("main").setText("Some random text");
      // window.textBox("main").selectText(6, 12);
      y.begin = 6;
      y.selectedText = "andom ";
      x.color = Color.BLACK;
         
      fontAction.actionPerformed(null);
      
      verify("y:getSelectedText");
      verify("y:getSelectionStart");
      verify("y:select(6,12)");
      verify("x:setForegroundColor(java.awt.Color[r=0,g=0,b=0],null)");
      verify("y:select(6,12)");
      verify("y:requestFocus");
      verifyEndOfInteraction();
   }
   
   @Test
   public void shouldHandleExistingFontFaceAndFamily() throws Exception {
      
      // frame.ekitCore.setMutator(new Mutator() { ...
      this.m = new M() {
         
         @Override
         public void mutate(SimpleAttributeSet sas) {
            SimpleAttributeSet injectedSas = new SimpleAttributeSet();
            injectedSas.addAttribute("face", "David");
            sas.addAttribute("font-family", injectedSas);
         }
      };
      
      // window.textBox("main").setText("word1 link word2");
      // window.textBox("main").selectText(7, 11);
      y.begin = 7;
      y.selectedText = "link";

      // window.dialog("user.input").textBox().setText(NEW_URL);
      final String NEW_URL = "new.url";
      w.inputText = NEW_URL;

      // window.button("button.anchor").click();
      anchorAction.actionPerformed(null);
      
      verify("y:getSelectedText");
      verify("y:getSelectionStart");
      verify("y:select(7,8)");
      verify("y:requestFocus");
      verify("y:select(8,9)");
      verify("y:requestFocus");
      verify("y:select(9,10)");
      verify("y:requestFocus");
      verify("y:select(10,11)");
      verify("y:requestFocus");
      verify("y:select(7,11)");
      verify("x:nuiad(AnchorDialogTitle,true,)");
      verify("w:dispose");
      verify("y:requestFocus");
      verify("y:sca");
      verify("x:refreshOnUpdate");
      verify("y:select(7,11)");
      verify("y:requestFocus");
      verifyEndOfInteraction();      
      
      assertFalse(y.b);
      checkAttribute(y.incomingSas, NEW_URL, HTML.Tag.A, "href");
      checkAttribute(y.incomingSas, "David", HTML.Tag.A, "font-family");
      checkAttribute(y.incomingSas, "David", HTML.Tag.A, "face");
   }

   @Test
   public void shouldHandleExistingFontFamily() throws Exception {
      
      this.m = new M() {
         
         @Override
         public void mutate(SimpleAttributeSet sas) {
            sas.addAttribute("font-family", "Monospace");
         }
      };

      // window.textBox("main").setText("word1 link word2");
      // window.textBox("main").selectText(7, 11);
      y.begin = 7;
      y.selectedText = "link";

      // window.dialog("user.input").textBox().setText(NEW_URL);
      final String NEW_URL = "new.url";
      w.inputText = NEW_URL;

      anchorAction.actionPerformed(null);

      verify("y:getSelectedText");
      verify("y:getSelectionStart");
      verify("y:select(7,8)");
      verify("y:requestFocus");
      verify("y:select(8,9)");
      verify("y:requestFocus");
      verify("y:select(9,10)");
      verify("y:requestFocus");
      verify("y:select(10,11)");
      verify("y:requestFocus");
      verify("y:select(7,11)");
      verify("x:nuiad(AnchorDialogTitle,true,)");
      verify("w:dispose");
      verify("y:requestFocus");
      verify("y:sca");
      verify("x:refreshOnUpdate");
      verify("y:select(7,11)");
      verify("y:requestFocus");
      verifyEndOfInteraction();
      
      assertFalse(y.b);
      
      // String text = window.textBox("main").text();
      // assertTrue(text.contains(NEW_URL));
      checkAttribute(y.incomingSas, NEW_URL, HTML.Tag.A, "href");
      checkAttribute(y.incomingSas, "Monospace", HTML.Tag.A, "font-family");
      checkAttribute(y.incomingSas, "Monospace", HTML.Tag.A, "face");
   }

   private void checkAttribute(SimpleAttributeSet sas, final String expected, 
      Object key1, Object key2) {
      assertEquals(expected, ((SimpleAttributeSet) sas.getAttribute(key1))
         .getAttribute(key2));
   }
   
   public void show(String indentation, SimpleAttributeSet a) {
      for(Enumeration<?> e = a.getAttributeNames(); e.hasMoreElements(); ) {
         Object name = e.nextElement();
         Object value = a.getAttribute(name);
         if (value instanceof SimpleAttributeSet) {
            System.out.println(name);
            show(indentation + "  ", (SimpleAttributeSet) value);
         } else {
            System.out.println(indentation + name + "-- " + value);
         }
      }
   }
   
   public static void main(String[] args) {
      SimpleAttributeSet sas = new SimpleAttributeSet();
      sas.addAttribute("n1", "v1");
      sas.addAttribute("n1", "v2");
      
      System.out.println(sas);
   }
}

