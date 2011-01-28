package com.hexidec.ekit;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

import com.hexidec.ekit.action.MyJTextPane;

public class DefaultMyJTextPane implements MyJTextPane {

	private JTextPane inner;

	public DefaultMyJTextPane(JTextPane textPane) {
		this.inner = textPane;
	}

	@Override
	public String getSelectedText() {
		return inner.getSelectedText();
	}

	@Override
	public int getSelectionStart() {
		return inner.getSelectionStart();
	}

	@Override
	public void select(int i, int j) {
		inner.select(i, j);
	}

	@Override
	public void setCharacterAttributes(SimpleAttributeSet sasTag, boolean b) {
		inner.setCharacterAttributes(sasTag, b);
	}

	@Override
	public void requestFocus() {
		inner.requestFocus();
	}

	@Override
	public SimpleAttributeSet newSimpleAttributeSet() {
		return new SimpleAttributeSet(inner.getCharacterAttributes());
	}
}
