package com.grendelscan.ui.settings.scanSettings;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;

import com.grendelscan.scan.Scan;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.customControls.AddRemoveHandler;
import com.grendelscan.ui.customControls.AddRemoveListComposite;
import com.grendelscan.ui.customControls.RegexValidator;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.settings.GrendelSettingsControl;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SessionHandlingComposite extends com.grendelscan.ui.customControls.basic.GComposite implements
		GrendelSettingsControl
{

	protected AddRemoveListComposite	knownSessionIDListComposite;
	protected GGroup						knownSessionIDsGroup;
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		GuiUtils.registerResourceUser(this);
	}

	public SessionHandlingComposite(com.grendelscan.ui.customControls.basic.GComposite parent, int style)
	{
		super(parent, style);
		initGUI();
	}


	private void initGUI()
	{
		GridLayout thisLayout = new GridLayout();
		thisLayout.makeColumnsEqualWidth = true;
		this.setLayout(thisLayout);
		RegexValidator regexValidator = new RegexValidator();
		
		AddRemoveHandler sessionHandler = new AddRemoveHandler()
		{
			@Override public void addItem(String item)
				{Scan.getScanSettings().addKnownSessionIDRegex(Pattern.compile(item, Pattern.CASE_INSENSITIVE));}

			@Override public void removeItem(String item)
				{Scan.getScanSettings().removeKnownSessionIDRegex(Pattern.compile(item, Pattern.CASE_INSENSITIVE));}

			@Override public void clear()
				{Scan.getScanSettings().clearKnownSessionIDRegexs();}
		};
		knownSessionIDListComposite = new AddRemoveListComposite(this, SWT.NONE, "Known Session ID Patterns", sessionHandler, null);
		knownSessionIDListComposite.addDataValidator(regexValidator);
		
		this.layout();
	}


	@Override
	public String updateToSettings()
	{
		return ""; //handled in controls
	}

	@Override
	public void updateFromSettings()
	{
		
		knownSessionIDListComposite.removeAll();
		for (Pattern p: Scan.getScanSettings().getReadOnlyKnownSessionIDRegexs())
		{
			knownSessionIDListComposite.addItem(p.pattern());
		}
	}
}
