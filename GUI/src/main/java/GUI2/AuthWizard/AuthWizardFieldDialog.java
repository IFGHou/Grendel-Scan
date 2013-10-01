package com.grendelscan.GUI2.AuthWizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;

import org.eclipse.swt.widgets.Combo;
import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Display;
import com.grendelscan.GUI.customControls.basic.GLabel;
import org.eclipse.swt.widgets.MessageBox;
import com.grendelscan.GUI.customControls.basic.GShell;

import com.cloudgarden.resource.SWTResourceManager;
import com.grendelscan.GUI.GUIConstants;


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
public class AuthWizardFieldDialog extends com.grendelscan.GUI.customControls.basic.GDialog {

	AuthWizardDialog authWizard;
	private GButton cancelButton;
	Combo passwordFieldCombo;
	private GButton okButton;
	Combo usernameFieldCombo;
	private GLabel usernameFieldLabel;
	GLabel passwordFieldLabel;
	
	public AuthWizardFieldDialog(AuthWizardDialog authWizard, GShell parent)
    {
	    super(parent);
	    this.authWizard = authWizard;
    }

	GShell dialogShell;
	private GComposite dialogComposite;



/* TODO UCdetector: Remove unused code: 
	public AuthWizardFieldDialog(GShell parent, int style) {
		super(parent, style);
	}
*/

	public void open(String fields[]) 
	{
			GShell parent = getParent();
			dialogShell = new GShell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			FillLayout dialogShellLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			SWTResourceManager.registerResourceUser(dialogShell);

			dialogShell.setText("Field Chooser");
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setFontf(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
			{
				dialogComposite = new GComposite(dialogShell, SWT.NONE);
				dialogComposite.setLayout(null);
				{
					passwordFieldLabel = new GLabel(dialogComposite, SWT.NONE);
					passwordFieldLabel.setText("Password field:");
					passwordFieldLabel.setBounds(12, 61, 99, 30);
				}
				{
					usernameFieldLabel = new GLabel(dialogComposite, SWT.NONE);
					usernameFieldLabel.setText("Username field:");
					usernameFieldLabel.setBounds(12, 19, 99, 30);
				}
				{
					usernameFieldCombo = new Combo(dialogComposite, SWT.READ_ONLY);
					usernameFieldCombo.setBounds(123, 19, 204, 25);
					usernameFieldCombo.setItems(fields);
				}
				{
					okButton = new GButton(dialogComposite, SWT.PUSH | SWT.CENTER);
					okButton.setText("OK");
					okButton.setBounds(24, 120, 60, 30);
					okButton.addSelectionListener
					(
						new SelectionAdapter()
						{
							@Override
							public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
							{
								MessageBox messageBox = new MessageBox(getParent().getShell(), SWT.OK);
								messageBox.setMessage("Proxy started.");
								messageBox.setText("Authentication Wizard");
								if (usernameFieldCombo.getText().equals("") 
									|| passwordFieldLabel.getText().equals(""))
								{
									messageBox.setMessage("Please select username and password field names.");
									messageBox.open();
								}
								else if (usernameFieldCombo.getText().equals(passwordFieldCombo.getText()))
								{
									messageBox.setMessage("The username and password field names cannot be the same.");
									messageBox.open();
								}
								else
								{
									authWizard.setUsernameField(usernameFieldCombo.getText());
									authWizard.setPasswordField(passwordFieldCombo.getText());
									dialogShell.close();
								}
							}
						}
					);
				}
				{
					passwordFieldCombo = new Combo(dialogComposite, SWT.READ_ONLY);
					passwordFieldCombo.setBounds(123, 61, 204, 25);
					passwordFieldCombo.setItems(fields);
				}
				{
					cancelButton = new GButton(dialogComposite, SWT.PUSH | SWT.CENTER);
					cancelButton.setText("Cancel");
					cancelButton.setBounds(102, 120, 60, 30);
					cancelButton.addSelectionListener
					(
						new SelectionAdapter()
						{
							@Override
							public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
							{
								dialogShell.close();
							}
						}
					);
				}

			}
			dialogShell.setLocation(getParent().toDisplay(100, 100));
			dialogShell.open();
			Display display = dialogShell.getDisplay();
			while (!dialogShell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
	}
	
}
