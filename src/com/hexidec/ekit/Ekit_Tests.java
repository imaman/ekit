package com.hexidec.ekit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JButton;
import javax.swing.text.SimpleAttributeSet;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;

public class Ekit_Tests extends FestSwingJUnitTestCase {

   private FrameFixture window;
   private Ekit frame;
   
   @Override
   protected void onSetUp() {
      frame = GuiActionRunner.execute(new GuiQuery<Ekit>() {
         @Override
         protected Ekit executeInEDT() {
            return new Ekit();
         }
      });

      window = new FrameFixture(robot(), frame);
      window.show(); 
   }

   @Test
   public void shouldRejectAnchorIfNoTextIsSelected() throws InterruptedException {
      window.textBox("main").setText("Some random text");
      window.button("button.anchor").click();
      robot().waitForIdle();
      window.dialog("info.dialog").label("label").requireText("No text was selected.");
   }


   @Test
   public void shouldCreateAnchor() throws InterruptedException {
      window.textBox("main").setText("Some random text");
      window.textBox("main").selectText(6, 12);
      window.button("button.anchor").click();
      robot().waitForIdle();
      final String URL = "some.url";
      window.dialog("user.input").textBox().setText(URL);
      window.dialog("user.input").button("accept").click();
      robot().waitForIdle();
      String text = window.textBox("main").text();
      assertTrue(text.contains(URL));
   }

   @Test
   public void shouldNotCreateAnchorIfUserCancels() throws InterruptedException {
      window.textBox("main").setText("Some random text");
      window.textBox("main").selectText(6, 12);
      window.button("button.anchor").click();
      robot().waitForIdle();
      final String URL = "some.url";
      window.dialog("user.input").textBox().setText(URL);
      window.dialog("user.input").button("cancel").click();
      robot().waitForIdle();
      String text = window.textBox("main").text();
      assertFalse(text.contains(URL));
   }

   @Test
   public void shouldExtractExistingLink() throws InterruptedException {
      final String OLD_URL = "old.url";
      final String NEW_URL = "new.url";
      window.textBox("main").setText("word1 <a href=\"" + OLD_URL + "\">link</a> word2");
      window.textBox("main").selectText(7, 11);
      window.button("button.anchor").click();
      robot().waitForIdle();
      window.dialog("user.input").textBox().requireText(OLD_URL);
      window.dialog("user.input").textBox().setText(NEW_URL);
      
      window.dialog("user.input").button("accept").click();
      robot().waitForIdle();
      String text = window.textBox("main").text();
      assertFalse(text.contains(OLD_URL));
      assertTrue(text.contains(NEW_URL));
   }
   
   @Test
   public void shouldChangeFontColor() throws InterruptedException {
      window.textBox("main").setText("Some random text");
      window.textBox("main").selectText(6, 12);
      window.menuItemWithPath("Font", "Color", "Custom...").click();
      robot().waitForIdle();
      window.dialog().button(new GenericTypeMatcher<JButton>(JButton.class) {

         @Override
         protected boolean isMatching(JButton b) {
            return "OK".equals(b.getActionCommand());
         }         
      }).click();
      
      String text = window.textBox("main").text();
      assertTrue(text.contains("Some <font color=\"#000000\">random</font> text"));
   }
   
   @Test
   public void shouldUpdateFontColor() throws InterruptedException {
      window.textBox("main").setText("should be black");
      window.textBox("main").selectText(0, 16);
      window.menuItemWithPath("Font", "Color", "Custom...").click();
      robot().waitForIdle();
      window.dialog().button(new GenericTypeMatcher<JButton>(JButton.class) {

         @Override
         protected boolean isMatching(JButton b) {
            return "OK".equals(b.getActionCommand());
         }         
      }).click();
      
      String text = window.textBox("main").text();
      System.out.println("text=\n" + text);
      assertTrue(text.contains("<font color=\"#000000\">should be black</font>"));
   }


   @Test
   public void shouldHandleExistingFontFaceAndFamily() throws Exception {
      
      frame.ekitCore.setMutator(new Mutator() {
         
         @Override
         public void mutate(SimpleAttributeSet sas) {
            SimpleAttributeSet injectedSas = new SimpleAttributeSet();
            injectedSas.addAttribute("face", "David");
            sas.addAttribute("font-family", injectedSas);
         }
      });

      final String NEW_URL = "new.url";
      window.textBox("main").setText("word1 link word2");
      window.textBox("main").selectText(7, 11);
      window.button("button.anchor").click();
      robot().waitForIdle();
      window.dialog("user.input").textBox().setText(NEW_URL);

      window.dialog("user.input").button("accept").click();
      robot().waitForIdle();
      String text = window.textBox("main").text();
      assertTrue(text.contains(NEW_URL));
   }

   @Test
   public void shouldHandleExistingFontFamily() throws Exception {
      
      frame.ekitCore.setMutator(new Mutator() {
         
         @Override
         public void mutate(SimpleAttributeSet sas) {
//            SimpleAttributeSet injectedSas = new SimpleAttributeSet();
            sas.addAttribute("font-family", "Monospace");
//            sas.addAttribute("font-family", injectedSas);
         }
      });

      final String NEW_URL = "new.url";
      window.textBox("main").setText("word1 link word2");
      window.textBox("main").selectText(7, 11);
      window.button("button.anchor").click();
      robot().waitForIdle();
      window.dialog("user.input").textBox().setText(NEW_URL);


      
      window.dialog("user.input").button("accept").click();
      robot().waitForIdle();
      String text = window.textBox("main").text();
      assertTrue(text.contains(NEW_URL));
   }
}

