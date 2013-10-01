package com.grendelscan.GUI.fuzzing;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Display;
import com.grendelscan.GUI.customControls.basic.GShell;

import com.grendelscan.GrendelScan;
import com.grendelscan.GUI.customControls.basic.GButton;
import com.grendelscan.GUI.proxy.interception.FilterChangeHandler;
import com.grendelscan.GUI.proxy.interception.InterceptFilter;
import com.grendelscan.GUI.proxy.interception.InterceptFilterLocation;
import com.grendelscan.GUI.proxy.interception.InterceptionRulesComposite;


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
public class FuzzCriteriaDialog extends com.grendelscan.GUI.customControls.basic.GDialog {
	protected GShell dialogShell;
	protected GButton platformErrorCheckbox;
	protected InterceptionRulesComposite filterComposite;
	protected GButton closeButton;
	protected final List<InterceptFilter> filters;
	protected boolean usePlatformErrors;
	
	/**
	 * 
	 * @param filters
	 * @return True if checks for platform error messages
	 */
	public static boolean showGUI(GShell parent, final List<InterceptFilter> filters, final boolean usePlatformErrors) 
	{
		FuzzCriteriaDialog inst = new FuzzCriteriaDialog(parent, SWT.NULL, filters, usePlatformErrors);
		return inst.usePlatformErrors;
	}

	private FuzzCriteriaDialog(GShell parent, int style, final List<InterceptFilter> filters, boolean usePlatformErrors) 
	{
		super(parent, style);
		this.usePlatformErrors = usePlatformErrors;
		this.filters = filters;
		initGUI();
	}

	private void initGUI() {
			dialogShell = new GShell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
			dialogShell.setText(GrendelScan.versionText + " - Fuzz Criteria");
			FillLayout dialogShellLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			dialogShell.setLayout(dialogShellLayout);
			GComposite dialogComposite = new GComposite(dialogShell, SWT.NONE);
			dialogComposite.setLayout(new FormLayout());
			{
				closeButton = new GButton(dialogComposite, SWT.PUSH | SWT.CENTER);
				FormData closeButtonLData = new FormData();
				closeButtonLData.width = 61;
				closeButtonLData.height = 27;
				closeButtonLData.left =  new FormAttachment(0, 1000, 5);
				closeButtonLData.bottom =  new FormAttachment(1000, 1000, -5);
				closeButton.setLayoutData(closeButtonLData);
				closeButton.setText("Close");
				closeButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evt) 
					{
						dialogShell.dispose();
					}
				});
			}
			{
				platformErrorCheckbox = new GButton(dialogComposite, SWT.CHECK | SWT.LEFT);
				FormData platformErrorCheckboxLData = new FormData();
				platformErrorCheckbox.setSelection(usePlatformErrors);
				platformErrorCheckboxLData.width = 271;
				platformErrorCheckboxLData.height = 19;
				platformErrorCheckboxLData.left =  new FormAttachment(0, 1000, 5);
				platformErrorCheckboxLData.top =  new FormAttachment(0, 1000, 5);
				platformErrorCheckbox.setLayoutData(platformErrorCheckboxLData);
				platformErrorCheckbox.setText("Check against platform error messages");
				platformErrorCheckbox.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evt) 
					{
						usePlatformErrors = platformErrorCheckbox.getSelection();
					}
				});
			}
			{
				FormData filterCompositeLData = new FormData();
				filterCompositeLData.width = 906;
				filterCompositeLData.height = 184;
				filterCompositeLData.left =  new FormAttachment(0, 1000, 5);
				filterCompositeLData.right =  new FormAttachment(1000, 1000, -5);
				filterCompositeLData.top =  new FormAttachment(0, 1000, 35);
				filterCompositeLData.bottom =  new FormAttachment(1000, 1000, -40);
				FilterChangeHandler changeHandler = new FilterChangeHandler()
				{
					@Override
					public void addFilter(InterceptFilter filter)
					{
					}
					@Override
					public void removeFilter(InterceptFilter filter)
					{
					}
				};
				filterComposite = new InterceptionRulesComposite(dialogComposite, SWT.NONE, InterceptFilterLocation.getResponseLocations(), true, changeHandler);
				filterComposite.updateFilterList(filters);
				filterComposite.setLayoutData(filterCompositeLData);
			}
			dialogComposite.layout();
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) 
			{
				if (!display.readAndDispatch())
					display.sleep();
			}
	}

}
