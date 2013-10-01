package com.grendelscan.ui.fuzzing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.grendelscan.GrendelScan;
import com.grendelscan.fuzzing.FuzzVector;
import com.grendelscan.fuzzing.FuzzVectorFormatException;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GShell;
import com.grendelscan.ui.fuzzing.vectorComposites.FuzzVectorComposite;
import com.grendelscan.ui.fuzzing.vectorComposites.NumericSequenceComposite;
import com.grendelscan.ui.fuzzing.vectorComposites.PresetStringComposite;
import com.grendelscan.ui.fuzzing.vectorComposites.StringSequenceComposite;

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
public class FuzzVectorChoiceDialog extends com.grendelscan.ui.customControls.basic.GDialog 
{
	private GButton cancelButton;
	private GButton okButton;
	private GGroup fuzzVectorGroup;
	Combo vectorTypeCombo;
	GComposite currentFuzzComposite;
	private FormData fuzzCompositeLayout;
	FuzzVector fuzzVector;
	GShell dialogShell;

		
	public static FuzzVector getFuzzVector(GShell shell, FuzzVector oldVector) 
	{
		FuzzVectorChoiceDialog inst = new FuzzVectorChoiceDialog(shell, SWT.NULL, oldVector);
		return inst.fuzzVector;
	}

	public FuzzVectorChoiceDialog(GShell parent, int style, FuzzVector oldVector) 
	{
		super(parent, style);
		initGUI(oldVector);
	}
	
	private void initVectorComposites(FuzzVector oldVector)
	{
		fuzzVector = oldVector;
		List<FuzzVectorComposite> comps = new ArrayList<FuzzVectorComposite>(3);
		comps.add(new StringSequenceComposite(fuzzVectorGroup, SWT.NONE));
		comps.add(new NumericSequenceComposite(fuzzVectorGroup, SWT.NONE));
		comps.add(new PresetStringComposite(fuzzVectorGroup, SWT.NONE));
		
//		vectorComposites.put("Numeric sequence", new NumericSequenceComposite(fuzzVectorGroup, SWT.NONE));
		
		for (FuzzVectorComposite comp: comps)
		{
			vectorTypeCombo.add(comp.getDescription());
			vectorTypeCombo.setData(comp.getDescription(), comp);
			GComposite composite = (GComposite) comp;
			if (oldVector != null && comp.getFuzzVectorClass().equals(oldVector.getClass()))
			{
				composite.setVisible(true);
				comp.displayFuzzVector(oldVector);
				vectorTypeCombo.setText(comp.getDescription());
			}
			else
			{
				composite.setVisible(false);
			}
			composite.setLayoutData(fuzzCompositeLayout);
		}
	}


	private void initGUI(FuzzVector oldVector) 
	{
			dialogShell = new GShell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
			dialogShell.setText(GrendelScan.versionText + " - Fuzz Vector");
			FillLayout dialogShellLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			dialogShell.setLayout(dialogShellLayout);
			GComposite dialogComposite = new GComposite(dialogShell, SWT.NONE);
			dialogComposite.setLayout(new FormLayout());
			{
				FormData vectorTypeComboLData = new FormData();
				vectorTypeComboLData.width = 215;
				vectorTypeComboLData.height = 25;
				vectorTypeComboLData.left =  new FormAttachment(0, 1000, 7);
				vectorTypeComboLData.top =  new FormAttachment(0, 1000, 7);
				vectorTypeCombo = new Combo(dialogComposite, SWT.READ_ONLY);
				vectorTypeCombo.setLayoutData(vectorTypeComboLData);
				vectorTypeCombo.addModifyListener(new ModifyListener() 
				{
					@Override
					public void modifyText(@SuppressWarnings("unused") ModifyEvent evt) 
					{
						GComposite composite = (GComposite) vectorTypeCombo.getData(vectorTypeCombo.getText());
						if (currentFuzzComposite != null)
						{
							currentFuzzComposite.setVisible(false);
						}
						composite.setVisible(true);
						currentFuzzComposite = composite;
					}
				});
			}
			{
				fuzzVectorGroup = new GGroup(dialogComposite, SWT.NONE);
				FormLayout fuzzVectorGroupLayout = new FormLayout();
				fuzzVectorGroup.setLayout(fuzzVectorGroupLayout);
				FormData fuzzVectorGroupLData = new FormData();
				fuzzVectorGroupLData.width = 701;
				fuzzVectorGroupLData.height = 361;
				fuzzVectorGroupLData.left =  new FormAttachment(0, 1000, 5);
				fuzzVectorGroupLData.top =  new FormAttachment(0, 1000, 45);
				fuzzVectorGroupLData.right =  new FormAttachment(1000, 1000, -5);
				fuzzVectorGroupLData.bottom =  new FormAttachment(1000, 1000, -42);
				fuzzVectorGroup.setLayoutData(fuzzVectorGroupLData);
				fuzzVectorGroup.setText("Fuzz Vector Definition");
				{
					fuzzCompositeLayout = new FormData();
					fuzzCompositeLayout.left =  new FormAttachment(0, 1000, 5);
					fuzzCompositeLayout.top =  new FormAttachment(0, 1000, 5);
					fuzzCompositeLayout.right =  new FormAttachment(1000, 1000, -5);
					fuzzCompositeLayout.bottom =  new FormAttachment(1000, 1000, -5);
				}
			}
			{
				okButton = new GButton(dialogComposite, SWT.PUSH | SWT.CENTER);
				FormData okButtonLData = new FormData();
				okButtonLData.width = 50;
				okButtonLData.height = 25;
				okButtonLData.left =  new FormAttachment(0, 1000, 5);
				okButtonLData.bottom =  new FormAttachment(1000, 1000, -5);
				okButton.setLayoutData(okButtonLData);
				okButton.setText("OK");
				okButton.addSelectionListener(new SelectionAdapter() 
				{
					@Override
					public void widgetSelected(@SuppressWarnings("unused") SelectionEvent evt) 
					{
						if (currentFuzzComposite == null)
						{
							fuzzVector = null;
    						dialogShell.close();
						}
						else
						{
							try
                            {
	                            fuzzVector = ((FuzzVectorComposite) currentFuzzComposite).getFuzzVector();
	    						dialogShell.close();
                            }
                            catch (FuzzVectorFormatException e)
                            {
                            	MessageBox m = new MessageBox(getParent().getShell());
                            	m.setText("Error");
                            	m.setMessage(e.toString());
                            	m.open();
                            }
						}
					}
				});
			}
			{
				cancelButton = new GButton(dialogComposite, SWT.PUSH | SWT.CENTER);
				FormData button1LData = new FormData();
				button1LData.width = 50;
				button1LData.height = 25;
				button1LData.left =  new FormAttachment(0, 1000, 67);
				button1LData.bottom =  new FormAttachment(1000, 1000, -5);
				cancelButton.setLayoutData(button1LData);
				cancelButton.setText("Cancel");
				cancelButton.addSelectionListener(new SelectionAdapter() 
				{
					@Override
					public void widgetSelected(@SuppressWarnings("unused") SelectionEvent evt) 
					{
						dialogShell.close();
					}
				});
			}
			initVectorComposites(oldVector);
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
