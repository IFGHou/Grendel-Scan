package com.grendelscan.GUI.fuzzing;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.grendelscan.GrendelScan;


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
public class FuzzTemplateComposite extends org.eclipse.swt.widgets.Dialog 
{
	private Label directionsLabel;
//	private TransactionComposite transactionComposite;
	Text transactionText;
	private Button cancelButton;
	private Button okButton;
	private FormData transactionLData;
	public static String fuzzTemplateText = "";
	private Shell dialogShell;
	
	@SuppressWarnings("unused")
	public static void getFuzzTemplate(Shell parent) 
	{
		new FuzzTemplateComposite(parent, SWT.NULL);
	}

	protected FuzzTemplateComposite(Shell parent, int style) 
	{
		super(parent, style);
		initGUI();
	}
	
	private void initGUI() 
	{
		fuzzTemplateText.replaceAll("\r\n", Text.DELIMITER);
			dialogShell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
			dialogShell.setText(GrendelScan.versionText + " - Fuzz Template");
			FillLayout dialogShellLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			dialogShell.setLayout(dialogShellLayout);
			Composite dialogComposite = new Composite(dialogShell, SWT.NONE);
			dialogComposite.setLayout(new FormLayout());
			{
				directionsLabel = new Label(dialogComposite, SWT.WRAP);
				FormData directionsLabelLData = new FormData();
				directionsLabelLData.width = 468;
				directionsLabelLData.height = 42;
				directionsLabelLData.left =  new FormAttachment(0, 1000, 5);
				directionsLabelLData.top =  new FormAttachment(0, 1000, 5);
				directionsLabel.setLayoutData(directionsLabelLData);
				directionsLabel.setText("To define a fuzz insertion point, type %%FUZZ%% anywhere in the HTTP request. Multiple locations are supported.");
			}
			{
				okButton = new Button(dialogComposite, SWT.PUSH | SWT.CENTER);
				FormData okButtonLData = new FormData();
				okButtonLData.width = 55;
				okButtonLData.height = 27;
				okButtonLData.left =  new FormAttachment(0, 1000, 6);
				okButtonLData.bottom =  new FormAttachment(1000, 1000, -4);
				okButton.setLayoutData(okButtonLData);
				okButton.setText("OK");
				okButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evt) 
					{
						fuzzTemplateText = transactionText.getText();
						dialogShell.close();
					}
				});
			}
			{
				cancelButton = new Button(dialogComposite, SWT.PUSH | SWT.CENTER);
				FormData cancelButtonLData = new FormData();
				cancelButtonLData.width = 55;
				cancelButtonLData.height = 27;
				cancelButtonLData.left =  new FormAttachment(0, 1000, 82);
				cancelButtonLData.bottom =  new FormAttachment(1000, 1000, -4);
				cancelButton.setLayoutData(cancelButtonLData);
				cancelButton.setText("Cancel");
				cancelButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evt) 
					{
						dialogShell.close();
					}
				});
			}
			{
				transactionLData = new FormData();
				transactionLData.width = 964;
				transactionLData.height = 511;
				transactionLData.right =  new FormAttachment(1000, 1000, -5);
				transactionLData.left =  new FormAttachment(0, 1000, 5);
				transactionLData.top =  new FormAttachment(0, 1000, 47);
				transactionLData.bottom =  new FormAttachment(1000, 1000, -37);
//				transactionComposite = new TransactionComposite(dialogComposite, SWT.NONE, true, false, false, false);
//				transactionComposite.updateRequestData(fuzzTemplateText);
				transactionText = new Text(dialogComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
				transactionText.setText(fuzzTemplateText);
				transactionText.setLayoutData(transactionLData);
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
