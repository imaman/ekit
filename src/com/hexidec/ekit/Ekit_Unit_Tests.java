package com.hexidec.ekit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;

import org.junit.Ignore;
import org.junit.Test;

import com.hexidec.ekit.action.CustomAction;
import com.hexidec.ekit.action.MyEkitCore;
import com.hexidec.ekit.action.MyJTextPane;
import com.hexidec.ekit.action.MyUserInputAnchorDialog;

public class Ekit_Unit_Tests  {

	private class Y implements MyJTextPane {

		public int begin;
		public String text;

		@Override
		public String getSelectedText() {
			return text;
		}

		@Override
		public int getSelectionStart() {
			return begin;
		}

		@Override
		public void select(int i, int j) {
		}

		@Override
		public void setCharacterAttributes(SimpleAttributeSet sasTag, boolean b) {
		}

		@Override
		public void requestFocus() {
		}

		@Override
		public SimpleAttributeSet newSimpleAttributeSet() {
			return null;
		}		
	}
	
	public class Z implements MyUserInputAnchorDialog {

		public String inputText;

		@Override
		public void dispose() {
		}

		@Override
		public String getInputText() {
			return inputText;
		}
		
	}
	
	private class X implements MyEkitCore {

		private String message;

		@Override
		public Mutator getMutator() {
			return null;
		}

		@Override
		public MyJTextPane getTextPane() {
			return y;
		}

		@Override
		public MyUserInputAnchorDialog newUserInputAnchorDialog(
				MyEkitCore owner, String title, boolean modal,
				String defaultAnchor) {
			return null;
		}

		@Override
		public void repaint() {
			System.out.println();
		}

		@Override
		public void refreshOnUpdate() {
			System.out.println();
		}

		@Override
		public void showInfoDialog(String title, boolean modal, String message,
				int type) {
			this.message = message;
		}

		@Override
		public Color chooseColor(String title, Color c) {
			return null;
		}	   
	}
   
   	private Y y = new Y();
   	private X x = new X();
   	private Z z = new Z();
   
   	private CustomAction ca;
   
	public Ekit_Unit_Tests() {
		Hashtable<String, String> customAttr = new Hashtable<String, String>();
		customAttr.put("color", "black");

		ca = new CustomAction(x, "", HTML.Tag.FONT, customAttr);
	}
   
   @Test
   public void shouldRejectAnchorIfNoTextIsSelected() throws InterruptedException {
	   y.text = "";
	   ca.actionPerformed(null);
	   assertEquals("ErrorNoTextSelected", x.message);
   }

   @Test
   @Ignore
   public void shouldCreateAnchor() throws InterruptedException {
      y.text = "andom ";
      y.begin = 6;
      
      ca.actionPerformed(null);
      
      final String URL = "some.url";
      z.inputText = URL;
      ca.actionPerformed(null);
      String text = "";
      assertTrue(text.contains(URL));
   }
//
//   @Test
//   public void shouldNotCreateAnchorIfUserCancels() throws InterruptedException {
//      window.textBox("main").setText("Some random text");
//      window.textBox("main").selectText(6, 12);
//      window.button("button.anchor").click();
//      robot().waitForIdle();
//      final String URL = "some.url";
//      window.dialog("user.input").textBox().setText(URL);
//      window.dialog("user.input").button("cancel").click();
//      robot().waitForIdle();
//      String text = window.textBox("main").text();
//      assertFalse(text.contains(URL));
//   }
//
//   @Test
//   public void shouldExtractExistingLink() throws InterruptedException {
//      final String OLD_URL = "old.url";
//      final String NEW_URL = "new.url";
//      window.textBox("main").setText("word1 <a href=\"" + OLD_URL + "\">link</a> word2");
//      window.textBox("main").selectText(7, 11);
//      window.button("button.anchor").click();
//      robot().waitForIdle();
//      window.dialog("user.input").textBox().requireText(OLD_URL);
//      window.dialog("user.input").textBox().setText(NEW_URL);
//      
//      window.dialog("user.input").button("accept").click();
//      robot().waitForIdle();
//      String text = window.textBox("main").text();
//      assertFalse(text.contains(OLD_URL));
//      assertTrue(text.contains(NEW_URL));
//   }
//   
//   @Test
//   public void shouldChangeFontColor() throws InterruptedException {
//      window.textBox("main").setText("Some random text");
//      window.textBox("main").selectText(6, 12);
//      window.menuItemWithPath("Font", "Color", "Custom...").click();
//      robot().waitForIdle();
//      window.dialog().button(new GenericTypeMatcher<JButton>(JButton.class) {
//
//         @Override
//         protected boolean isMatching(JButton b) {
//            return "OK".equals(b.getActionCommand());
//         }         
//      }).click();
//      
//      String text = window.textBox("main").text();
//      assertTrue(text.contains("Some <font color=\"#000000\">random</font> text"));
//   }
//   
//   @Test
//   public void shouldUpdateFontColor() throws InterruptedException {
//      window.textBox("main").setText("should be black");
//      window.textBox("main").selectText(0, 16);
//      window.menuItemWithPath("Font", "Color", "Custom...").click();
//      robot().waitForIdle();
//      window.dialog().button(new GenericTypeMatcher<JButton>(JButton.class) {
//
//         @Override
//         protected boolean isMatching(JButton b) {
//            return "OK".equals(b.getActionCommand());
//         }         
//      }).click();
//      
//      String text = window.textBox("main").text();
//      System.out.println("text=\n" + text);
//      assertTrue(text.contains("<font color=\"#000000\">should be black</font>"));
//   }
//
//
//   @Test
//   public void shouldHandleExistingFontFaceAndFamily() throws Exception {
//      
//      frame.ekitCore.setMutator(new Mutator() {
//         
//         @Override
//         public void mutate(SimpleAttributeSet sas) {
//            SimpleAttributeSet injectedSas = new SimpleAttributeSet();
//            injectedSas.addAttribute("face", "David");
//            sas.addAttribute("font-family", injectedSas);
//         }
//      });
//
//      final String NEW_URL = "new.url";
//      window.textBox("main").setText("word1 link word2");
//      window.textBox("main").selectText(7, 11);
//      window.button("button.anchor").click();
//      robot().waitForIdle();
//      window.dialog("user.input").textBox().setText(NEW_URL);
//
//      window.dialog("user.input").button("accept").click();
//      robot().waitForIdle();
//      String text = window.textBox("main").text();
//      assertTrue(text.contains(NEW_URL));
//   }
//
//   @Test
//   public void shouldHandleExistingFontFamily() throws Exception {
//      
//      frame.ekitCore.setMutator(new Mutator() {
//         
//         @Override
//         public void mutate(SimpleAttributeSet sas) {
////            SimpleAttributeSet injectedSas = new SimpleAttributeSet();
//            sas.addAttribute("font-family", "Monospace");
////            sas.addAttribute("font-family", injectedSas);
//         }
//      });
//
//      final String NEW_URL = "new.url";
//      window.textBox("main").setText("word1 link word2");
//      window.textBox("main").selectText(7, 11);
//      window.button("button.anchor").click();
//      robot().waitForIdle();
//      window.dialog("user.input").textBox().setText(NEW_URL);
//
//
//      
//      window.dialog("user.input").button("accept").click();
//      robot().waitForIdle();
//      String text = window.textBox("main").text();
//      assertTrue(text.contains(NEW_URL));
//   }
}

