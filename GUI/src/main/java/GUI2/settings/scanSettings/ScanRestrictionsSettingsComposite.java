package com.grendelscan.GUI2.settings.scanSettings;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import com.grendelscan.GUI.customControls.basic.GGroup;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.customControls.AddRemoveHandler;
import com.grendelscan.GUI.customControls.AddRemoveListComposite;
import com.grendelscan.GUI.customControls.RegexValidator;
import com.grendelscan.GUI.settings.GrendelSettingsControl;
import com.grendelscan.scan.Scan;
import com.grendelscan.utils.collections.CollectionUtils;

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
public class ScanRestrictionsSettingsComposite extends com.grendelscan.GUI.customControls.basic.GComposite implements
		GrendelSettingsControl
{

	protected AddRemoveListComposite	forbiddenParameterList;

	protected GGroup						forbiddenParamterGroup;
	protected GGroup						ignoredParameterGroup;
//	protected AddRemoveListComposite	ignoredParameterList;
	protected AddRemoveListComposite	knownSessionIDListComposite;
	protected GGroup						knownSessionIDsGroup;
	protected AddRemoveListComposite	urlBlacklist;
	protected GGroup						urlBlacklistGroup;
	protected AddRemoveListComposite	urlWhitelist;
	protected GGroup						urlWhitelistGroup;
	{
		// Register as a resource user - SWTResourceManager will
		// handle the obtaining and disposing of resources
		SWTResourceManager.registerResourceUser(this);
	}

	public ScanRestrictionsSettingsComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style)
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
		
//		AddRemoveHandler ignoredParameterHandler = new AddRemoveHandler()
//		{
//			@Override public void addItem(String item)
//				{Scan.getScanSettings().addIrrelevantQueryParameter(item);}
//
//			@Override public void removeItem(String item)
//				{Scan.getScanSettings().removeIrrelevantQueryParameter(item);}
//
//			@Override public void clear()
//				{Scan.getScanSettings().clearIrrelevantQueryParameters();}
//		};
//		ignoredParameterList = new AddRemoveListComposite(this, SWT.NONE, "Ignored Parameter Names", ignoredParameterHandler, null);

		
		AddRemoveHandler forbiddenParameterHandler = new AddRemoveHandler()
		{
			@Override public void addItem(String item)
				{Scan.getScanSettings().addForbiddenQueryParameter(item);}

			@Override public void removeItem(String item)
				{Scan.getScanSettings().removeForbiddenQueryParameter(item);}

			@Override public void clear()
				{Scan.getScanSettings().clearForbiddenQueryParameters();}
		};
		forbiddenParameterList = new AddRemoveListComposite(this, SWT.NONE, "Forbidden Query Parameters", forbiddenParameterHandler, null);

		
		AddRemoveHandler blacklistHandler = new AddRemoveHandler()
		{
			@Override public void addItem(String item)
				{Scan.getScanSettings().getUrlFilters().addUrlBlacklist(Pattern.compile(item));}

			@Override public void removeItem(String item)
				{Scan.getScanSettings().getUrlFilters().removeUrlBlacklist(Pattern.compile(item));}

			@Override public void clear()
				{Scan.getScanSettings().getUrlFilters().clearUrlBlacklists();}
		};
		urlBlacklist = new AddRemoveListComposite(this, SWT.NONE, "URL Blacklist Regexs", blacklistHandler, null);
		urlBlacklist.addDataValidator(regexValidator);

		
		AddRemoveHandler whitelistHandler = new AddRemoveHandler()
		{
			@Override public void addItem(String item)
				{Scan.getScanSettings().getUrlFilters().addUrlWhitelist(Pattern.compile(item));}

			@Override public void removeItem(String item)
				{Scan.getScanSettings().getUrlFilters().removeUrlWhitelist(Pattern.compile(item));}

			@Override public void clear()
				{Scan.getScanSettings().getUrlFilters().clearUrlWhitelists();}
		};
		urlWhitelist = new AddRemoveListComposite(this, SWT.NONE, "URL Whitelist Regexs", whitelistHandler, null);
		urlWhitelist.addDataValidator(regexValidator);

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
//		ignoredParameterList.setItems(CollectionUtils.toStringArray(Scan.getScanSettings()
//				.getReadOnlyIrrelevantQueryParameters()));
		forbiddenParameterList.setItems(CollectionUtils.toStringArray(Scan.getScanSettings()
				.getReadOnlyForbiddenQueryParameters()));
		urlBlacklist.setItems(CollectionUtils.toStringArray(Scan.getScanSettings().getUrlFilters()
				.getBlacklistsAsString()));
		urlWhitelist.setItems(CollectionUtils.toStringArray(Scan.getScanSettings().getUrlFilters()
				.getWhitelistsAsString()));
	}
}
