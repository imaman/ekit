package com.hexidec.ekit;

import java.awt.Color;

import javax.swing.JColorChooser;

import com.hexidec.ekit.action.MyEkitCore;
import com.hexidec.ekit.action.MyJTextPane;
import com.hexidec.ekit.action.MyUserInputAnchorDialog;
import com.hexidec.ekit.component.SimpleInfoDialog;
import com.hexidec.ekit.component.UserInputAnchorDialog;

public class DefaultMyEkitCore implements MyEkitCore {

	private EkitCore inner;

	public DefaultMyEkitCore(EkitCore ekitCore) {
		this.inner = ekitCore;
	}

	@Override
	public Mutator getMutator() {
		return inner.getMutator();
	}

	@Override
	public MyJTextPane getTextPane() {
		return new DefaultMyJTextPane(inner.getTextPane());
	}

	@Override
	public MyUserInputAnchorDialog newUserInputAnchorDialog(
			MyEkitCore parentEkit, String title, boolean modal,
			String defaultAnchor) {
		
		return new DefaultMyUserInputAnchorDialog(new UserInputAnchorDialog(inner, title, modal, defaultAnchor));
	}

	@Override
	public void repaint() {
		inner.repaint();
	}

	@Override
	public void refreshOnUpdate() {
		inner.refreshOnUpdate();
	}

	@Override
	public void showInfoDialog(String title, boolean modal, String message, int type) {
		new SimpleInfoDialog(inner.getFrame(), title, modal, message, type);
	}

	@Override
	public Color chooseColor(String title, Color c) {
        return JColorChooser.showDialog(inner.getFrame(), title, c);
	}
	
	
}
