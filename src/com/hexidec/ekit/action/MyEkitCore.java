package com.hexidec.ekit.action;

import java.awt.Color;

import com.hexidec.ekit.Mutator;

public interface MyEkitCore {

	Mutator getMutator();

	MyJTextPane getTextPane();

	MyUserInputAnchorDialog newUserInputAnchorDialog(MyEkitCore owner, String title, boolean modal,
		String defaultAnchor);

	void repaint();

	void refreshOnUpdate();

	void showInfoDialog(String title, boolean modal, String message, int type);

	Color chooseColor(String title, Color c);

}
