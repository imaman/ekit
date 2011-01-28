package com.hexidec.ekit.action;

import javax.swing.text.SimpleAttributeSet;

public interface MyJTextPane {

	String getSelectedText();

	int getSelectionStart();

	void select(int i, int j);

	void setCharacterAttributes(SimpleAttributeSet sasTag, boolean b);

	void requestFocus();

	SimpleAttributeSet newSimpleAttributeSet();

}
