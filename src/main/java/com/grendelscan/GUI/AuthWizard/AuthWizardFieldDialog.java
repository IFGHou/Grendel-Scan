package com.grendelscan.GUI.AuthWizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
public class AuthWizardFieldDialog extends org.eclipse.swt.widgets.Dialog {

	AuthWizardDialog authWizard;
	private Button cancelButton;
	Combo passwordFieldCombo;
	private Button okButton;
	Combo usernameFieldCombo;
	private Label usernameFieldLabel;
	Label passwordFieldLabel;
	
	public AuthWizardFieldDialog(AuthWizardDialog authWizard, Shell parent)
    {
	    super(parent);
	    this.authWizard = authWizard;
    }

	Shell dialogShell;
	private Composite dialogComposite;



/* TODO UCdetector: Remove unused code: 
	public AuthWizardFieldDialog(Shell parent, int style) {
		super(parent, style);
	}
*/

	public void open(String fields[]) 
	{
			Shell parent = getParent();
			dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			FillLayout dialogShellLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			SWTResourceManager.registerResourceUser(dialogShell);

			dialogShell.setText("Field Chooser");
			dialogShell.setLayout(dialogShellLayout);
			dialogShell.layout();
			dialogShell.pack();			
			dialogShell.setFont(SWTResourceManager.getFont(GUIConstants.fontName, GUIConstants.fontSize, 0, false, false));
			{
				dialogComposite = new Composite(dialogShell, SWT.NONE);
				dialogComposite.setLayout(null);
				{
					passwordFieldLabel = new Label(dialogComposite, SWT.NONE);
					passwordFieldLabel.setText("Password field:");
					passwordFieldLabel.setBounds(12, 61, 99, 30);
				}
				{
					usernameFieldLabel = new Label(dialogComposite, SWT.NONE);
					usernameFieldLabel.setText("Username field:");
					usernameFieldLabel.setBounds(12, 19, 99, 30);
				}
				{
					usernameFieldCombo = new Combo(dialogComposite, SWT.READ_ONLY);
					usernameFieldCombo.setBounds(123, 19, 204, 25);
					usernameFieldCombo.setItems(fields);
				}
				{
					okButton = new Button(dialogComposite, SWT.PUSH | SWT.CENTER);
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
					cancelButton = new Button(dialogComposite, SWT.PUSH | SWT.CENTER);
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
