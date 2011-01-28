package com.hexidec.ekit;

import com.hexidec.ekit.action.MyUserInputAnchorDialog;
import com.hexidec.ekit.component.UserInputAnchorDialog;

public class DefaultMyUserInputAnchorDialog implements MyUserInputAnchorDialog {

	private UserInputAnchorDialog inner;

	public DefaultMyUserInputAnchorDialog(
			UserInputAnchorDialog userInputAnchorDialog) {
		this.inner = userInputAnchorDialog;
	}

	@Override
	public void dispose() {
		inner.dispose();
	}

	@Override
	public String getInputText() {
		return inner.getInputText();
	}

}
